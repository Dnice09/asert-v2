package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import lombok.*;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponseMinDto {
    private Long id;
    private String name;

    public HotelResponseMinDto(Hotel entity) {
        this.id = entity.getId();
        this.name = entity.getName().concat(" (").concat(entity.getCompany().getName()).concat(")")
                .concat(" [Date Applied: ")
                .concat(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))).concat("]");
        if (entity.getAdminHierarchy() != null) {
            this.name = this.name.concat(" (").concat(entity.getCompany().getName()).concat(")").concat(" [")
                    .concat(entity.getAdminHierarchy().getName()).concat("]").concat(" [Date Applied: ")
                    .concat(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))).concat("]");
        }
    }
}
