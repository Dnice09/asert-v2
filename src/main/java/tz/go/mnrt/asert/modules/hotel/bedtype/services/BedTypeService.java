package tz.go.mnrt.asert.modules.hotel.bedtype.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.hotel.bedtype.dtos.BedTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.bedtype.dtos.BedTypeResponseDto;

public interface BedTypeService {

  BedTypeRequestDto save(BedTypeRequestDto bedTypeDto);

  Page<BedTypeResponseDto> findAll(Pageable page, Map<String, String> search);

  BedTypeResponseDto findByUuid(UUID id);

  void delete(UUID uuid);
}

