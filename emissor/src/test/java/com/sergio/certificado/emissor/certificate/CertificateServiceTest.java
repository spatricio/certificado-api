package com.sergio.certificado.emissor.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.sergio.certificado.emissor.config.MessagingConfiguration.QUEUE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

class CertificateServiceTest {

	@Test
	void persistsCertificateAndPublishesItsEvent() {
		CertificateRepository repository = mock(CertificateRepository.class);
		RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
		CertificateService service = new CertificateService(repository, rabbitTemplate);

		CertificateResponse response = service.issue(new CreateCertificateRequest("Ana Silva", "Introducao a APIs"));

		assertNotNull(response.id());
		assertNotNull(response.codigoValidacao());
		assertEquals("Ana Silva", response.participante());
		verify(repository).save(isA(Certificate.class));
		verify(rabbitTemplate).convertAndSend(eq(QUEUE_NAME), isA(CertificateIssuedEvent.class));
	}
}
