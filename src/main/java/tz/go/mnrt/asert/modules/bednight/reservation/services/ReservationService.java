package tz.go.mnrt.asert.modules.bednight.reservation.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.bednight.reservation.dtos.ReservationRequestDto;
import tz.go.mnrt.asert.modules.bednight.reservation.dtos.ReservationResponseDto;

public interface ReservationService {

  ReservationRequestDto save(ReservationRequestDto reservationDto);

  Page<ReservationResponseDto> findAll(Pageable page, Map<String, String> search);

  ReservationResponseDto findByUuid(UUID id);

  void checkout(UUID id);

  void delete(UUID uuid);
}

