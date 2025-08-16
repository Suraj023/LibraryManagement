package com.library.kafka.producer;

import com.library.model.AuditLog;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditLogProducer {
    private static final String TOPIC = "audit-logs";
    private final KafkaTemplate<String, AuditLog> kafkaTemplate;

    public AuditLogProducer(KafkaTemplate<String, AuditLog> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(AuditLog auditLog) {
        kafkaTemplate.send(TOPIC, auditLog);
    }
}
