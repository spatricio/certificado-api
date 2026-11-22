package com.sergio.certificado.emissor.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static com.sergio.certificado.emissor.config.MessagingConfiguration.QUEUE_NAME;

@Service
public class CertificateService {

	private final CertificateRepository repository;
	private final RabbitTemplate rabbitTemplate;

	public CertificateService(CertificateRepository repository, RabbitTemplate rabbitTemplate) {
		this.repository = repository;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Transactional
	public CertificateResponse issue(CreateCertificateRequest request) {
		Instant issuedAt = Instant.now();
		UUID certificateId = UUID.randomUUID();
		String validationCode = UUID.randomUUID().toString();
		Certificate certificate = new Certificate(
				certificateId, validationCode, request.participante(), request.curso(), issuedAt);

		repository.save(certificate);
		rabbitTemplate.convertAndSend(QUEUE_NAME, new CertificateIssuedEvent(
				UUID.randomUUID(),
				certificateId,
				validationCode,
				request.participante(),
				request.curso(),
				issuedAt));

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
