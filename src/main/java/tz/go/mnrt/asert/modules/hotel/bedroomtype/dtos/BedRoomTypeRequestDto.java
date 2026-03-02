package tz.go.mnrt.asert.modules.hotel.bedroomtype.dtos;

import java.util.UUID;

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
public class BedRoomTypeRequestDto {
    private Long id;
    private UUID uuid;

    private String name;
    private String code;

    private String description;

    public BedRoomTypeRequestDto(BedRoomType entity) {
        entity.toDao(this);
    }
}
