package com.sergio.certificado.contract;

import java.time.Instant;
import java.util.UUID;

public record CertificateIssuedEvent(
		UUID eventId,
		UUID certificateId,
		String validationCode,
		String participant,
		String course,
		Instant issuedAt) {
}
