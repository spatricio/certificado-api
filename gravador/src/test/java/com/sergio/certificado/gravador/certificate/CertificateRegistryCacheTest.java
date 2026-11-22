package com.sergio.certificado.gravador.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CertificateRegistryCacheTest.CacheTestConfiguration.class)
class CertificateRegistryCacheTest {

	@Autowired
	private RegisteredCertificateRepository repository;

	@Autowired
	private CertificateRegistryService service;

	@Autowired
	private CacheManager cacheManager;

	@Test
	void cachesSuccessfulLookupByCertificateId() {
		UUID certificateId = UUID.randomUUID();
		RegisteredCertificate certificate = new RegisteredCertificate(
				UUID.randomUUID(),
				certificateId,
				UUID.randomUUID().toString(),
				"Participante",
				"Curso",
				Instant.now());
		when(repository.findByCertificateId(certificateId)).thenReturn(Optional.of(certificate));

		service.findByCertificateId(certificateId);
		service.findByCertificateId(certificateId);

		verify(repository, times(1)).findByCertificateId(certificateId);
	}

	@Test
	void doesNotCacheMissingCertificate() {
		UUID certificateId = UUID.randomUUID();
		when(repository.findByCertificateId(certificateId)).thenReturn(Optional.empty());

		assertThrows(CertificateNotFoundException.class, () -> service.findByCertificateId(certificateId));
		assertThrows(CertificateNotFoundException.class, () -> service.findByCertificateId(certificateId));

		verify(repository, times(2)).findByCertificateId(certificateId);
	}

	@Test
	void evictsCertificateCachesWhenAnEventIsRecorded() {
		CertificateIssuedEvent event = new CertificateIssuedEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				UUID.randomUUID().toString(),
				"Participante",
				"Curso",
				Instant.now());
		CertificateLookupResponse response = new CertificateLookupResponse(
				event.certificateId(),
				event.validationCode(),
				event.participant(),
				event.course(),
				event.issuedAt());
		cacheManager.getCache("certificados-por-id").put(event.certificateId(), response);
		cacheManager.getCache("certificados-por-codigo").put(event.validationCode(), response);

		when(repository.existsById(event.eventId())).thenReturn(false);

		service.record(event);

		assertNull(cacheManager.getCache("certificados-por-id").get(event.certificateId()));
		assertNull(cacheManager.getCache("certificados-por-codigo").get(event.validationCode()));
	}

	@Configuration
	@EnableCaching
	@Import(CertificateRegistryService.class)
	static class CacheTestConfiguration {

		@Bean
		RegisteredCertificateRepository registeredCertificateRepository() {
			return mock(RegisteredCertificateRepository.class);
		}

		@Bean
		CacheManager cacheManager() {
			return new ConcurrentMapCacheManager("certificados-por-id", "certificados-por-codigo");
		}
	}
}
