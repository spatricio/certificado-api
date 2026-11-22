package com.sergio.certificado.gravador.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CertificateRegistryService {

	private final RegisteredCertificateRepository repository;

	public CertificateRegistryService(RegisteredCertificateRepository repository) {
		this.repository = repository;
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "certificados-por-codigo", key = "#event.validationCode"),
			@CacheEvict(cacheNames = "certificados-por-id", key = "#event.certificateId")
	})
	public void record(CertificateIssuedEvent event) {
		if (repository.existsById(event.eventId())) {
			return;
		}

		try {
			repository.saveAndFlush(new RegisteredCertificate(
					event.eventId(),
					event.certificateId(),
					event.validationCode(),
					event.participant(),
					event.course(),
					event.issuedAt()));
		} catch (DataIntegrityViolationException exception) {
			if (!repository.existsById(event.eventId())) {
				throw exception;
			}
		}
	}

	@Cacheable(cacheNames = "certificados-por-codigo", key = "#validationCode")
	public CertificateLookupResponse findByValidationCode(String validationCode) {
		RegisteredCertificate certificate = repository.findByValidationCode(validationCode)
				.orElseThrow(CertificateNotFoundException::new);
		return toResponse(certificate);
	}

	@Cacheable(cacheNames = "certificados-por-id", key = "#certificateId")
	public CertificateLookupResponse findByCertificateId(UUID certificateId) {
		RegisteredCertificate certificate = repository.findByCertificateId(certificateId)
				.orElseThrow(CertificateNotFoundException::new);
		return toResponse(certificate);
	}

	private CertificateLookupResponse toResponse(RegisteredCertificate certificate) {
		return new CertificateLookupResponse(
				certificate.getCertificateId(),
				certificate.getValidationCode(),
				certificate.getParticipant(),
				certificate.getCourse(),
				certificate.getIssuedAt());
	}
}
