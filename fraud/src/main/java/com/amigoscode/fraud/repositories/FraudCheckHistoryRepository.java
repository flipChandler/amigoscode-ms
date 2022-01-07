package com.amigoscode.fraud.repositories;

import com.amigoscode.fraud.domains.FraudCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudCheckHistoryRepository extends JpaRepository<FraudCheckHistory, Integer> {
}