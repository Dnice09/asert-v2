package tz.go.mnrt.asert.modules.assessment.educationbackground.repository;

import tz.go.mnrt.asert.modules.assessment.educationbackground.entity.EducationBackground;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface EducationBackgroundRepository extends BaseRepository<EducationBackground, Long> {
    Optional<EducationBackground> findByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
}
