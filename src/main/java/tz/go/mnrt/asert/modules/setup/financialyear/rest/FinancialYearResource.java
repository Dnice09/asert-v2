package tz.go.mnrt.asert.modules.setup.financialyear.rest;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.financialyear.dtos.FinancialYearDto;
import tz.go.mnrt.asert.modules.setup.financialyear.services.FinancialYearService;

@RestController
@RequestMapping(Constant.API_V1 + "/financial-years")
@RequiredArgsConstructor
public class FinancialYearResource {
    final FinancialYearService financialYearService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                financialYearService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    public CustomApiResponse create(@Valid @RequestBody FinancialYearDto financialYearDto) {
        if (financialYearDto.getId() != null || financialYearDto.getUuid() != null) {
            throw new ValidationException("New Financial Year cannot contain an id or a uuid");
        }

        return CustomApiResponse.created("Financial Year successfully created",
                financialYearService.save(financialYearDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody FinancialYearDto financialYearDto, @PathVariable UUID uuid) {
        if (financialYearDto.getUuid() == null || !Objects.equals(financialYearDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Financial Year uuid must be present and equals to path id {" + uuid + "}");
        }

        return CustomApiResponse.accepted("Financial Year updated successfully",
                financialYearService.save(financialYearDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(financialYearService.findByUuid(uuid));
    }

    @GetMapping("/current")
    public CustomApiResponse findCurrent() {
        return CustomApiResponse.ok(financialYearService.getCurrent());
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        financialYearService.delete(uuid);
        return CustomApiResponse.noContent("Financial Year deleted successfully");
    }
}
