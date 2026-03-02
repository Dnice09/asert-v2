package tz.go.mnrt.asert.modules.assessment.assessor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorCertificate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessorCertificateRepository extends JpaRepository<AssessorCertificate, Long> {

    Optional<AssessorCertificate> findByUuid(UUID uuid);

    Page<AssessorCertificate> findByAssessorId(Long assessorId, Pageable pageable);

    List<AssessorCertificate> findByAssessorIdAndIsActiveTrue(Long assessorId);

    List<AssessorCertificate> findByAssessorUuid(UUID assessorUuid);

    @Query("SELECT ac FROM AssessorCertificate ac WHERE ac.assessor.id = :assessorId AND ac.verificationStatus = :status")
    List<AssessorCertificate> findByAssessorIdAndVerificationStatus(@Param("assessorId") Long assessorId, 
                                                                   @Param("status") String status);

    @Query("SELECT ac FROM AssessorCertificate ac WHERE ac.expiryDate IS NOT NULL AND ac.expiryDate < :date AND ac.isActive = true")
    List<AssessorCertificate> findExpiredCertificates(@Param("date") LocalDate date);

    @Query("SELECT ac FROM AssessorCertificate ac WHERE ac.expiryDate IS NOT NULL AND ac.expiryDate BETWEEN :startDate AND :endDate AND ac.isActive = true")
    List<AssessorCertificate> findCertificatesExpiringSoon(@Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT ac FROM AssessorCertificate ac WHERE ac.certificateType = :type AND ac.isActive = true")
    List<AssessorCertificate> findByCertificateType(@Param("type") String type);

    @Query("SELECT COUNT(ac) FROM AssessorCertificate ac WHERE ac.assessor.id = :assessorId AND ac.verificationStatus = 'VERIFIED' AND ac.isActive = true")
    Long countVerifiedCertificatesByAssessorId(@Param("assessorId") Long assessorId);

    boolean existsByAssessorIdAndCertificateNameIgnoreCaseAndIsActiveTrue(Long assessorId, String certificateName);
}