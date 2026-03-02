package tz.go.mnrt.asert.modules.form.formsection.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "form_sections")
public class FormSection extends BaseModel {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "max_score")
    private Integer maxScore;

    @ManyToOne(fetch = FetchType.LAZY)
    private Form form;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "is_deleted = false")
    @BatchSize(size = 20)
    @Builder.Default
    private Set<FormField> fields = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_section_id")
    private FormSection parentSection;

    @OneToMany(mappedBy = "parentSection", cascade = CascadeType.ALL)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<FormSection> subsections = new HashSet<>();

    @Column(name = "section_level")
    private Integer sectionLevel;
}
