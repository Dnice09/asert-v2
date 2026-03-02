package tz.go.mnrt.asert.modules.setup.documenttype.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.setup.documenttype.entity.DocumentType;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String name;

    public DocumentDto(DocumentType entity) {
        entity.toDao(this);
    }
}
