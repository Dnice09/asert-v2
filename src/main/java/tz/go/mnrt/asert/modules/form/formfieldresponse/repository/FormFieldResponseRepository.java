package tz.go.mnrt.asert.modules.form.formfieldresponse.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.formfieldresponse.entity.FormFieldResponse;

@Repository
public interface FormFieldResponseRepository extends BaseRepository<FormFieldResponse, Long> {
    Optional<FormFieldResponse> findByUuid(UUID uuid);

    List<FormFieldResponse> findBySubmissionId(Long submissionId);

    void deleteBySubmissionId(Long submissionId);

    boolean existsByFieldId(Long id);

    boolean existsByFieldSectionId(Long id);

    // Check if any responses exist for multiple fields
    @Query("SELECT r.field.id FROM FormFieldResponse r WHERE r.field.id IN :fieldIds")
    List<Long> findFieldIdsWithResponses(@Param("fieldIds") List<Long> fieldIds);
}
