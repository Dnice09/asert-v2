package tz.go.mnrt.asert.modules.assessment.document.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.setup.documenttype.entity.DocumentType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessor_documents")
public class AssessorDocument extends BaseModel {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "document_type_id", nullable = false, insertable = false, updatable = false)
    private DocumentType documentType;

    @Column(name = "document_type_id", nullable = false)
    private Long documentTypeId;

    @ManyToOne
    @JoinColumn(name = "assessor_id", nullable = false, insertable = false, updatable = false)
    private Assessor assessor;

    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;
}
