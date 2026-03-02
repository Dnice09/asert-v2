package tz.go.mnrt.asert.modules.company.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.company.dtos.CompanyDto;

import java.util.Map;
import java.util.UUID;

public interface CompanyService {

    CompanyDto save(CompanyDto companyDto);

    Page<CompanyDto> findAll(Pageable page, Map<String, String> search);

    CompanyDto findByUuid(UUID id);

    CompanyDto findById(Long id);

    void delete(UUID uuid);

}
