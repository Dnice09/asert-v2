package tz.go.mnrt.asert.modules.form.formfield.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.form.formfield.dtos.ConditionalActionDto;
import tz.go.mnrt.asert.modules.form.formfield.dtos.ConditionalLogicDto;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;
import tz.go.mnrt.asert.modules.form.formfield.services.ConditionalLogicService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConditionalLogicServiceImpl implements ConditionalLogicService {

    private final ObjectMapper objectMapper;

    @Override
    public boolean evaluateCondition(ConditionalLogicDto condition, Map<UUID, String> fieldValues) {
        String fieldValue = fieldValues.get(condition.getTriggerFieldUuid());
        String conditionValue = condition.getValue();
        String operator = condition.getOperator();

        if (fieldValue == null) {
            return false;
        }

        switch (operator.toLowerCase()) {
            case "equals":
                return fieldValue.equals(conditionValue);
            case "not_equals":
                return !fieldValue.equals(conditionValue);
            case "contains":
                return fieldValue.contains(conditionValue);
            case "not_contains":
                return !fieldValue.contains(conditionValue);
            case "empty":
                return fieldValue.trim().isEmpty();
            case "not_empty":
                return !fieldValue.trim().isEmpty();
            case "greater_than":
                return compareNumeric(fieldValue, conditionValue) > 0;
            case "less_than":
                return compareNumeric(fieldValue, conditionValue) < 0;
            case "greater_equal":
                return compareNumeric(fieldValue, conditionValue) >= 0;
            case "less_equal":
                return compareNumeric(fieldValue, conditionValue) <= 0;
            default:
                log.warn("Unknown operator: {}", operator);
                return false;
        }
    }

    @Override
    public List<ConditionalLogicDto> getConditionalLogicForField(FormField field) {
        if (!StringUtils.hasText(field.getConditionalLogic())) {
            return List.of();
        }

        try {
            return objectMapper.readValue(field.getConditionalLogic(),
                    new TypeReference<List<ConditionalLogicDto>>() {
                    });
        } catch (Exception e) {
            log.error("Error parsing conditional logic for field {}: {}", field.getUuid(), e.getMessage());
            return List.of();
        }
    }

    @Override
    public Map<UUID, Boolean> evaluateFieldVisibility(List<FormField> fields, Map<UUID, String> fieldValues) {
        Map<UUID, Boolean> visibility = new HashMap<>();

        for (FormField field : fields) {
            visibility.put(field.getUuid(), true);

            List<ConditionalLogicDto> conditions = getConditionalLogicForField(field);
            for (ConditionalLogicDto condition : conditions) {
                if (evaluateCondition(condition, fieldValues)) {
                    for (ConditionalActionDto action : condition.getActions()) {
                        if ("hide_field".equals(action.getActionType()) && action.getTargetFieldUuid() != null) {
                            visibility.put(action.getTargetFieldUuid(), false);
                        } else if ("show_field".equals(action.getActionType()) && action.getTargetFieldUuid() != null) {
                            visibility.put(action.getTargetFieldUuid(), true);
                        }
                    }
                }
            }
        }

        return visibility;
    }

    @Override
    public Map<UUID, Boolean> evaluateFieldRequiredStatus(List<FormField> fields, Map<UUID, String> fieldValues) {
        Map<UUID, Boolean> requiredStatus = new HashMap<>();

        for (FormField field : fields) {
            requiredStatus.put(field.getUuid(), field.isRequired());

            List<ConditionalLogicDto> conditions = getConditionalLogicForField(field);
            for (ConditionalLogicDto condition : conditions) {
                if (evaluateCondition(condition, fieldValues)) {
                    for (ConditionalActionDto action : condition.getActions()) {
                        if ("make_required".equals(action.getActionType()) && action.getTargetFieldUuid() != null) {
                            requiredStatus.put(action.getTargetFieldUuid(), true);
                        } else if ("make_optional".equals(action.getActionType())
                                && action.getTargetFieldUuid() != null) {
                            requiredStatus.put(action.getTargetFieldUuid(), false);
                        }
                    }
                }
            }
        }

        return requiredStatus;
    }

    @Override
    public Map<UUID, Boolean> evaluateSectionVisibility(List<FormField> fields, Map<UUID, String> fieldValues) {
        Map<UUID, Boolean> sectionVisibility = new HashMap<>();

        for (FormField field : fields) {
            List<ConditionalLogicDto> conditions = getConditionalLogicForField(field);
            for (ConditionalLogicDto condition : conditions) {
                if (evaluateCondition(condition, fieldValues)) {
                    for (ConditionalActionDto action : condition.getActions()) {
                        if ("hide_section".equals(action.getActionType()) && action.getTargetSectionUuid() != null) {
                            sectionVisibility.put(action.getTargetSectionUuid(), false);
                        } else if ("show_section".equals(action.getActionType())
                                && action.getTargetSectionUuid() != null) {
                            sectionVisibility.put(action.getTargetSectionUuid(), true);
                        }
                    }
                }
            }
        }

        return sectionVisibility;
    }

    private int compareNumeric(String value1, String value2) {
        try {
            Double num1 = Double.parseDouble(value1);
            Double num2 = Double.parseDouble(value2);
            return num1.compareTo(num2);
        } catch (NumberFormatException e) {
            return value1.compareTo(value2);
        }
    }
}
