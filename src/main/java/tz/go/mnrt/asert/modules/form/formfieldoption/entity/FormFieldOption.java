package tz.go.mnrt.asert.modules.form.formfieldoption.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "form_field_options")
public class FormFieldOption extends BaseModel {
    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "orderIndex")
    private int orderIndex;

    @Column(name = "score")
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    private FormField field;
}
