package tz.go.mnrt.asert.modules.bednight.incidentreport.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.bednight.incidentreport.entity.IncidentReport;

public interface IncidentReportRepository
    extends BaseRepository<IncidentReport, Long> {

  Optional<IncidentReport> findByUuid(UUID uuid);

  Optional<IncidentReport> findByVisitorId(Long visitorId);

  @Query("SELECT ir FROM IncidentReport ir WHERE ir.visitor.uuid = :uuid")
  List<IncidentReport> findByVisitorUuid(@Param("uuid") UUID uuid);

  void deleteByUuid(UUID uuid);
}
