package com.sergio.certificado.emissor.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

	List<OutboxEvent> findTop50ByPublishedAtIsNullOrderByCreatedAtAsc();

	@Modifying
	@Transactional
	@Query("""
			update OutboxEvent outboxEvent
			set outboxEvent.publishedAt = :publishedAt
			where outboxEvent.eventId = :eventId and outboxEvent.publishedAt is null
			""")
	int markPublished(@Param("eventId") UUID eventId, @Param("publishedAt") Instant publishedAt);
}
