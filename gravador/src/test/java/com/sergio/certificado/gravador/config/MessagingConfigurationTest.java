package com.sergio.certificado.gravador.config;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagingConfigurationTest {

	@Test
	void convertsTheCertificateEventToJsonAndBack() {
		CertificateIssuedEvent event = new CertificateIssuedEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				UUID.randomUUID().toString(),
				"Participante",
				"Curso",
				Instant.parse("2026-01-01T10:00:00Z"));
		MessageConverter converter = new MessagingConfiguration().messageConverter();

		Message message = converter.toMessage(event, new MessageProperties());
		Object convertedEvent = converter.fromMessage(message);

		assertEquals(event, convertedEvent);
	}
}
