package tz.go.mnrt.asert.modules.hotel.bedroomtype.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.hotel.bedroomtype.dtos.BedRoomTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.dtos.BedRoomTypeResponseDto;


public interface BedRoomTypeService {

  BedRoomTypeRequestDto save(BedRoomTypeRequestDto bedTypeDto);

  Page<BedRoomTypeResponseDto> findAll(Pageable page, Map<String, String> search);

  BedRoomTypeResponseDto findByUuid(UUID id);

  void delete(UUID uuid);
}

