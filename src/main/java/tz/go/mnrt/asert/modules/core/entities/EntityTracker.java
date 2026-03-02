package tz.go.mnrt.asert.modules.core.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.enums.EntityAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "entity_trackers")
public class EntityTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "action", nullable = false)
    private EntityAction action;

    @Column(name = "trackable_type", nullable = false)
    private String trackableType;

    @Column(name = "trackable_id", nullable = false)
    private Long trackableId;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    public EntityTracker(EntityAction action, String typeName, String state, Long id, String createdBy) {
        this.action = action;
        this.trackableType = typeName;
        this.state = state;
        this.trackableId = id;
        this.createdBy = createdBy;
    }
}
