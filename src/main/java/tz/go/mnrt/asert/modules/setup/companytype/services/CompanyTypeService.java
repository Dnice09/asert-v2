package tz.go.mnrt.asert.modules.setup.companytype.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.setup.companytype.dtos.CompanyTypeDto;

import java.util.Map;
import java.util.UUID;

public interface CompanyTypeService {

  CompanyTypeDto save(CompanyTypeDto companyTypeDto);

  Page<CompanyTypeDto> findAll(Pageable page, Map<String, String> search);

  CompanyTypeDto findByUuid(UUID id);

  void delete(UUID uuid);
}
