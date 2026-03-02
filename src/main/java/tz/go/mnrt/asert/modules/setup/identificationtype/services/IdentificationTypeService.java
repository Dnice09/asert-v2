package tz.go.mnrt.asert.modules.setup.identificationtype.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.setup.identificationtype.dtos.IdentificationTypeRequestDto;
import tz.go.mnrt.asert.modules.setup.identificationtype.dtos.IdentificationTypeResponseDto;

public interface IdentificationTypeService {

  IdentificationTypeRequestDto save(IdentificationTypeRequestDto identificationTypeDto);

  Page<IdentificationTypeResponseDto> findAll(Pageable page, Map<String, String> search);

  IdentificationTypeResponseDto findByUuid(UUID id);

  void delete(UUID uuid);
}

