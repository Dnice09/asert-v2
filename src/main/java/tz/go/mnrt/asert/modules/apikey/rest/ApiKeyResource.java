package tz.go.mnrt.asert.modules.apikey.rest;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
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
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyStatusDto;
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyRequestDto;
import tz.go.mnrt.asert.modules.apikey.services.ApiKeyService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

@RestController
@RequestMapping(Constant.API_V1 + "/api-keys")
@RequiredArgsConstructor
public class ApiKeyResource {

    final ApiKeyService apiKeyService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                apiKeyService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody ApiKeyRequestDto apiKeyRequestDto) {
        if (apiKeyRequestDto.getId() != null || apiKeyRequestDto.getUuid() != null) {
            throw new ValidationException("New ApiKey cannot contain id or uuid");
        }
        return CustomApiResponse.created("ApiKey created successfully", apiKeyService.save(apiKeyRequestDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody ApiKeyRequestDto apiKeyDto, @PathVariable UUID uuid) {
        if (apiKeyDto.getUuid() == null || !Objects.equals(apiKeyDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "ApiKey id must be present and equals to path id {" + uuid + "}");
        }

        return CustomApiResponse.accepted("ApiKey updated successfully", apiKeyService.save(apiKeyDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(apiKeyService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        apiKeyService.delete(uuid);
        return CustomApiResponse.noContent("ApiKey deleted successfully");
    }

    @PostMapping("/change-status")
    @Transactional
    public CustomApiResponse changeStatus(@Valid @RequestBody ApiKeyStatusDto apiKeyStatusDto) {
        return CustomApiResponse.created("ApiKey status updated successfully",
                apiKeyService.changeStatus(apiKeyStatusDto));
    }
}
