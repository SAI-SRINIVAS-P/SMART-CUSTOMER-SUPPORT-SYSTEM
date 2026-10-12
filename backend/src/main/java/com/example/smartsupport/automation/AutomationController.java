package com.example.smartsupport.automation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/automation")
public class AutomationController {
    private final AutomationService automation;

    public AutomationController(AutomationService automation) {
        this.automation = automation;
    }

    @GetMapping("/summary")
    public AutomationService.AutomationSummary summary() {
        return automation.summary();
    }

    @GetMapping("/notifications")
    public List<AutomationService.NotificationView> notifications() {
        return automation.notifications();
    }

    @GetMapping("/runs")
    public List<AutomationService.AutomationRunView> runs() {
        return automation.runs();
    }

    @PostMapping("/run")
    public AutomationService.AutomationRunView runNow() {
        return automation.runDelayedDeliveryCheck("MANUAL");
    }

    @PostMapping("/notifications/{id}/acknowledge")
    @ResponseStatus(HttpStatus.OK)
    public AutomationService.NotificationView acknowledge(@PathVariable long id) {
        return automation.acknowledge(id);
    }
}
