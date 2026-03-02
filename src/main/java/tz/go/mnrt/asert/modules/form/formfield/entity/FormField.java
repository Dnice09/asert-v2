package tz.go.mnrt.asert.modules.form.formfield.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.formfieldoption.entity.FormFieldOption;
import tz.go.mnrt.asert.modules.form.formfieldresponse.entity.FormFieldResponse;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "form_fields")
public class FormField extends BaseModel {
    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "field_type", nullable = false)
    private String fieldType;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "help_text")
    private String helpText;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "validation_rules", columnDefinition = "TEXT")
    private String validationRules;
    
    @Column(name = "conditional_logic", columnDefinition = "TEXT")
    private String conditionalLogic;

    @ManyToOne(fetch = FetchType.LAZY)
    private FormSection section;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<FormFieldOption> options = new HashSet<>();

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<FormFieldResponse> responses = new HashSet<>();

    /**
     * Check if this field needs options based on its type
     *
     * @return true if field requires options, false otherwise
     */
    public boolean needsOptions() {
        return "select".equals(fieldType) ||
                "radio".equals(fieldType) ||
                "checkbox".equals(fieldType);
    }

    /**
     * Get the maximum possible score for this field
     *
     * @return the maximum score available
     */
    public Integer getMaxScore() {
        if (!needsOptions() || options == null || options.isEmpty()) {
            return null;
        }

        return options.stream()
                .map(option -> option.getScore() != null ? option.getScore() : 0)
                .max(Integer::compare)
                .orElse(0);
    }
}
