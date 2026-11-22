package com.sergio.certificado.emissor.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.sergio.certificado.emissor.config.MessagingConfiguration.QUEUE_NAME;

@Component
public class OutboxPublisher {

	private static final Logger logger = LoggerFactory.getLogger(OutboxPublisher.class);
	private static final long CONFIRM_TIMEOUT_SECONDS = 10;

	private final OutboxEventRepository repository;
	private final RabbitTemplate rabbitTemplate;

	public OutboxPublisher(OutboxEventRepository repository, RabbitTemplate rabbitTemplate) {
		this.repository = repository;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Scheduled(fixedDelay = 1000)
	public void publishPendingEvents() {
		for (OutboxEvent event : repository.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()) {
			if (!publish(event)) {
				return;
			}
		}
	}

	private boolean publish(OutboxEvent event) {
		CorrelationData correlation = new CorrelationData(event.getEventId().toString());
		try {
			rabbitTemplate.convertAndSend(QUEUE_NAME, event.toMessage(), correlation);
			CorrelationData.Confirm confirm = correlation.getFuture()
					.get(CONFIRM_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			if (!confirm.ack()) {
				throw new IllegalStateException("RabbitMQ rejeitou a publicacao: " + confirm.reason());
			}
			if (correlation.getReturned() != null) {
				throw new IllegalStateException("RabbitMQ nao encontrou a fila de destino.");
			}

			repository.markPublished(event.getEventId(), Instant.now());
			return true;
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			logger.warn("Publicacao interrompida; evento {} continua pendente no outbox.", event.getEventId(), exception);
			return false;
		} catch (ExecutionException | TimeoutException | RuntimeException exception) {
			logger.warn("Falha ao publicar evento {}; ele sera tentado novamente.", event.getEventId(), exception);
			return false;
		}
	}
}
