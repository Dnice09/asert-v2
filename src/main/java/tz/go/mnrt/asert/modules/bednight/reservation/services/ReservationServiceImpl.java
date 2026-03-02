package tz.go.mnrt.asert.modules.bednight.reservation.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.helpers.Utils;
import tz.go.mnrt.asert.modules.bednight.reservation.dtos.ReservationRequestDto;
import tz.go.mnrt.asert.modules.bednight.reservation.dtos.ReservationResponseDto;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.Reservation;
import tz.go.mnrt.asert.modules.bednight.reservation.repository.ReservationRepository;
import tz.go.mnrt.asert.modules.bednight.visitor.entity.Visitor;
import tz.go.mnrt.asert.modules.bednight.visitor.repository.VisitorRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl extends SimpleSearchService<Reservation> implements ReservationService {
  private final ReservationRepository reservationRepository;
  private final VisitorRepository visitorRepository;

  @Override
  public ReservationRequestDto save(ReservationRequestDto reservationRequestDto) {
    Reservation reservation = new Reservation();
    if (reservationRequestDto.getUuid() != null) {
      reservation =
          reservationRepository
              .findByUuid(reservationRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "Reservation with uuid {" + reservationRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(reservationRequestDto, reservation, "uuid");
    assert (reservation.getUuid() != null);
        for (Long visitorId : reservationRequestDto.getVisitorIds()) {
            Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new ValidationException("Visitor not found"));

            reservation.addVisitor(visitor);
        }
    reservation.setReservationCode(Utils.createReservationCode(reservation.getHotelId(),reservation.getRoomNumber()));
    reservation = reservationRepository.save(reservation);
    reservationRequestDto.setId(reservation.getId());
    return reservationRequestDto;
  }

  @Override
  public Page<ReservationResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated Reservations with page {} and search {} ", page, search);
    return reservationRepository
        .findAll(createSpecification(Reservation.class, search), page)
        .map(ReservationResponseDto::new);
  }

  @Override
  public ReservationResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return reservationRepository
        .findByUuid(uuid)
        .map(ReservationResponseDto::new)
        .orElseThrow(() -> new ValidationException("Reservation with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting Reservation with uuid {} ", uuid);
    reservationRepository.softDelete(uuid);
  }

  @Override
  public void checkout(UUID id) {
     log.info("checkout reservation with uuid {} ", id);
     Reservation reservation =
          reservationRepository
              .findByUuid(id)
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "Reservation with uuid {" + id + "} not found"));
    reservation.setIsCheckedOut(true);
    reservation.setCheckOutDate(LocalDateTime.now());
    reservationRepository.save(reservation);
  }
}
