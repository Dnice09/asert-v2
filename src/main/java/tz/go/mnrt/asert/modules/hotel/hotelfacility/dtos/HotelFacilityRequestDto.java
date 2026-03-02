package tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos;

import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.HotelFacilityTypes;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.entity.HotelFacility;

import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelFacilityRequestDto {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "Facility name is required")
    private String name;

    @NotNull(message = "Facility type is required")
    private HotelFacilityTypes facilityType;

    private String description;

    private Integer capacity;

    private String openingHours;

    private Long hotelId;

    public HotelFacilityRequestDto(HotelFacility entity) {
        BeanUtils.copyProperties(entity, this);
        if (entity.getHotel() != null) {
            this.hotelId = entity.getHotel().getId();
        }
    }
}
