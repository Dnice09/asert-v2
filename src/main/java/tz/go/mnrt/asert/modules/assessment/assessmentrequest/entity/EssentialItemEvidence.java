package tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;

import javax.persistence.*;

@Entity
@Table(name = "essential_item_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EssentialItemEvidence extends BaseModel {

    @Column(name = "evidence_description")
    private String evidenceDescription;

    @Column(name = "evidence_type")
    private String evidenceType; // "document", "certificate", "photo", "video"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essential_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_essential_item_evidence_item"))
    @JsonBackReference
    private EssentialItem essentialItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_upload_id", foreignKey = @ForeignKey(name = "fk_essential_item_evidence_file"))
    private FileUpload fileUpload;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    // Helper methods
    public String getDisplayName() {
        if (fileName != null && !fileName.isEmpty()) {
            return fileName;
        }
        if (evidenceDescription != null && !evidenceDescription.isEmpty()) {
            return evidenceDescription;
        }
        return "Evidence File";
    }

    public String getFileSizeFormatted() {
        if (fileSize == null) {
            return "Unknown size";
        }
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public boolean isDocument() {
        return mimeType != null && (mimeType.contains("pdf") || 
                                   mimeType.contains("document") || 
                                   mimeType.contains("text"));
    }

    public boolean isVideo() {
        return mimeType != null && mimeType.startsWith("video/");
    }
}