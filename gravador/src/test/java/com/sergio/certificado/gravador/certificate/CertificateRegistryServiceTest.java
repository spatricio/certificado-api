package com.sergio.certificado.gravador.certificate;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CertificateRegistryServiceTest {

	private final RegisteredCertificateRepository repository = mock(RegisteredCertificateRepository.class);
	private final CertificateRegistryService service = new CertificateRegistryService(repository);

	@Test
	void doesNotWriteAnEventAlreadyRegistered() {
		CertificateIssuedEvent event = newEvent();
		when(repository.existsById(event.eventId())).thenReturn(true);

		service.record(event);

		verify(repository, never()).saveAndFlush(isA(RegisteredCertificate.class));
	}

	@Test
	void ignoresAConcurrentDuplicateAfterTheUniqueConstraintRejectsIt() {
		CertificateIssuedEvent event = newEvent();
		when(repository.existsById(event.eventId())).thenReturn(false, true);
		when(repository.saveAndFlush(isA(RegisteredCertificate.class)))
				.thenThrow(new DataIntegrityViolationException("duplicate event"));

		assertDoesNotThrow(() -> service.record(event));
	}

	@Test
	void propagatesAnIntegrityFailureWhenTheEventWasNotStored() {
		CertificateIssuedEvent event = newEvent();
		when(repository.existsById(event.eventId())).thenReturn(false, false);
		when(repository.saveAndFlush(isA(RegisteredCertificate.class)))
				.thenThrow(new DataIntegrityViolationException("unrelated integrity failure"));

		assertThrows(DataIntegrityViolationException.class, () -> service.record(event));
	}

	private CertificateIssuedEvent newEvent() {
		return new CertificateIssuedEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				UUID.randomUUID().toString(),
				"Participante",
				"Curso",
				Instant.now());
	}
}
