package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class HotelListingResponseDto {
    private Long id;
    private UUID uuid;

    private String name;
    private String website;
    private String email;
    private String phone;

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

    private String locationName;
    private Long locationId;
    private String displayName;

    private Double aggregateScore;
    private String starRating;

    @Builder.Default
    private Set<HotelFacilityResponseDto> facilities = new HashSet<>();

    @Builder.Default
    private Set<HotelMediaResponseDto> media = new HashSet<>();

    @Builder.Default
    private Set<RoomTypeResponseDto> roomTypes = new HashSet<>();

    public HotelListingResponseDto(Hotel entity) {
        BeanUtils.copyProperties(entity, this, "roomTypes", "facilities", "media");
    }

    public HotelListingResponseDto(Hotel entity, String fileUploadUrl) {
        // Initialize collections manually to ensure they're not null
        this.roomTypes = new HashSet<>();
        this.facilities = new HashSet<>();
        this.media = new HashSet<>();

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

        if (entity.getFacilities() != null && !entity.getFacilities().isEmpty()) {
            entity.getFacilities().forEach(facility -> this.facilities.add(new HotelFacilityResponseDto(facility)));
        }

        if (entity.getMedia() != null && !entity.getMedia().isEmpty()) {
            entity.getMedia().forEach(media -> this.media.add(new HotelMediaResponseDto(media, fileUploadUrl)));
        }
    }
}
