package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HotelAssessorsAssignmentRequestDto {
    private List<Long> assessorIds;
}
