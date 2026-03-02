package tz.go.mnrt.asert.modules.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "reports")
public class Report extends BaseModel {

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @ManyToOne()
    @JsonIgnore
    private Report parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Report> children;
}
