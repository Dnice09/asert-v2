package tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos;

import java.util.UUID;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.HotelFacilityTypes;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.entity.HotelFacility;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelFacilityResponseDto {
    private Long id;
    private UUID uuid;

    private String name;
    private HotelFacilityTypes facilityType;
    private String facilityTypeName;
    private String description;
    private Integer capacity;
    private String openingHours;

    private Long hotelId;
    private String hotelName;

    private Long version;

    public HotelFacilityResponseDto(HotelFacility entity) {
        BeanUtils.copyProperties(entity, this);
        if (entity.getFacilityType() != null) {
            this.facilityTypeName = entity.getFacilityType().name();
        }
        if (entity.getHotel() != null) {
            this.hotelId = entity.getHotel().getId();
            this.hotelName = entity.getHotel().getName();
        }
    }
}
