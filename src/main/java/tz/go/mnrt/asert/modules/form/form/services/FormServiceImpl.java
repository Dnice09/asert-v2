package tz.go.mnrt.asert.modules.form.form.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.form.form.dtos.FormRequestDto;
import tz.go.mnrt.asert.modules.form.form.dtos.FormResponseDto;
import tz.go.mnrt.asert.modules.form.form.dtos.FormWithScoringDto;

import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.form.repository.FormRepository;
import tz.go.mnrt.asert.modules.form.formfield.dtos.FormFieldRequestDto;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;
import tz.go.mnrt.asert.modules.form.formfieldoption.dtos.FormFieldOptionRequestDto;
import tz.go.mnrt.asert.modules.form.formfieldoption.entity.FormFieldOption;
import tz.go.mnrt.asert.modules.form.formfieldresponse.repository.FormFieldResponseRepository;
import tz.go.mnrt.asert.modules.form.formsection.dtos.FormSectionRequestDto;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionWithScoringDto;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoringDto;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.repository.FormSubmissionScoreRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormServiceImpl extends SimpleSearchService<Form> implements FormService {
    private final FormRepository formRepository;
    private final ObjectMapper objectMapper;
    private final FormSubmissionScoreRepository formSubmissionScoreRepository;
    private final FormFieldResponseRepository formFieldResponseRepository;

    @Override
    @Transactional
    public FormRequestDto save(FormRequestDto formRequestDto) {
        log.info("Processing Save for Form: {}", formRequestDto.getName());

        // 1. Property Type Conflict Check
        validatePropertyTypeConflict(formRequestDto);

        Form form;
        if (formRequestDto.getUuid() != null) {
                // LOAD EXISTING (Managed Entity)
            form = formRepository.findByUuidWithAllDetails(formRequestDto.getUuid())
                    .orElseThrow(() -> new ValidationException(
                            "Form with uuid {" + formRequestDto.getUuid() + "} not found"));
            initializeFormCollections(form);

            BeanUtils.copyProperties(formRequestDto, form, "id", "uuid", "sections");
            updateFormSections(form, formRequestDto.getSections());
        } else {
            // NEW FORM
            form = new Form();
            form.setUuid(UUID.randomUUID());
            BeanUtils.copyProperties(formRequestDto, form, "id", "uuid", "sections");
            form = formRepository.save(form); // Initial save to get ID for sections
            createFormSections(form, formRequestDto.getSections(), null);
        }

        // 2. FINAL SAVE (Cascades all changes to sections -> fields -> options)
        form = formRepository.save(form);
        formRequestDto.setId(form.getId());
        return formRequestDto;
    }



    private void updateFormSections(Form form, List<FormSectionRequestDto> sectionDtos) {
        if (sectionDtos == null)
            return;

        Map<Long, FormSection> existingSections = new HashMap<>();
        collectAllSections(form.getSections(), existingSections);

        Set<Long> sectionsInDto = new HashSet<>();
        collectSectionIds(sectionDtos, sectionsInDto);

        // Identify sections to delete and validate
        List<Long> sectionIdsToDelete = existingSections.keySet().stream()
                .filter(id -> !sectionsInDto.contains(id))
                .collect(Collectors.toList());

        if (!sectionIdsToDelete.isEmpty()) {
            for (Long sectionId : sectionIdsToDelete) {
                FormSection section = existingSections.get(sectionId);
                canDeleteSection(section);
            }
        }

        // Remove orphans
        removeOrphanSectionsOptimized(form.getSections(), sectionsInDto);

        // Process updates and new sections
        List<FormSectionRequestDto> topLevelDtos = sectionDtos.stream()
                .filter(s -> s.getParentSectionUuid() == null)
                .collect(Collectors.toList());

        for (FormSectionRequestDto dto : topLevelDtos) {
            syncSection(form, dto, existingSections, null);
        }
    }

    private void syncSection(Form form, FormSectionRequestDto dto, Map<Long, FormSection> existingMap,
            FormSection parent) {
        FormSection section;
        if (dto.getId() != null && existingMap.containsKey(dto.getId())) {
            section = existingMap.get(dto.getId());
        } else {
            section = new FormSection();
            section.setUuid(UUID.randomUUID());
            section.setForm(form);
            if (parent != null) {
                parent.getSubsections().add(section);
            } else {
                form.getSections().add(section);
            }
        }

        section.setTitle(dto.getTitle());
        section.setOrderIndex(dto.getOrderIndex());
        section.setWeight(dto.getWeight());
        section.setMaxScore(dto.getMaxScore());
        section.setParentSection(parent);
        section.setSectionLevel(dto.getSectionLevel() != null ? dto.getSectionLevel()
                : (parent != null ? parent.getSectionLevel() + 1 : 0));

        syncFields(section, dto.getFields());

        if (dto.getSubsections() != null) {
            for (FormSectionRequestDto subDto : dto.getSubsections()) {
                syncSection(form, subDto, existingMap, section);
            }
        }
    }

    private void syncFields(FormSection section, List<FormFieldRequestDto> fieldDtos) {
        if (fieldDtos == null) {
            section.getFields().clear();
            return;
        }

        Map<Long, FormField> existingFields = section.getFields().stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(FormField::getId, f -> f));

        Set<Long> dtoIds = fieldDtos.stream().map(FormFieldRequestDto::getId).filter(id -> id != null)
                .collect(Collectors.toSet());

        // Remove orphans from list (triggering orphanRemoval)
        section.getFields().removeIf(f -> f.getId() != null && !dtoIds.contains(f.getId()) && canDeleteField(f));

        for (FormFieldRequestDto dto : fieldDtos) {
            FormField field;
            if (dto.getId() != null && existingFields.containsKey(dto.getId())) {
                field = existingFields.get(dto.getId());
            } else {
                field = new FormField();
                field.setUuid(UUID.randomUUID());
                field.setSection(section);
                section.getFields().add(field); // Sync collection
            }

            field.setLabel(dto.getLabel());
            field.setFieldType(dto.getFieldType());
            field.setRequired(dto.isRequired());
            field.setPlaceholder(dto.getPlaceholder());
            field.setHelpText(dto.getHelpText());
            field.setOrderIndex(dto.getOrderIndex());
            field.setValidationRules(dto.getValidationRules());
            field.setConditionalLogic(serializeConditionalLogic(dto.getConditionalLogic()));

            if (fieldNeedsOptions(field.getFieldType())) {
                syncOptions(field, dto.getOptions());
            } else {
                field.getOptions().clear();
            }
        }
    }

    private void collectAllSections(Set<FormSection> sections, Map<Long, FormSection> existingMap) {
        for (FormSection section : sections) {
            if (section.getId() != null) {
                existingMap.put(section.getId(), section);
            }
            if (section.getSubsections() != null) {
                collectAllSections(section.getSubsections(), existingMap);
            }
        }
    }

    private void removeOrphanSectionsOptimized(Set<FormSection> sections, Set<Long> dtoIds) {
        sections.removeIf(s -> s.getId() != null && !dtoIds.contains(s.getId()));
        for (FormSection section : sections) {
            if (section.getSubsections() != null) {
                removeOrphanSectionsOptimized(section.getSubsections(), dtoIds);
            }
        }
    }

    private boolean canDeleteSection(FormSection section) {
        // Check if section has any submission scores
        if (formSubmissionScoreRepository.existsBySectionId(section.getId())) {
            throw new ValidationException("Cannot delete section '" + section.getTitle() +
                "' because it has associated submission scores. Delete the submissions first.");
        }
        return true;
    }

    private boolean canDeleteField(FormField field) {
        // Check if field has any responses
        if (formFieldResponseRepository.existsByFieldId(field.getId())) {
            throw new ValidationException("Cannot delete field '" + field.getLabel() +
                "' because it has associated responses. Delete the submissions first.");
        }
        return true;
    }







    // --- Core Logic Helpers ---

    private void validatePropertyTypeConflict(FormRequestDto dto) {
        if (dto.getUuid() == null && dto.getPropertyTypes() != null) {
            for (PropertyType type : dto.getPropertyTypes()) {
                List<Form> existing = formRepository.findByPropertyTypesContaining(type);
                for (Form f : existing) {
                    if (!f.isDeleted()) {
                        throw new ValidationException("Form already exists for property type: " + type);
                    }
                }
            }
        }
    }

    private void createFormSections(Form form, List<FormSectionRequestDto> dtos, FormSection parent) {
        if (dtos == null)
            return;
        for (FormSectionRequestDto dto : dtos) {
            FormSection s = new FormSection();
            s.setUuid(UUID.randomUUID());
            s.setForm(form);
            s.setParentSection(parent);
            s.setTitle(dto.getTitle());
            s.setWeight(dto.getWeight());
            s.setMaxScore(dto.getMaxScore());
            s.setSectionLevel(dto.getSectionLevel() != null ? dto.getSectionLevel()
                    : (parent != null ? parent.getSectionLevel() + 1 : 0));
            form.getSections().add(s);

            createSectionFields(s, dto.getFields());
            createFormSections(form, dto.getSubsections(), s);
        }
    }

    private void createSectionFields(FormSection section, List<FormFieldRequestDto> dtos) {
        if (dtos == null)
            return;
        for (FormFieldRequestDto dto : dtos) {
            FormField f = new FormField();
            f.setUuid(UUID.randomUUID());
            f.setSection(section);
            f.setLabel(dto.getLabel());
            f.setFieldType(dto.getFieldType());
            f.setRequired(dto.isRequired());
            f.setPlaceholder(dto.getPlaceholder());
            f.setHelpText(dto.getHelpText());
            f.setOrderIndex(dto.getOrderIndex());
            f.setValidationRules(dto.getValidationRules());
            f.setConditionalLogic(serializeConditionalLogic(dto.getConditionalLogic()));
            section.getFields().add(f);

            if (fieldNeedsOptions(f.getFieldType())) {
                createFieldOptions(f, dto.getOptions());
            }
        }
    }

    private void createFieldOptions(FormField field, List<FormFieldOptionRequestDto> dtos) {
        if (dtos == null)
            return;
        for (FormFieldOptionRequestDto dto : dtos) {
            FormFieldOption o = new FormFieldOption();
            o.setUuid(UUID.randomUUID());
            o.setField(field);
            o.setLabel(dto.getLabel());
            o.setValue(dto.getValue());
            o.setScore(dto.getScore());
            o.setOrderIndex(dto.getOrderIndex());
            field.getOptions().add(o);
        }
    }

    // --- Standard Service Methods ---

    @Override
    public Page<FormResponseDto> findAll(Pageable page, Map<String, String> search) {
        // This still uses the old search mechanism, but the projection avoids N+1 on the main query
        if(search == null || search.isEmpty()){
            return formRepository.findAllProjected(page);
        }
        return formRepository.findAll(createSpecification(Form.class, search), page)
                .map(FormResponseDto::new);
    }

    @Override
    public FormResponseDto findByUuid(UUID uuid) {
        Form f = formRepository.findByUuidWithAllDetails(uuid).orElseThrow(() -> new ValidationException("Not found"));
        initializeFormCollections(f);
        return new FormResponseDto(f);
    }

    @Override
    public void delete(UUID uuid) {
        formRepository.softDelete(uuid);
    }

    @Override
    public FormWithScoringDto getFormWithScoring(UUID uuid) {
        Form f = formRepository.findByUuidWithAllDetails(uuid).orElseThrow(() -> new ValidationException("Not found"));
        initializeFormCollections(f);

        FormWithScoringDto scoringDto = new FormWithScoringDto();
        BeanUtils.copyProperties(new FormResponseDto(f), scoringDto);
        scoringDto.setScoringSections(f.getSections().stream()
                .filter(s -> !s.isDeleted() && s.getParentSection() == null)
                .map(this::mapSectionWithScoring)
                .collect(Collectors.toList()));
        return scoringDto;
    }



    private SectionWithScoringDto mapSectionWithScoring(FormSection s) {
        SectionWithScoringDto d = new SectionWithScoringDto();
        d.setUuid(s.getUuid());
        d.setTitle(s.getTitle());
        d.setWeight(s.getWeight());
        d.setMaxScore(s.getMaxScore());
        d.setSubsections(s.getSubsections().stream().filter(sub -> !sub.isDeleted()).map(this::mapSectionWithScoring)
                .collect(Collectors.toList()));
        return d;
    }

    private boolean fieldNeedsOptions(String type) {
        return "select".equals(type) || "radio".equals(type) || "checkbox".equals(type);
    }

    private void collectSectionIds(List<FormSectionRequestDto> dtos, Set<Long> ids) {
        if (dtos == null)
            return;
        for (FormSectionRequestDto d : dtos) {
            if (d.getId() != null)
                ids.add(d.getId());
            collectSectionIds(d.getSubsections(), ids);
        }
    }

    private String serializeConditionalLogic(List<?> logic) {
        if (logic == null || logic.isEmpty())
            return null;
        try {
            return objectMapper.writeValueAsString(logic);
        } catch (Exception e) {
            return null;
        }
    }

    private void initializeFormCollections(Form form) {
        Hibernate.initialize(form.getSections());
        for (FormSection section : form.getSections()) {
            if (!section.isDeleted()) {
                Hibernate.initialize(section.getFields());
                Hibernate.initialize(section.getSubsections());
                for (FormField field : section.getFields()) {
                    if (!field.isDeleted() && field.needsOptions()) {
                        Hibernate.initialize(field.getOptions());
                    }
                }
                for (FormSection subsection : section.getSubsections()) {
                    if (!subsection.isDeleted()) {
                        initializeSectionCollections(subsection);
                    }
                }
            }
        }
    }

    private void initializeSectionCollections(FormSection section) {
        Hibernate.initialize(section.getFields());
        Hibernate.initialize(section.getSubsections());
        for (FormField field : section.getFields()) {
            if (!field.isDeleted() && field.needsOptions()) {
                Hibernate.initialize(field.getOptions());
            }
        }
        for (FormSection subsection : section.getSubsections()) {
            if (!subsection.isDeleted()) {
                initializeSectionCollections(subsection);
            }
        }
    }

    private void syncOptions(FormField field, List<FormFieldOptionRequestDto> optionDtos) {
        if (optionDtos == null) {
            field.getOptions().clear();
            return;
        }

        Map<Long, FormFieldOption> existingOptions = field.getOptions().stream()
                .filter(o -> o.getId() != null)
                .collect(Collectors.toMap(FormFieldOption::getId, o -> o));

        Set<Long> dtoIds = optionDtos.stream().map(FormFieldOptionRequestDto::getId).filter(id -> id != null)
                .collect(Collectors.toSet());

        field.getOptions().removeIf(o -> o.getId() != null && !dtoIds.contains(o.getId()));

        for (FormFieldOptionRequestDto dto : optionDtos) {
            FormFieldOption opt;
            if (dto.getId() != null && existingOptions.containsKey(dto.getId())) {
                opt = existingOptions.get(dto.getId());
            } else {
                opt = new FormFieldOption();
                opt.setUuid(UUID.randomUUID());
                opt.setField(field);
                field.getOptions().add(opt); // Sync collection
            }
            opt.setLabel(dto.getLabel());
            opt.setValue(dto.getValue());
            opt.setScore(dto.getScore());
            opt.setOrderIndex(dto.getOrderIndex());
        }
    }
}
