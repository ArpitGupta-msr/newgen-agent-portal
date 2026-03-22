package com.newgen.otp.repository;

import com.newgen.otp.entity.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpRecord, Long> {

    Optional<OtpRecord> findTopByAgencyCodeOrderByCreatedAtDesc(String agencyCode);
}
