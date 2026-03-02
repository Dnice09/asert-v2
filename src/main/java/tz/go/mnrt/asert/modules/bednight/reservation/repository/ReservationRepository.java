package tz.go.mnrt.asert.modules.bednight.reservation.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.Reservation;

public interface ReservationRepository
    extends BaseRepository<Reservation, Long> {

  Optional<Reservation> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
