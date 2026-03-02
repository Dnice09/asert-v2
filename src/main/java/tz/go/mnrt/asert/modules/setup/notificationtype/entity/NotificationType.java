package tz.go.mnrt.asert.modules.setup.notificationtype.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_types")
public class NotificationType extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

}
