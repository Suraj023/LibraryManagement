package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    // Getters & Setters
    public Long getId() { return id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }

    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }

    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }

    public int getHttpStatusCode() { return httpStatusCode; }
    public void setHttpStatusCode(int httpStatusCode) { this.httpStatusCode = httpStatusCode; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
