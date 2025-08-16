package com.library.kafka.consumer;

import com.library.model.AuditLog;
import com.library.repository.AuditLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditLogConsumer {

    private final AuditLogRepository auditLogRepository;

    public AuditLogConsumer(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @KafkaListener(topics = "audit-logs", groupId = "audit-log-consumer-group")
    public void consume(AuditLog auditLog) {
        System.out.println("📥 Received from Kafka: " + auditLog);
        auditLogRepository.save(auditLog);
    }
}
