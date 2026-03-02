package tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelMediaRequestDto {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Approval status is required")
    private Boolean isApproved;

    @NotNull(message = "Media ID is required")
    private Long mediaId;

    private Long hotelId;

    private Long userId;

    @NotNull(message = "Default status is required")
    @Builder.Default
    private Boolean isDefault = false;

    public HotelMediaRequestDto(HotelMedia entity) {
        BeanUtils.copyProperties(entity, this);
        if (entity.getHotel() != null) {
            this.hotelId = entity.getHotel().getId();
        }
        if (entity.getUser() != null) {
            this.userId = entity.getUser().getId();
        }
        if (entity.getAttachment() != null) {
            this.mediaId = entity.getAttachment().getId();
        }
    }
}
