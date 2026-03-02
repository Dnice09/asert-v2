package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorScoreDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.form.form.dtos.FormResponseDto;
import tz.go.mnrt.asert.modules.form.formsection.dtos.FormSectionResponseDto;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentVarianceLogResponseDto {
    private Long id;
    private UUID uuid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    private Long hotelId;

    private Long formId;

    private Long formFieldId;

    private String varianceType;

    private String createdBy;

    private HotelResponseDto hotel;

    private FormResponseDto form;

    private AssessorResponseDto assessor;

    private String status;

    private Double scoreDifference;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime detectedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime resolvedAt;

    private FormSectionResponseDto section;

    private UUID sectionUuid;

    // The new List field
    private List<AssessorScoreDto> assessorScores = new ArrayList<>();

    public AssessmentVarianceLogResponseDto(AssessmentVarianceLog entity) {
        entity.toDao(this);

        if (entity.getSection() != null) {
            this.section = new FormSectionResponseDto(entity.getSection());
        }

        sectionUuid = entity.getSection() != null ? entity.getSection().getUuid() : null;
        if (entity.getHotel() != null) {
            hotelId = entity.getHotel().getId();
            this.hotel = new HotelResponseDto(entity.getHotel());
        }
        if (entity.getForm() != null) {
            formId = entity.getForm().getId();
            this.form = new FormResponseDto(entity.getForm());
        }
        // Map flat assessor fields to the list
        addAssessorToList(entity.getAssessor1(), entity.getAssessor1Score());
        addAssessorToList(entity.getAssessor2(), entity.getAssessor2Score());
        addAssessorToList(entity.getAssessor3(), entity.getAssessor3Score());
    }

    public AssessmentVarianceLogResponseDto(AssessmentVarianceLog entity, Assessor assessor) {
        entity.toDao(this);

        if (entity.getSection() != null) {
            this.section = new FormSectionResponseDto(entity.getSection());
        }

        if (assessor != null) {
            this.assessor = new AssessorResponseDto(assessor);
        }

        sectionUuid = entity.getSection() != null ? entity.getSection().getUuid() : null;
        if (entity.getHotel() != null) {
            hotelId = entity.getHotel().getId();
            this.hotel = new HotelResponseDto(entity.getHotel());
        }
        if (entity.getForm() != null) {
            formId = entity.getForm().getId();
            this.form = new FormResponseDto(entity.getForm());
        }
        // Map flat assessor fields to the list
        addAssessorToList(entity.getAssessor1(), entity.getAssessor1Score());
        addAssessorToList(entity.getAssessor2(), entity.getAssessor2Score());
        addAssessorToList(entity.getAssessor3(), entity.getAssessor3Score());
    }

    private void addAssessorToList(Assessor assessor, Double score) {
        if (assessor != null) {
            this.assessorScores.add(AssessorScoreDto.builder()
                    .id(assessor.getId())
                    .score(score)
                    .title(assessor.getTitle())
                    .firstName(assessor.getFirstName())
                    .lastName(assessor.getLastName())
                    .email(assessor.getEmail())
                    .build());
        }
    }

}
