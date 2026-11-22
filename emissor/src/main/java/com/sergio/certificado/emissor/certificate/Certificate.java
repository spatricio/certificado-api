package com.sergio.certificado.emissor.certificate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "certificates")
public class Certificate {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true)
	private String validationCode;

	@Column(nullable = false)
	private String participant;

	@Column(nullable = false)
	private String course;

	@Column(nullable = false)
	private Instant issuedAt;

	protected Certificate() {
	}

	Certificate(UUID id, String validationCode, String participant, String course, Instant issuedAt) {
		this.id = id;
		this.validationCode = validationCode;
		this.participant = participant;
		this.course = course;
		this.issuedAt = issuedAt;
	}

	public UUID getId() {
		return id;
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
