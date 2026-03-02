package tz.go.mnrt.asert.modules.setup.translation.rest;

import java.io.IOException;
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
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.core.services.TranslationService;
import tz.go.mnrt.asert.modules.setup.translation.dtos.TranslationDto;

@RestController
@RequestMapping(Constant.API_V1 + "/translations")
@RequiredArgsConstructor
public class TranslationResource {
    private final TranslationService translationService;

    @GetMapping
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
        return CustomApiResponse.ok(
                translationService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @GetMapping(value = "/all", produces = "application/json")
    @NoAuthorization
    public CustomApiResponse getAll(@RequestParam() Map<String, String> search) {
        return CustomApiResponse.ok(translationService.findAll());
    }

    @PostMapping
    public CustomApiResponse create(@Valid @RequestBody TranslationDto translationDto) {
        if (translationDto.getId() != null || translationDto.getUuid() != null) {
            throw new ValidationException("New translation does not contain an id or a uuid");
        }

        TranslationDto dto = translationService.titleizeTranslation(translationDto);

        return CustomApiResponse.created("Translation created successfully", translationService.save(dto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody TranslationDto translationDto, @PathVariable UUID uuid) {
        if (translationDto.getUuid() == null || !Objects.equals(translationDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Translation uuid must be present and equals to path id {" + uuid + "}");
        }

        TranslationDto dto = translationService.titleizeTranslation(translationDto);

        translationService.save(dto);
        return CustomApiResponse.ok("Translation Updated successfully");
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable UUID uuid) throws IOException {
        return CustomApiResponse.ok(translationService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable UUID uuid) {
        translationService.delete(uuid);
        return CustomApiResponse.noContent("Translation deleted successfully");
    }
}
