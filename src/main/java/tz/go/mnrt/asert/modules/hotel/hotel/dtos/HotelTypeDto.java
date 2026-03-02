package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import lombok.*;

import javax.validation.Valid;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class HotelTypeDto implements Serializable {
    private String id;
    private String name;
}
