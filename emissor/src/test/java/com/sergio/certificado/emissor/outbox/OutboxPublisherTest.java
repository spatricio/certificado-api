package com.sergio.certificado.emissor.outbox;

import com.sergio.certificado.contract.CertificateIssuedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.sergio.certificado.emissor.config.MessagingConfiguration.QUEUE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxPublisherTest {

	private final OutboxEventRepository repository = mock(OutboxEventRepository.class);
	private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
	private final OutboxPublisher publisher = new OutboxPublisher(repository, rabbitTemplate);

	@Test
	void marksAnEventPublishedOnlyAfterTheBrokerConfirmsIt() {
		OutboxEvent event = newEvent();
		when(repository.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of(event));
		doAnswer(invocation -> {
			CorrelationData correlation = invocation.getArgument(2);
			correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
			return null;
		}).when(rabbitTemplate).convertAndSend(
				anyString(), any(CertificateIssuedEvent.class), any(CorrelationData.class));

		publisher.publishPendingEvents();

		verify(repository).markPublished(eq(event.getEventId()), any(Instant.class));
		verify(rabbitTemplate).convertAndSend(
				eq(QUEUE_NAME), isA(CertificateIssuedEvent.class), isA(CorrelationData.class));
	}

	@Test
	void leavesAnEventPendingWhenTheBrokerRejectsIt() {
		OutboxEvent event = newEvent();
		when(repository.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of(event));
		doAnswer(invocation -> {
			CorrelationData correlation = invocation.getArgument(2);
			correlation.getFuture().complete(new CorrelationData.Confirm(false, "broker rejected"));
			return null;
		}).when(rabbitTemplate).convertAndSend(
				anyString(), any(CertificateIssuedEvent.class), any(CorrelationData.class));

		publisher.publishPendingEvents();

		verify(repository, never()).markPublished(eq(event.getEventId()), any(Instant.class));
	}

	private OutboxEvent newEvent() {
		return new OutboxEvent(new CertificateIssuedEvent(
				UUID.randomUUID(),
				UUID.randomUUID(),
				UUID.randomUUID().toString(),
				"Participante",
				"Curso",
				Instant.now()));
	}
}
