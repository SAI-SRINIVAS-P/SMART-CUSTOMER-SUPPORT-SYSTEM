package com.example.smartsupport.automation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "automation_runs")
public class AutomationRunEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String triggerType;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @Column(nullable = false)
    private int scannedOrders;

    @Column(nullable = false)
    private int overdueOrders;

    @Column(nullable = false)
    private int notificationsCreated;

    @Column(nullable = false, length = 20)
    private String result;

    @Column(length = 1000)
    private String details;

    protected AutomationRunEntity() {}

    public AutomationRunEntity(String triggerType) {
        this.triggerType = triggerType;
        this.startedAt = LocalDateTime.now();
        this.result = "RUNNING";
    }

    public void complete(int scannedOrders, int overdueOrders, int notificationsCreated) {
        this.scannedOrders = scannedOrders;
        this.overdueOrders = overdueOrders;
        this.notificationsCreated = notificationsCreated;
        this.result = "SUCCESS";
        this.details = "Scan completed. Existing notifications were not duplicated.";
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        this.result = "FAILED";
        this.details = reason == null ? "Automation run failed." : reason.substring(0, Math.min(reason.length(), 1000));
        this.finishedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTriggerType() { return triggerType; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public int getScannedOrders() { return scannedOrders; }
    public int getOverdueOrders() { return overdueOrders; }
    public int getNotificationsCreated() { return notificationsCreated; }
    public String getResult() { return result; }
    public String getDetails() { return details; }
}
