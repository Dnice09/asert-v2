package tz.go.mnrt.asert.modules.selfassessment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentDraft;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SelfAssessmentDraftRepository extends BaseRepository<SelfAssessmentDraft, Long> {
    Optional<SelfAssessmentDraft> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Query("SELECT sd FROM SelfAssessmentDraft sd WHERE sd.form.uuid = :formUuid AND sd.submittedBy = :submittedBy AND sd.isDeleted = false")
    Optional<SelfAssessmentDraft> findByFormUuidAndSubmittedBy(@Param("formUuid") UUID formUuid, @Param("submittedBy") String submittedBy);

    @Query("SELECT sd FROM SelfAssessmentDraft sd WHERE sd.form.uuid = :formUuid AND sd.hotel.uuid = :hotelUuid AND sd.submittedBy = :submittedBy AND sd.isDeleted = false")
    Optional<SelfAssessmentDraft> findByFormUuidAndHotelUuidAndSubmittedBy(@Param("formUuid") UUID formUuid, @Param("hotelUuid") UUID hotelUuid, @Param("submittedBy") String submittedBy);

    @Query("SELECT sd FROM SelfAssessmentDraft sd WHERE sd.submittedBy = :submittedBy AND sd.isDeleted = false ORDER BY sd.lastSavedAt DESC")
    List<SelfAssessmentDraft> findBySubmittedByOrderByLastSavedAtDesc(@Param("submittedBy") String submittedBy);

    @Query("SELECT sd FROM SelfAssessmentDraft sd WHERE sd.hotel.uuid = :hotelUuid AND sd.isDeleted = false ORDER BY sd.lastSavedAt DESC")
    List<SelfAssessmentDraft> findByHotelUuidOrderByLastSavedAtDesc(@Param("hotelUuid") UUID hotelUuid);
}
