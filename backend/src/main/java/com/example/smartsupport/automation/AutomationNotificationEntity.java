package com.example.smartsupport.automation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "automation_notifications", uniqueConstraints = {
    @UniqueConstraint(name = "uk_automation_notification_event_key", columnNames = "event_key")
})
public class AutomationNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_key", nullable = false, unique = true, length = 120)
    private String eventKey;

    @Column(nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "notification_type", nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acknowledgedAt;

    protected AutomationNotificationEntity() {}

    public AutomationNotificationEntity(String eventKey, String orderNumber, String type,
                                        String title, String message) {
        this.eventKey = eventKey;
        this.orderNumber = orderNumber;
        this.type = type;
        this.title = title;
        this.message = message;
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now();
    }

    public void acknowledge() {
        if ("OPEN".equals(status)) {
            status = "ACKNOWLEDGED";
            acknowledgedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getEventKey() { return eventKey; }
    public String getOrderNumber() { return orderNumber; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
}
