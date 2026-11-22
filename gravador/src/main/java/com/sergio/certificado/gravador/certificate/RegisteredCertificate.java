package com.sergio.certificado.gravador.certificate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "registered_certificates")
public class RegisteredCertificate {

	@Id
	private UUID eventId;

	@Column(nullable = false, unique = true)
	private UUID certificateId;

	@Column(nullable = false, unique = true)
	private String validationCode;

	@Column(nullable = false)
	private String participant;

	@Column(nullable = false)
	private String course;

	@Column(nullable = false)
	private Instant issuedAt;

	protected RegisteredCertificate() {
	}

	RegisteredCertificate(
			UUID eventId,
			UUID certificateId,
			String validationCode,
			String participant,
			String course,
			Instant issuedAt) {
		this.eventId = eventId;
		this.certificateId = certificateId;
		this.validationCode = validationCode;
		this.participant = participant;
		this.course = course;
		this.issuedAt = issuedAt;
	}

	public UUID getEventId() {
		return eventId;
	}

	public UUID getCertificateId() {
		return certificateId;
	}

	public String getValidationCode() {
		return validationCode;
	}

	public String getParticipant() {
		return participant;
	}

	public String getCourse() {
		return course;
	}

	public Instant getIssuedAt() {
		return issuedAt;
	}
}
