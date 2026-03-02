package tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelMediaResponseDto {
    private Long id;
    private UUID uuid;

    private String description;
    private Boolean isApproved;

    private Long mediaId;
    private String mediaUrl;
    private String mediaFileName;
    private String mediaFileType;

    private Long hotelId;
    private String hotelName;

    private Long userId;
    private String userName;

    private Boolean isDefault;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public HotelMediaResponseDto(HotelMedia entity, String uploadedFilesUrl) {
        BeanUtils.copyProperties(entity, this);

        if (entity.getHotel() != null) {
            this.hotelId = entity.getHotel().getId();
            this.hotelName = entity.getHotel().getName();
        }

        if (entity.getUser() != null) {
            this.userId = entity.getUser().getId();
            this.userName = entity.getUser().getCreatedBy(); // Assuming there's a name field in User
        }

        if (entity.getAttachment() != null) {
            this.mediaId = entity.getAttachment().getId();
            this.mediaFileName = entity.getAttachment().getName();
            this.mediaFileType = entity.getAttachment().getFileType();
            // Return URL instead of base64 data for performance
            this.mediaUrl = "/api/v1/uploads/" + entity.getAttachment().getUuid() + "/view";
        }
    }
}
