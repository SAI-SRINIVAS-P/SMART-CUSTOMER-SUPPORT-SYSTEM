package com.example.smartsupport.automation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AutomationRunRepository extends JpaRepository<AutomationRunEntity, Long> {
    List<AutomationRunEntity> findTop20ByOrderByStartedAtDesc();
    Optional<AutomationRunEntity> findFirstByOrderByStartedAtDesc();
}
