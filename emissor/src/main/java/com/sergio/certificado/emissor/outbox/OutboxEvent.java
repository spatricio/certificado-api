package com.sergio.certificado.emissor.outbox;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

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

	@Column(nullable = false)
	private Instant createdAt;

	private Instant publishedAt;

	protected OutboxEvent() {
	}

	public OutboxEvent(CertificateIssuedEvent event) {
		this.eventId = event.eventId();
		this.certificateId = event.certificateId();
		this.validationCode = event.validationCode();
		this.participant = event.participant();
		this.course = event.course();
		this.issuedAt = event.issuedAt();
		this.createdAt = Instant.now();
	}

	public UUID getEventId() {
		return eventId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public CertificateIssuedEvent toMessage() {
		return new CertificateIssuedEvent(eventId, certificateId, validationCode, participant, course, issuedAt);
	}
}
