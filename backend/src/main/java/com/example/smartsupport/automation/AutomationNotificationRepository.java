package com.example.smartsupport.automation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutomationNotificationRepository extends JpaRepository<AutomationNotificationEntity, Long> {
    boolean existsByEventKey(String eventKey);
    List<AutomationNotificationEntity> findAllByOrderByCreatedAtDesc();
    List<AutomationNotificationEntity> findTop100ByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
