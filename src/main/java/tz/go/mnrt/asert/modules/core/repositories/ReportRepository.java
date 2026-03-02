package tz.go.mnrt.asert.modules.core.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.dtos.ReportDto;
import tz.go.mnrt.asert.modules.core.entities.Report;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

public interface ReportRepository extends BaseRepository<Report, Long> {

    void deleteByUuid(UUID uuid);

    @Query("FROM Report r where lower(r.name) LIKE %:query% AND r.parent.id =:parentId ORDER BY"
            + " r.name asc")
    List<Report> findAllByParentId(
            @Param("parentId") Long parentId, @Param("query") String query);

    List<Report> findAllByParentId(Long id);

    @Query("FROM Report f where (lower(f.name) LIKE %:query% or lower(f.url) LIKE %:query%) AND"
            + " f.parent is null ORDER BY f.name asc")
    Page<Report> findAllByParentIsNull(@Param("query") String query, Pageable pageable);

    Page<Report> findAllByParentIsNull(Pageable pageable);

    @Query("Select DISTINCT f from Report f left join fetch f.children where f.parent is null ")
    List<Report> findAllByParentIsNull();

    @Query("SELECT new tz.go.mnrt.asert.modules.core.dtos.ReportDto(f.id,f.uuid,f.name,f.url) from"
            + " Report f WHERE f.parent.id = :parentId")
    List<ReportDto> children(@Param("parentId") Long parentId);

    Optional<Report> findByUuid(UUID uuid);

    List<Report> findReportByParentIsNull();
}
