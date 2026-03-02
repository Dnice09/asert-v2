package tz.go.mnrt.asert.modules.hotel.roomtype.dtos;

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
import tz.go.mnrt.asert.modules.hotel.roomtype.entity.RoomType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomTypeResponseDto {
    private Long id;
    private UUID uuid;

    private String name;
    private String description;
    private Integer quantity;
    private Double size;
    private Integer maxOccupancy;
    private Long bedTypeId;
    private Long bedRoomTypeId;

    private Long hotelId;
    private String hotelName;

    private Long version;

    @Builder.Default
    private Set<String> amenities = new HashSet<>();

    public RoomTypeResponseDto(RoomType entity) {
        setBedTypeId(entity.getType() != null ? entity.getType().getId() : null);
        setBedRoomTypeId(entity.getBedRoomType() != null ? entity.getBedRoomType().getId() : null);
        BeanUtils.copyProperties(entity, this);
        if (entity.getHotel() != null) {
            this.hotelId = entity.getHotel().getId();
            this.hotelName = entity.getHotel().getName();
        }
        if (entity.getAmenities() != null) {
            this.amenities.addAll(entity.getAmenities());
        }
    }
}
