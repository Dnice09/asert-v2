package tz.go.mnrt.asert.modules.hotel.bedtype.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.bedtype.entity.BedType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BedTypeRequestDto {
    private Long id;
    private UUID uuid;

    private String name;

    private String description;

    public BedTypeRequestDto(BedType entity) {
        entity.toDao(this);
    }
}
