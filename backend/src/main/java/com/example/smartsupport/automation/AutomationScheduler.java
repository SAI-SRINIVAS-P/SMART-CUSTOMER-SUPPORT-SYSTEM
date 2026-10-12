package com.example.smartsupport.automation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutomationScheduler {
    private final AutomationService automation;
    private final boolean enabled;

    public AutomationScheduler(
        AutomationService automation,
        @Value("${app.automation.scheduled-enabled:true}") boolean enabled
    ) {
        this.automation = automation;
        this.enabled = enabled;
    }

    @Scheduled(
        fixedDelayString = "${app.automation.scan-interval-ms:300000}",
        initialDelayString = "${app.automation.initial-delay-ms:30000}"
    )
    public void scanForOverdueDeliveries() {
        if (enabled) {
            automation.runDelayedDeliveryCheck("SCHEDULED");
        }
    }
}
