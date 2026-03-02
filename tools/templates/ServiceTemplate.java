package tz.go.mnrt.asert.modules.#package_name.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.#package_name.dtos.#module_nameRequestDto;
import tz.go.mnrt.asert.modules.#package_name.dtos.#module_nameResponseDto;

public interface #module_nameService {

  #module_nameRequestDto save(#module_nameRequestDto #module_varDto);

  Page<#module_nameResponseDto> findAll(Pageable page, Map<String, String> search);

  #module_nameResponseDto findByUuid(UUID id);

  void delete(UUID uuid);
}

