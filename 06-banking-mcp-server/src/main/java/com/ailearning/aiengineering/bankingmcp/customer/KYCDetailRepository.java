package com.ailearning.aiengineering.bankingmcp.customer;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KYCDetailRepository extends JpaRepository<KYCDetail, Long>{
    Optional<KYCDetail> findFirstByUserIdAndStatusOrderBySubmittedAtDesc(String userId, String status);

    Optional<KYCDetail> findByUserId(String userId);

}
