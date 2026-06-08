package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Information about the API/service
    private String serviceName;
    private String httpMethod;
    private String requestUrl;

    // Request & Response payloads (stored as text/JSON)
    @Lob
    private String requestBody;

    @Lob
    private String responseBody;

    // Status info
    private int httpStatusCode;
    private String errorCode;

    private boolean success;

    private LocalDateTime timestamp;

    // Constructors
    public AuditLog() {}

    public AuditLog(String serviceName, String httpMethod, String requestUrl,
                    String requestBody, String responseBody,
                    int httpStatusCode, String errorCode, boolean success) {
        this.serviceName = serviceName;
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }
}
