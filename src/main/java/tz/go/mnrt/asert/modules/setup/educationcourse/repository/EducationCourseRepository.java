package tz.go.mnrt.asert.modules.setup.educationcourse.repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.educationcourse.entity.EducationCourse;

import java.util.Optional;
import java.util.UUID;

public interface EducationCourseRepository extends BaseRepository<EducationCourse, Long> {
    Optional<EducationCourse> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<EducationCourse> findByNameIgnoreCase(String name);
}
