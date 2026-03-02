package tz.go.mnrt.asert.modules.setup.companytype.rest;

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
import tz.go.mnrt.asert.modules.setup.companytype.dtos.CompanyTypeDto;
import tz.go.mnrt.asert.modules.setup.companytype.services.CompanyTypeService;

@RestController
@RequestMapping(Constant.API_V1 + "/company-types")
@RequiredArgsConstructor
public class CompanyTypeResource {

    final CompanyTypeService companyTypeService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                companyTypeService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody CompanyTypeDto companyTypeDto) {
        if (companyTypeDto.getId() != null || companyTypeDto.getUuid() != null) {
            throw new ValidationException("New CompanyType cannot contain id or uuid");
        }

        return CustomApiResponse.created("CompanyType create successfully", companyTypeService.save(companyTypeDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody CompanyTypeDto companyTypeDto, @PathVariable UUID uuid) {
        if (companyTypeDto.getUuid() == null || !Objects.equals(companyTypeDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "CompanyType id must be present and equals to path id {" + uuid + "}");
        }

        return CustomApiResponse.accepted("CompanyType updated successfully", companyTypeService.save(companyTypeDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(companyTypeService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        companyTypeService.delete(uuid);
        return CustomApiResponse.noContent("CompanyType deleted successfully");
    }
}
