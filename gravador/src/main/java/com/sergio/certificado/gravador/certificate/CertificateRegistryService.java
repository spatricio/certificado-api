package com.sergio.certificado.gravador.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CertificateRegistryService {

	private final RegisteredCertificateRepository repository;

	public CertificateRegistryService(RegisteredCertificateRepository repository) {
		this.repository = repository;
	}

	@CacheEvict(cacheNames = "certificados", key = "#event.validationCode")
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

	@Cacheable(cacheNames = "certificados", key = "#validationCode")
	public CertificateLookupResponse findByValidationCode(String validationCode) {
		RegisteredCertificate certificate = repository.findByValidationCode(validationCode)
				.orElseThrow(CertificateNotFoundException::new);
		return new CertificateLookupResponse(
				certificate.getCertificateId(),
				certificate.getValidationCode(),
				certificate.getParticipant(),
				certificate.getCourse(),
				certificate.getIssuedAt());
	}
}
