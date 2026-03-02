package tz.go.mnrt.asert.modules.setup.country.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.setup.country.dtos.CountryRequestDto;
import tz.go.mnrt.asert.modules.setup.country.dtos.CountryResponseDto;

public interface CountryService {

  CountryRequestDto save(CountryRequestDto countryDto);

  Page<CountryResponseDto> findAll(Pageable page, Map<String, String> search);

  CountryResponseDto findByUuid(UUID id);

  void delete(UUID uuid);
}

