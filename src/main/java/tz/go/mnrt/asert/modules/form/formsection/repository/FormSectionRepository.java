package tz.go.mnrt.asert.modules.form.formsection.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;

public interface FormSectionRepository
        extends BaseRepository<FormSection, Long> {

    Optional<FormSection> findByUuid(UUID uuid);

    List<FormSection> findByFormId(Long formId);

    void deleteByUuid(UUID uuid);

    void deleteByFormId(Long formId);
}
