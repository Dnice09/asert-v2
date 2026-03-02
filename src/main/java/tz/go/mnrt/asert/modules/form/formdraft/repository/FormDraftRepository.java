package tz.go.mnrt.asert.modules.form.formdraft.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.formdraft.entity.FormDraft;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormDraftRepository extends BaseRepository<FormDraft, Long> {
    Optional<FormDraft> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Query("SELECT fd FROM FormDraft fd WHERE fd.form.uuid = :formUuid AND LOWER(fd.submittedBy) = LOWER(:submittedBy) AND fd.isDeleted = false")
    Optional<FormDraft> findByFormUuidAndSubmittedBy(@Param("formUuid") UUID formUuid,
            @Param("submittedBy") String submittedBy);

    @Query("SELECT fd FROM FormDraft fd WHERE fd.form.uuid = :formUuid AND fd.hotel.uuid = :hotelUuid AND LOWER(fd.submittedBy) = LOWER(:submittedBy) AND fd.isDeleted = false")
    Optional<FormDraft> findByFormUuidAndHotelUuidAndSubmittedBy(@Param("formUuid") UUID formUuid,
            @Param("hotelUuid") UUID hotelUuid, @Param("submittedBy") String submittedBy);

    @Query("SELECT fd FROM FormDraft fd WHERE LOWER(fd.submittedBy) = LOWER(:submittedBy) AND fd.isDeleted = false ORDER BY fd.lastSavedAt DESC")
    List<FormDraft> findBySubmittedByOrderByLastSavedAtDesc(@Param("submittedBy") String submittedBy);

    @Query("SELECT fd FROM FormDraft fd WHERE fd.hotel.uuid = :hotelUuid AND fd.isDeleted = false ORDER BY fd.lastSavedAt DESC")
    List<FormDraft> findByHotelUuidOrderByLastSavedAtDesc(@Param("hotelUuid") UUID hotelUuid);
}
