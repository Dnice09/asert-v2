package tz.go.mnrt.asert.modules.hotel.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.statemachine.hotel.entity.HotelStateMachineState;

@Repository
public interface HotelStateMachineStateRepository extends JpaRepository<HotelStateMachineState, String> {
}
