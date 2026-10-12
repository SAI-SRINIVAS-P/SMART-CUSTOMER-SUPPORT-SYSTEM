package com.example.smartsupport.automation;

import com.example.smartsupport.order.OrderEntity;
import com.example.smartsupport.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutomationServiceTest {
    @Mock OrderRepository orders;
    @Mock AutomationNotificationRepository notifications;
    @Mock AutomationRunRepository runs;

    private AutomationService service;

    @BeforeEach
    void setUp() {
        service = new AutomationService(orders, notifications, runs, 1L, true, 300_000L);
        when(runs.save(any(AutomationRunEntity.class))).thenAnswer(call -> call.getArgument(0));
    }

    @Test
    void createsInternalAlertForOverdueShippedOrder() {
        OrderEntity overdue = new OrderEntity("ORD-LATE-1", 1L, "SHIPPED", new BigDecimal("45.00"), LocalDate.now().minusDays(1));
        when(orders.findAllByCustomerIdOrderByIdDesc(1L)).thenReturn(List.of(overdue));
        when(notifications.existsByEventKey(anyString())).thenReturn(false);
        when(notifications.saveAndFlush(any(AutomationNotificationEntity.class)))
            .thenAnswer(call -> call.getArgument(0));

        AutomationService.AutomationRunView run = service.runDelayedDeliveryCheck("MANUAL");

        assertEquals("SUCCESS", run.result());
        assertEquals(1, run.scannedOrders());
        assertEquals(1, run.overdueOrders());
        assertEquals(1, run.notificationsCreated());
        ArgumentCaptor<AutomationNotificationEntity> alert = ArgumentCaptor.forClass(AutomationNotificationEntity.class);
        verify(notifications).saveAndFlush(alert.capture());
        assertEquals("ORD-LATE-1", alert.getValue().getOrderNumber());
        assertEquals("OPEN", alert.getValue().getStatus());
    }

    @Test
    void doesNotCreateDuplicateAlertOnAnotherScan() {
        OrderEntity overdue = new OrderEntity("ORD-LATE-2", 1L, "PROCESSING", new BigDecimal("45.00"), LocalDate.now().minusDays(2));
        when(orders.findAllByCustomerIdOrderByIdDesc(1L)).thenReturn(List.of(overdue));
        when(notifications.existsByEventKey(anyString())).thenReturn(true);

        AutomationService.AutomationRunView run = service.runDelayedDeliveryCheck("MANUAL");

        assertEquals(1, run.overdueOrders());
        assertEquals(0, run.notificationsCreated());
        verify(notifications, never()).saveAndFlush(any(AutomationNotificationEntity.class));
    }

    @Test
    void ignoresDeliveredOrdersEvenWhenEstimatedDateIsPast() {
        OrderEntity delivered = new OrderEntity("ORD-DONE-1", 1L, "DELIVERED", new BigDecimal("45.00"), LocalDate.now().minusDays(4));
        when(orders.findAllByCustomerIdOrderByIdDesc(1L)).thenReturn(List.of(delivered));

        AutomationService.AutomationRunView run = service.runDelayedDeliveryCheck("MANUAL");

        assertEquals(1, run.scannedOrders());
        assertEquals(0, run.overdueOrders());
        assertEquals(0, run.notificationsCreated());
        verify(notifications, never()).saveAndFlush(any(AutomationNotificationEntity.class));
    }
}
