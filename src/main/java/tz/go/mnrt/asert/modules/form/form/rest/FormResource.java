package tz.go.mnrt.asert.modules.form.form.rest;

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
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.form.form.dtos.FormRequestDto;

import tz.go.mnrt.asert.modules.form.form.services.FormService;
import tz.go.mnrt.asert.modules.form.formfield.dtos.ConditionalLogicEvaluationRequestDto;
import tz.go.mnrt.asert.modules.form.formfield.dtos.ConditionalLogicEvaluationResponseDto;
import tz.go.mnrt.asert.modules.form.formfield.services.ConditionalLogicService;

@RestController
@RequestMapping(Constant.API_V1 + "/forms")
@RequiredArgsConstructor
@Slf4j
public class FormResource {

    final FormService formService;
    final ConditionalLogicService conditionalLogicService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                formService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody FormRequestDto formRequestDto) {
        if (formRequestDto.getId() != null || formRequestDto.getUuid() != null) {
            throw new ValidationException("New Form cannot contain id or uuid");
        }
        return CustomApiResponse.created("Form create successfully", formService.save(formRequestDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody FormRequestDto formDto, @PathVariable UUID uuid) {
        if (formDto.getUuid() == null || !Objects.equals(formDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Form uuid must be present and equals to path uuid {" + uuid + "}");
        }

        return CustomApiResponse.accepted("Form updated successfully", formService.save(formDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(formService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        formService.delete(uuid);
        return CustomApiResponse.noContent("Form deleted successfully");
    }



    @PostMapping("/evaluate-conditional-logic")
    public CustomApiResponse evaluateConditionalLogic(
            @Valid @RequestBody ConditionalLogicEvaluationRequestDto request) {
        try {
            var form = formService.findByUuid(request.getFormUuid());
            var fields = form.getSections().stream()
                    .flatMap(section -> section.getFields().stream())
                    .map(fieldDto -> {
                        var field = new tz.go.mnrt.asert.modules.form.formfield.entity.FormField();
                        field.setUuid(fieldDto.getUuid());
                        field.setConditionalLogic(fieldDto.getConditionalLogic());
                        field.setRequired(fieldDto.isRequired());
                        return field;
                    })
                    .collect(java.util.stream.Collectors.toList());

            var fieldVisibility = conditionalLogicService.evaluateFieldVisibility(fields, request.getFieldValues());
            var fieldRequiredStatus = conditionalLogicService.evaluateFieldRequiredStatus(fields,
                    request.getFieldValues());
            var sectionVisibility = conditionalLogicService.evaluateSectionVisibility(fields, request.getFieldValues());

            var response = new ConditionalLogicEvaluationResponseDto(fieldVisibility, fieldRequiredStatus,
                    sectionVisibility);
            return CustomApiResponse.ok(response);
        } catch (Exception e) {
            log.error("Error evaluating conditional logic: {}", e.getMessage());
            return CustomApiResponse.badRequest("Error evaluating conditional logic: " + e.getMessage(), null);
        }
    }
}
