package com.sergio.certificado.gravador.messaging;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import com.sergio.certificado.gravador.certificate.CertificateRegistryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.sergio.certificado.gravador.config.MessagingConfiguration.QUEUE_NAME;

@Component
public class CertificateIssuedListener {

	private final CertificateRegistryService service;

	public CertificateIssuedListener(CertificateRegistryService service) {
		this.service = service;
	}

	@RabbitListener(queues = QUEUE_NAME)
	public void handle(CertificateIssuedEvent event) {
		service.record(event);
	}
}
