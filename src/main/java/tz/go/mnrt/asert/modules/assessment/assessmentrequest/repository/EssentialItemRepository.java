package tz.go.mnrt.asert.modules.assessment.assessmentrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.EssentialItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EssentialItemRepository extends JpaRepository<EssentialItem, Long>, JpaSpecificationExecutor<EssentialItem> {

    Optional<EssentialItem> findByUuid(UUID uuid);

    List<EssentialItem> findByAssessmentRequest_Id(Long assessmentRequestId);

    List<EssentialItem> findByAssessmentRequest_UuidOrderByItemNo(UUID assessmentRequestUuid);

    @Query("SELECT ei FROM EssentialItem ei WHERE ei.assessmentRequest.id = :assessmentRequestId AND ei.compliance = :compliance")
    List<EssentialItem> findByAssessmentRequestIdAndCompliance(@Param("assessmentRequestId") Long assessmentRequestId, 
                                                             @Param("compliance") String compliance);

    @Query("SELECT ei FROM EssentialItem ei WHERE ei.assessmentRequest.id = :assessmentRequestId AND ei.evidenceProvided = true")
    List<EssentialItem> findByAssessmentRequestIdAndEvidenceProvided(@Param("assessmentRequestId") Long assessmentRequestId);

    @Query("SELECT COUNT(ei) FROM EssentialItem ei WHERE ei.assessmentRequest.id = :assessmentRequestId AND ei.compliance = 'compliant'")
    long countCompliantByAssessmentRequestId(@Param("assessmentRequestId") Long assessmentRequestId);

    @Query("SELECT COUNT(ei) FROM EssentialItem ei WHERE ei.assessmentRequest.id = :assessmentRequestId AND ei.compliance = 'non-compliant'")
    long countNonCompliantByAssessmentRequestId(@Param("assessmentRequestId") Long assessmentRequestId);

    @Query("SELECT COUNT(ei) FROM EssentialItem ei WHERE ei.assessmentRequest.id = :assessmentRequestId AND (ei.compliance IS NULL OR ei.compliance = '')")
    long countPendingByAssessmentRequestId(@Param("assessmentRequestId") Long assessmentRequestId);

    @Query("SELECT ei FROM EssentialItem ei WHERE ei.assessmentRequest.id = :assessmentRequestId ORDER BY ei.itemNo")
    List<EssentialItem> findByAssessmentRequestIdOrderByItemNo(@Param("assessmentRequestId") Long assessmentRequestId);

    boolean existsByAssessmentRequest_IdAndItemNo(Long assessmentRequestId, Integer itemNo);
}