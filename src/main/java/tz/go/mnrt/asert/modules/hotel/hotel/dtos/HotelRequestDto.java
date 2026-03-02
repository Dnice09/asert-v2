package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos.HotelFacilityRequestDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelRequestDto {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "Hotel name is required")
    private String name;

    private String website;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String description;

    private Double latitude;
    private Double longitude;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    private Long defaultImageId;

    private List<HotelMediaDto> hotelMediaDtoList;

    private HotelState status;

    @NotNull(message = "LocationId is required")
    private Long locationId;

    @Valid
    @Builder.Default
    private Set<RoomTypeRequestDto> roomTypes = new HashSet<>();

    @Valid
    @Builder.Default
    private Set<HotelFacilityRequestDto> facilities = new HashSet<>();

    public HotelRequestDto(Hotel entity) {
        BeanUtils.copyProperties(entity, this);
        if (entity.getDefaultImage() != null) {
            this.defaultImageId = entity.getDefaultImage().getId();
        }
        if (entity.getAdminHierarchy() != null) {
            this.locationId = entity.getAdminHierarchy().getId();
        }
    }
}
