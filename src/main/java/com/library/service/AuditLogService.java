package com.library.service;

import com.library.kafka.producer.AuditLogProducer;
import com.library.model.AuditLog;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogProducer auditLogProducer;

    public AuditLogService(AuditLogProducer auditLogProducer) {
        this.auditLogProducer = auditLogProducer;
    }

    public void log(String serviceName,
                    String httpMethod,
                    String requestUrl,
                    String requestBody,
                    String responseBody,
                    int httpStatusCode,
                    String errorCode,
                    boolean success) {
        AuditLog auditLog = new AuditLog(
                serviceName,
                httpMethod,
                requestUrl,
                requestBody,
                responseBody,
                httpStatusCode,
                errorCode,
                success
        );
        auditLogProducer.send(auditLog);
    }
}
