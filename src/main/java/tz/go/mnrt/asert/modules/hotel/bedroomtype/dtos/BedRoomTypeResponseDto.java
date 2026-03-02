package tz.go.mnrt.asert.modules.hotel.bedroomtype.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.entity.BedRoomType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BedRoomTypeResponseDto {
    private Long id;
    //private UUID uuid;

    private String name;
    private String code;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    public BedRoomTypeResponseDto(BedRoomType entity) {
        BeanUtils.copyProperties(entity, this);
    }
}
