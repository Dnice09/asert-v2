package tz.go.mnrt.asert.modules.setup.incidentreporttype.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.entity.IncidentReportType;

public interface IncidentReportTypeRepository
    extends BaseRepository<IncidentReportType, Long> {

  Optional<IncidentReportType> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
