package com.edenred.taggy_sustain.repository;

import com.edenred.taggy_sustain.model.CalculoImpactoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculoImpactoLogRepository extends JpaRepository<CalculoImpactoLog, Long> {
}
