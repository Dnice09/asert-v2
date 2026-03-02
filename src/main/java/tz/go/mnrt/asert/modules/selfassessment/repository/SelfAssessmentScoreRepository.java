package tz.go.mnrt.asert.modules.selfassessment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentScore;

@Repository
public interface SelfAssessmentScoreRepository extends JpaRepository<SelfAssessmentScore, Long> {

    List<SelfAssessmentScore> findBySelfAssessmentIdAndIsDeletedFalse(Long selfAssessmentId);

    @Query("SELECT sas FROM SelfAssessmentScore sas " +
           "LEFT JOIN FETCH sas.section " +
           "WHERE sas.selfAssessment.id = :selfAssessmentId " +
           "AND sas.isDeleted = false")
    List<SelfAssessmentScore> findBySelfAssessmentIdWithSection(@Param("selfAssessmentId") Long selfAssessmentId);

    @Query("SELECT sas FROM SelfAssessmentScore sas " +
           "WHERE sas.selfAssessment.uuid = :selfAssessmentUuid " +
           "AND sas.section.uuid = :sectionUuid " +
           "AND sas.isDeleted = false")
    SelfAssessmentScore findBySelfAssessmentUuidAndSectionUuid(
            @Param("selfAssessmentUuid") UUID selfAssessmentUuid,
            @Param("sectionUuid") UUID sectionUuid);

    @Modifying
    @Query("DELETE FROM SelfAssessmentScore sas WHERE sas.selfAssessment.id = :selfAssessmentId")
    void deleteBySelfAssessmentId(@Param("selfAssessmentId") Long selfAssessmentId);
}
