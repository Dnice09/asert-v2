package tz.go.mnrt.asert.modules.form.formfield.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import tz.go.mnrt.asert.modules.form.formfield.dtos.ConditionalLogicDto;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;

public interface ConditionalLogicService {

    boolean evaluateCondition(ConditionalLogicDto condition, Map<UUID, String> fieldValues);

    List<ConditionalLogicDto> getConditionalLogicForField(FormField field);

    Map<UUID, Boolean> evaluateFieldVisibility(List<FormField> fields, Map<UUID, String> fieldValues);

    Map<UUID, Boolean> evaluateFieldRequiredStatus(List<FormField> fields, Map<UUID, String> fieldValues);

    Map<UUID, Boolean> evaluateSectionVisibility(List<FormField> fields, Map<UUID, String> fieldValues);
}
