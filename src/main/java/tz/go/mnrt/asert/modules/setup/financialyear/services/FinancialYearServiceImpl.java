package tz.go.mnrt.asert.modules.setup.financialyear.services;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.setup.financialyear.dtos.FinancialYearDto;
import tz.go.mnrt.asert.modules.setup.financialyear.entity.FinancialYear;
import tz.go.mnrt.asert.modules.setup.financialyear.repository.FinancialYearRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialYearServiceImpl extends SimpleSearchService<FinancialYear> implements FinancialYearService {
    private final FinancialYearRepository financialYearRepository;

    @Override
    public FinancialYearDto save(FinancialYearDto financialYearDto) {
        log.info("Checking for overlap with start date: " + financialYearDto.getStartDate() + " and end date: "
                + financialYearDto.getEndDate());

        financialYearRepository
                .existByDate(financialYearDto.getStartDate(), financialYearDto.getEndDate())
                .filter(f -> {
                    boolean isSameId = Objects.equals(f.getId(), financialYearDto.getId());
                    log.info("Checking financial year with ID: " + f.getId() + " Name: " + f.getName() + " isSameId: "
                            + isSameId);
                    return !isSameId;
                })
                .ifPresent(f -> {
                    log.error("Overlap found with financial year: " + f.getId() + " " + f.getName());
                    throw new ValidationException(
                            "Start date and/or end date overlap with another financial year");
                });

        FinancialYear financialYear = new FinancialYear();

        if (financialYearDto.getIsCurrent()) {
            validateActive(financialYearDto);
        }

        if (financialYearDto.getUuid() != null) {
            financialYear = financialYearRepository
                    .findByUuid(financialYearDto.getUuid())
                    .orElseThrow(() -> new ValidationException("Financial year to update not found"));
        }

        BeanUtils.copyProperties(financialYearDto, financialYear, "uuid");
        financialYear = financialYearRepository.save(financialYear);

        if (financialYearDto.getIsCurrent()) {
            log.info("Updating related configurations and setting other financial years to not current");
            financialYearRepository.setNotCurrentOthers(financialYear.getId());
        }

        financialYearDto.setId(financialYear.getId());
        return financialYearDto;
    }

    private void validateActive(FinancialYearDto dto) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(dto.getStartDate()) || today.isAfter(dto.getEndDate())) {
            throw new ValidationException(
                    "You cannot set 'is current' because this financial year has not yet started or has already ended");
        }
    }

    @Override
    public Page<FinancialYearDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated financial years with page {} and search {} ", page, search);
        // NOTE: Auto-generated method stub
        return financialYearRepository.findAll(createSpecification(FinancialYear.class, search), page)
                .map(FinancialYearDto::new);
    }

    @Override
    public FinancialYearDto findByUuid(UUID uuid) {
        log.info("finding financial by uuid {} ", uuid);
        return financialYearRepository.findByUuid(uuid).map(FinancialYearDto::new)
                .orElseThrow(() -> new ValidationException("FinancialYear with uuid {" + uuid + "} not found"));
    }

    @Override
    public FinancialYearDto getCurrent() {
        return financialYearRepository.findFirstByIsCurrentTrue().map(FinancialYearDto::new)
                .orElseThrow(() -> new ValidationException("No current financial found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting financial with uuid {} ", uuid);
        financialYearRepository.deleteByUuid(uuid);
    }
}
