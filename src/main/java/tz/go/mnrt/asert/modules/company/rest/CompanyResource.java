package tz.go.mnrt.asert.modules.company.rest;

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
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.company.dtos.CompanyDto;
import tz.go.mnrt.asert.modules.company.services.CompanyService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

@RestController
@RequestMapping(Constant.API_V1 + "/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyResource {

    final CompanyService companyService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                companyService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody CompanyDto companyDto) {
        if (companyDto.getId() != null || companyDto.getUuid() != null) {
            throw new ValidationException("New Company cannot contain id or uuid");
        }

        return CustomApiResponse.created("Company create successfully", companyService.save(companyDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody CompanyDto companyDto, @PathVariable UUID uuid) {
        if (companyDto.getUuid() == null || !Objects.equals(companyDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Company id must be present and equals to path id {" + uuid + "}");
        }

        companyService.save(companyDto);
        return CustomApiResponse.ok("Company updated successfully");
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findByUuid(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(companyService.findByUuid(uuid));
    }

    @GetMapping("/{id}/get-by-id")
    public CustomApiResponse findById(@PathVariable("id") Long id) {
        return CustomApiResponse.ok(companyService.findById(id));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        companyService.delete(uuid);
        return CustomApiResponse.ok("Company deleted successfully");
    }
}
