package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UploadPhotoDto implements Serializable {
    private Long id;
    private String photo;
}
