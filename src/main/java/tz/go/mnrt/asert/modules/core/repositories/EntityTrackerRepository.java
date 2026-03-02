package tz.go.mnrt.asert.modules.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.go.mnrt.asert.modules.core.entities.EntityTracker;

public interface EntityTrackerRepository extends JpaRepository<EntityTracker, Long> {}
