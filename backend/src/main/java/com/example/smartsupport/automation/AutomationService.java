package com.example.smartsupport.automation;

import com.example.smartsupport.order.OrderEntity;
import com.example.smartsupport.order.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AutomationService {
    private final OrderRepository orders;
    private final AutomationNotificationRepository notifications;
    private final AutomationRunRepository runs;
    private final long demoCustomerId;
    private final boolean scheduledEnabled;
    private final long scanIntervalMs;

    public AutomationService(
        OrderRepository orders,
        AutomationNotificationRepository notifications,
        AutomationRunRepository runs,
        @Value("${app.demo-customer-id:1}") long demoCustomerId,
        @Value("${app.automation.scheduled-enabled:true}") boolean scheduledEnabled,
        @Value("${app.automation.scan-interval-ms:300000}") long scanIntervalMs
    ) {
        this.orders = orders;
        this.notifications = notifications;
        this.runs = runs;
        this.demoCustomerId = demoCustomerId;
        this.scheduledEnabled = scheduledEnabled;
        this.scanIntervalMs = scanIntervalMs;
    }

    @Transactional
    public synchronized AutomationRunView runDelayedDeliveryCheck(String triggerType) {
        AutomationRunEntity run = runs.save(new AutomationRunEntity(triggerType));
        List<OrderEntity> allOrders = orders.findAllByCustomerIdOrderByIdDesc(demoCustomerId);
        LocalDate today = LocalDate.now();
        List<OrderEntity> overdue = allOrders.stream()
            .filter(order -> order.getEstimatedDelivery() != null)
            .filter(order -> order.getEstimatedDelivery().isBefore(today))
            .filter(order -> Set.of("PROCESSING", "SHIPPED").contains(order.getStatus().toUpperCase(Locale.ROOT)))
            .toList();

        int created = 0;
        for (OrderEntity order : overdue) {
            String eventKey = "LATE_DELIVERY:" + order.getOrderNumber() + ":" + order.getEstimatedDelivery();
            if (notifications.existsByEventKey(eventKey)) {
                continue;
            }
            String title = "Delivery date passed: " + order.getOrderNumber();
            String message = "Order " + order.getOrderNumber() + " is " + order.getStatus()
                + ". Its estimated delivery date (" + order.getEstimatedDelivery()
                + ") has passed. Review the shipment and contact the customer if needed.";
            notifications.saveAndFlush(new AutomationNotificationEntity(
                eventKey, order.getOrderNumber(), "LATE_DELIVERY", title, message));
            created++;
        }

        run.complete(allOrders.size(), overdue.size(), created);
        runs.save(run); // Keep the completed totals persisted for both manual and scheduled runs.
        return view(run);
    }

    @Transactional(readOnly = true)
    public AutomationSummary summary() {
        AutomationRunView lastRun = runs.findFirstByOrderByStartedAtDesc().map(this::view).orElse(null);
        return new AutomationSummary(
            scheduledEnabled,
            Math.max(1, scanIntervalMs / 60_000),
            notifications.countByStatus("OPEN"),
            notifications.count(),
            lastRun
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationView> notifications() {
        return notifications.findTop100ByOrderByCreatedAtDesc().stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public List<AutomationRunView> runs() {
        return runs.findTop20ByOrderByStartedAtDesc().stream().map(this::view).toList();
    }

    @Transactional
    public NotificationView acknowledge(long id) {
        AutomationNotificationEntity notification = notifications.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Automation notification not found"));
        notification.acknowledge();
        return view(notifications.save(notification));
    }

    private NotificationView view(AutomationNotificationEntity item) {
        return new NotificationView(item.getId(), item.getOrderNumber(), item.getType(), item.getTitle(),
            item.getMessage(), item.getStatus(), item.getCreatedAt(), item.getAcknowledgedAt());
    }

    private AutomationRunView view(AutomationRunEntity run) {
        return new AutomationRunView(run.getId(), run.getTriggerType(), run.getStartedAt(), run.getFinishedAt(),
            run.getScannedOrders(), run.getOverdueOrders(), run.getNotificationsCreated(), run.getResult(), run.getDetails());
    }

    public record NotificationView(Long id, String orderNumber, String type, String title, String message,
                                   String status, LocalDateTime createdAt, LocalDateTime acknowledgedAt) {}
    public record AutomationRunView(Long id, String triggerType, LocalDateTime startedAt, LocalDateTime finishedAt,
                                    int scannedOrders, int overdueOrders, int notificationsCreated,
                                    String result, String details) {}
    public record AutomationSummary(boolean scheduledEnabled, long scanIntervalMinutes, long openNotifications,
                                    long totalNotifications, AutomationRunView lastRun) {}
}
