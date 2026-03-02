package tz.go.mnrt.asert.modules.assessment.assessor.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

public interface AssessorRepository extends BaseRepository<Assessor, Long> {
    Optional<Assessor> findByUuid(UUID uuid);

    Optional<Assessor> findByUserId(Long userId);

    void deleteByUuid(UUID uuid);

    @Query("SELECT a FROM Assessor a JOIN a.user u WHERE LOWER(u.email) = LOWER(:email) AND a.isDeleted = false")
    Optional<Assessor> findByUserEmail(@Param("email") String email);

    /**
     * Batch load assessors by user emails to avoid N+1 query problem.
     * Uses a single query to fetch multiple assessors at once.
     */
    @Query("SELECT a FROM Assessor a JOIN FETCH a.user u WHERE LOWER(u.email) IN :emails AND a.isDeleted = false")
    List<Assessor> findByUserEmailIn(@Param("emails") List<String> emails);

}
