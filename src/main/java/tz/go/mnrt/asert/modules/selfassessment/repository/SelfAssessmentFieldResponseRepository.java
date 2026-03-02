package tz.go.mnrt.asert.modules.selfassessment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentFieldResponse;

@Repository
public interface SelfAssessmentFieldResponseRepository extends JpaRepository<SelfAssessmentFieldResponse, Long> {

    List<SelfAssessmentFieldResponse> findBySelfAssessmentIdAndIsDeletedFalse(Long selfAssessmentId);

    @Query("SELECT safr FROM SelfAssessmentFieldResponse safr " +
           "LEFT JOIN FETCH safr.field " +
           "WHERE safr.selfAssessment.id = :selfAssessmentId " +
           "AND safr.isDeleted = false")
    List<SelfAssessmentFieldResponse> findBySelfAssessmentIdWithField(@Param("selfAssessmentId") Long selfAssessmentId);

    @Query("SELECT safr FROM SelfAssessmentFieldResponse safr " +
           "WHERE safr.selfAssessment.uuid = :selfAssessmentUuid " +
           "AND safr.field.uuid = :fieldUuid " +
           "AND safr.isDeleted = false")
    List<SelfAssessmentFieldResponse> findBySelfAssessmentUuidAndFieldUuid(
            @Param("selfAssessmentUuid") UUID selfAssessmentUuid,
            @Param("fieldUuid") UUID fieldUuid);

    @Modifying
    @Query("DELETE FROM SelfAssessmentFieldResponse safr WHERE safr.selfAssessment.id = :selfAssessmentId")
    void deleteBySelfAssessmentId(@Param("selfAssessmentId") Long selfAssessmentId);
}
