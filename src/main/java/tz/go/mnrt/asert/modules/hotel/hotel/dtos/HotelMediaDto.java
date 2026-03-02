package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import lombok.*;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class HotelMediaDto implements Serializable {
    private Long id;
    private UUID uuid;
    private Long hotelId;
    private Long userId;
    private Long attachmentId;
    private String name;
    // Remove base64 field - use attachmentId to reference FileUpload instead
    // private String base64;
    private Long fileSize;

    public HotelMediaDto(HotelMedia entity) {
        entity.toDao(this);
    }
}
