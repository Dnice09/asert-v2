package tz.go.mnrt.asert.modules.form.formfield.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;

public interface FormFieldRepository
        extends BaseRepository<FormField, Long> {

    Optional<FormField> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Query("SELECT f FROM FormField f WHERE f.section.id = :sectionId AND f.isDeleted = false")
    List<FormField> findBySectionId(@Param("sectionId") Long id);
}
