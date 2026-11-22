package com.sergio.certificado.emissor.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import com.sergio.certificado.emissor.outbox.OutboxEvent;
import com.sergio.certificado.emissor.outbox.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CertificateService {

	private final CertificateRepository repository;
	private final OutboxEventRepository outboxRepository;

	public CertificateService(CertificateRepository repository, OutboxEventRepository outboxRepository) {
		this.repository = repository;
		this.outboxRepository = outboxRepository;
	}

	@Transactional
	public CertificateResponse issue(CreateCertificateRequest request) {
		Instant issuedAt = Instant.now();
		UUID certificateId = UUID.randomUUID();
		String validationCode = UUID.randomUUID().toString();
		Certificate certificate = new Certificate(
				certificateId, validationCode, request.participante(), request.curso(), issuedAt);

		repository.save(certificate);
		CertificateIssuedEvent event = new CertificateIssuedEvent(
				UUID.randomUUID(),
				certificateId,
				validationCode,
				request.participante(),
				request.curso(),
				issuedAt);
		outboxRepository.save(new OutboxEvent(event));

		return toResponse(certificate);
	}

	private CertificateResponse toResponse(Certificate certificate) {
		return new CertificateResponse(
				certificate.getId(),
				certificate.getValidationCode(),
				certificate.getParticipant(),
				certificate.getCourse(),
				certificate.getIssuedAt());
	}
}
