package com.sergio.certificado.gravador.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CertificateRegistryServiceTest {

	private final RegisteredCertificateRepository repository = mock(RegisteredCertificateRepository.class);
	private final CertificateRegistryService service = new CertificateRegistryService(repository);

	@Test
	void doesNotWriteAnEventAlreadyRegistered() {
		CertificateIssuedEvent event = newEvent();
		when(repository.existsById(event.eventId())).thenReturn(true);

		service.record(event);

		verify(repository, never()).saveAndFlush(isA(RegisteredCertificate.class));
	}

	@Test
	void processingTheSameMessageTwiceStoresOneRow() {
		CertificateIssuedEvent event = newEvent();
		when(repository.existsById(event.eventId())).thenReturn(false, true);

		service.record(event);
		service.record(event);

		verify(repository, times(1)).saveAndFlush(isA(RegisteredCertificate.class));
	}

	@Test
	void ignoresAConcurrentDuplicateAfterTheUniqueConstraintRejectsIt() {
		CertificateIssuedEvent event = newEvent();
		when(repository.existsById(event.eventId())).thenReturn(false, true);
		when(repository.saveAndFlush(isA(RegisteredCertificate.class)))
				.thenThrow(new DataIntegrityViolationException("duplicate event"));

		assertDoesNotThrow(() -> service.record(event));
	}

	@Test
	void propagatesAnIntegrityFailureWhenTheEventWasNotStored() {
		CertificateIssuedEvent event = newEvent();
		when(repository.existsById(event.eventId())).thenReturn(false, false);
		when(repository.saveAndFlush(isA(RegisteredCertificate.class)))
				.thenThrow(new DataIntegrityViolationException("unrelated integrity failure"));

		assertThrows(DataIntegrityViolationException.class, () -> service.record(event));
	}

	@Test
	void findsCertificateById() {
		UUID certificateId = UUID.randomUUID();
		RegisteredCertificate certificate = newCertificate(certificateId);
		when(repository.findByCertificateId(certificateId)).thenReturn(Optional.of(certificate));

		CertificateLookupResponse response = service.findByCertificateId(certificateId);

		assertEquals(certificateId, response.id());
		assertEquals(certificate.getValidationCode(), response.codigoValidacao());
	}

	@Test
	void doesNotFindCertificateByUnknownId() {
		UUID certificateId = UUID.randomUUID();
		when(repository.findByCertificateId(certificateId)).thenReturn(Optional.empty());

		assertThrows(CertificateNotFoundException.class, () -> service.findByCertificateId(certificateId));
	}

	private CertificateIssuedEvent newEvent() {
		return new CertificateIssuedEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				UUID.randomUUID().toString(),
				"Participante",
				"Curso",
				Instant.now());
	}

	private RegisteredCertificate newCertificate(UUID certificateId) {
		return new RegisteredCertificate(
				UUID.randomUUID(),
				certificateId,
				UUID.randomUUID().toString(),
				"Participante",
				"Curso",
				Instant.now());
	}
}
