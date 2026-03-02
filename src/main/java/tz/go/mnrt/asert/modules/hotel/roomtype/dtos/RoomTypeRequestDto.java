package tz.go.mnrt.asert.modules.hotel.roomtype.dtos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
public class RoomTypeRequestDto {
    private Long id;
    private UUID uuid;

    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Double size;

    private String name;

    private Integer maxOccupancy;

    private Long bedTypeId;
    private Long bedRoomTypeId;

    private Long roomTypeId;

    private Long hotelId;

    @Builder.Default
    private Set<String> amenities = new HashSet<>();

    public RoomTypeRequestDto(RoomType entity) {
        BeanUtils.copyProperties(entity, this);
        if (entity.getHotel() != null) {
            this.hotelId = entity.getHotel().getId();
        }
        if (entity.getAmenities() != null) {
            this.amenities.addAll(entity.getAmenities());
        }
    }
}
