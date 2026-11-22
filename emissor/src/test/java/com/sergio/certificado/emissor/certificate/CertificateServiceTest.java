package com.sergio.certificado.emissor.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import com.sergio.certificado.emissor.outbox.OutboxEvent;
import com.sergio.certificado.emissor.outbox.OutboxEventRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

class CertificateServiceTest {

	@Test
	void persistsCertificateAndPublishesItsEvent() {
		CertificateRepository repository = mock(CertificateRepository.class);
		OutboxEventRepository outboxRepository = mock(OutboxEventRepository.class);
		CertificateService service = new CertificateService(repository, outboxRepository);

		CertificateResponse response = service.issue(new CreateCertificateRequest("Ana Silva", "Introducao a APIs"));

		assertNotNull(response.id());
		assertNotNull(response.codigoValidacao());
		assertEquals("Ana Silva", response.participante());
		verify(repository).save(isA(Certificate.class));
		verify(outboxRepository).save(isA(OutboxEvent.class));
	}
}
