package tz.go.mnrt.asert.modules.form.formfieldoption.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.formfieldoption.entity.FormFieldOption;

@Repository
public interface FormFieldOptionRepository extends BaseRepository<FormFieldOption, Long> {
    Optional<FormFieldOption> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    List<FormFieldOption> findByFieldId(Long fieldId);

    void deleteByFieldId(Long fieldId);
}
