package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessmentStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorHotel;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.dtos.HotelAssessmentApprovalResponseDto;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos.HotelFacilityResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos.HotelMediaResponseDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponseDto {
    private Long id;
    private UUID uuid;

    private String name;
    private String website;
    private String email;
    private String phone;
    private String description;

    private Double latitude;
    private Double longitude;

    private PropertyType propertyType;
    private String propertyTypeName;

    // Add formUuid field
    private UUID formUuid;
    private String formName;

    private HotelMediaResponseDto defaultImage;

    private HotelState status;
    private String statusName;

    private Boolean isActive;

    private Long version;

    private String companyName;
    private Long companyId;

    private Long userId;

    private String locationName;
    private Long locationId;
    private String displayName;

    private Double aggregateScore;
    private String starRating;

    @Builder.Default
    private Set<RoomTypeResponseDto> roomTypes = new HashSet<>();

    @Builder.Default
    private Set<AssessorResponseDto> assessors = new HashSet<>();

    @Builder.Default
    private Set<HotelFacilityResponseDto> facilities = new HashSet<>();

    @Builder.Default
    private Set<HotelMediaResponseDto> media = new HashSet<>();

    @NotNull(message = "Created Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Long assessorHotelId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessorHotelDateAssigned;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessorHotelDeadline;

    private Boolean assessorHotelDataCollected;
    private AssessmentStatus assessmentStatus;

    private Set<HotelAssessmentApprovalResponseDto> assessmentApprovals = new HashSet<>();

    @Builder.Default
    private Set<FormSubmissionResponseDto> formSubmissions = new HashSet<>();

    public HotelResponseDto(Hotel entity) {
        BeanUtils.copyProperties(
                entity,
                this,
                "roomTypes",
                "facilities",
                "media",
                "formSubmissions",
                "assessmentApprovals");
    }

    public HotelResponseDto(Hotel entity, AssessorHotel assessorHotel, String fileUploadUrl) {

        // Init collections
        this.roomTypes = new HashSet<>();
        this.facilities = new HashSet<>();
        this.media = new HashSet<>();
        this.formSubmissions = new HashSet<>();
        this.assessmentApprovals = new HashSet<>();

        BeanUtils.copyProperties(
                entity,
                this,
                "roomTypes",
                "facilities",
                "media",
                "formSubmissions",
                "assessmentApprovals");

        this.assessmentApprovals = entity.getAssessmentApprovals()
                .stream().map(HotelAssessmentApprovalResponseDto::new)
                .collect(Collectors.toSet());

        // AssessorHotel fields
        this.assessorHotelId = assessorHotel.getId();
        this.assessorHotelDateAssigned = assessorHotel.getDateAssigned();
        this.assessorHotelDeadline = assessorHotel.getDeadline();
        this.assessorHotelDataCollected = assessorHotel.getDataCollected();
        this.assessmentStatus = assessorHotel.getStatus();

        // Map form submissions ✅
        mapFormSubmissions(entity);

        // Property type
        if (entity.getPropertyType() != null) {
            this.propertyTypeName = entity.getPropertyType().name();
        }

        if (entity.getStatus() != null) {
            this.statusName = entity.getStatus().name();
        }

        this.displayName = entity.getName() + " (" + entity.getCompany().getName() + ")";

        if (entity.getCompany() != null) {
            this.companyId = entity.getCompany().getId();
            this.companyName = entity.getCompany().getName();
        }

        if (entity.getAdminHierarchy() != null) {
            this.locationId = entity.getAdminHierarchy().getId();
            this.locationName = entity.getAdminHierarchy().getName();
        }

        if (entity.getDefaultImage() != null) {
            this.defaultImage = new HotelMediaResponseDto(entity.getDefaultImage(), fileUploadUrl);
        }

        entity.getRoomTypes().forEach(rt -> this.roomTypes.add(new RoomTypeResponseDto(rt)));
        entity.getFacilities().forEach(f -> this.facilities.add(new HotelFacilityResponseDto(f)));
        entity.getMedia().forEach(m -> this.media.add(new HotelMediaResponseDto(m, fileUploadUrl)));
    }

    public HotelResponseDto(Hotel entity, String fileUploadUrl) {

        // Initialize collections manually to ensure they're not null
        this.roomTypes = new HashSet<>();
        this.facilities = new HashSet<>();
        this.media = new HashSet<>();
        this.assessors = new HashSet<>();

        // Copy properties (excluding collections)
        BeanUtils.copyProperties(entity, this, "roomTypes", "facilities", "media");

        // Handle property type
        if (entity.getPropertyType() != null) {
            this.propertyTypeName = entity.getPropertyType().name();
        }

        // Handle status
        if (entity.getStatus() != null) {
            this.statusName = entity.getStatus().name();
        }

        this.displayName = entity.getName().concat(" (").concat(entity.getCompany().getName()).concat(")");

        // Handle company
        if (entity.getCompany() != null) {
            this.companyId = entity.getCompany().getId();
            this.companyName = entity.getCompany().getName();
        }

        // Handle location/admin hierarchy
        if (entity.getAdminHierarchy() != null) {
            this.locationId = entity.getAdminHierarchy().getId();
            this.locationName = entity.getAdminHierarchy().getName();
        }

        // Handle default image
        if (entity.getDefaultImage() != null) {
            this.defaultImage = new HotelMediaResponseDto(entity.getDefaultImage(), fileUploadUrl);
        }

        // Map collections
        if (entity.getRoomTypes() != null && !entity.getRoomTypes().isEmpty()) {
            entity.getRoomTypes().forEach(roomType -> this.roomTypes.add(new RoomTypeResponseDto(roomType)));
        }

        if (entity.getAssessors() != null && !entity.getAssessors().isEmpty()) {
            setAssessors(entity.getAssessors().stream()
                    .map(AssessorResponseDto::new)
                    .collect(Collectors.toSet()));
        }

        if (entity.getFacilities() != null && !entity.getFacilities().isEmpty()) {
            entity.getFacilities().forEach(facility -> this.facilities.add(new HotelFacilityResponseDto(facility)));
        }

        if (entity.getMedia() != null && !entity.getMedia().isEmpty()) {
            entity.getMedia().forEach(media -> this.media.add(new HotelMediaResponseDto(media, fileUploadUrl)));
        }

        if (entity.getFormSubmissions() != null && !entity.getFormSubmissions().isEmpty()) {
            entity.getFormSubmissions()
                    .stream()
                    .map(s -> new FormSubmissionResponseDto().withDetails(s));
        }
    }

    public void setFormInfo(Form form) {
        if (form != null) {
            this.formUuid = form.getUuid();
            this.formName = form.getName();
        }
    }

    private void mapFormSubmissions(Hotel entity) {
        if (entity.getFormSubmissions() != null && !entity.getFormSubmissions().isEmpty()) {
            this.formSubmissions = entity.getFormSubmissions()
                    .stream()
                    .map(s -> new FormSubmissionResponseDto().withDetailsAndScores(s))
                    .collect(Collectors.toSet());
        }
    }
}
