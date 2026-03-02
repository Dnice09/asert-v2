package tz.go.mnrt.asert.modules.setup.financialyear.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.setup.financialyear.dtos.FinancialYearDto;

public interface FinancialYearService {

    FinancialYearDto save(FinancialYearDto financialYearDto);

    Page<FinancialYearDto> findAll(Pageable page, Map<String, String> search);

    FinancialYearDto findByUuid(UUID id);

    FinancialYearDto getCurrent();

    void delete(UUID uuid);
}
