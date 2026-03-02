package tz.go.mnrt.asert.modules.adminhierarchy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminHierarchyMinDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String name;
    private String code;
}
