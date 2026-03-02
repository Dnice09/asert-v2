package tz.go.mnrt.asert.modules.form.formsubmissionscore.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.entity.FormSubmissionScore;

@Repository
public interface FormSubmissionScoreRepository extends BaseRepository<FormSubmissionScore, Long> {
    Optional<FormSubmissionScore> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    List<FormSubmissionScore> findBySubmissionId(Long submissionId);

    List<FormSubmissionScore> findBySectionId(Long sectionId);

    Optional<FormSubmissionScore> findBySubmissionIdAndSectionId(Long submissionId, Long sectionId);

    void deleteBySubmissionId(Long submissionId);

    // Check if any scores exist for a section
    boolean existsBySectionId(Long sectionId);

    // Check if any scores exist for multiple sections
    @Query("SELECT s.section.id FROM FormSubmissionScore s WHERE s.section.id IN :sectionIds")
    List<Long> findSectionIdsWithScores(@Param("sectionIds") List<Long> sectionIds);
}
