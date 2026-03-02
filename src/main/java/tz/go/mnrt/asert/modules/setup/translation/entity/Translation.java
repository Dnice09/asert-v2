package tz.go.mnrt.asert.modules.setup.translation.entity;

import lombok.*;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@Table(name = "translations")
@NoArgsConstructor
@AllArgsConstructor
public class Translation extends BaseModel {
  @Column(name = "displayLabel", unique = true)
  private String displayLabel;

  @Column(name = "english", unique = true)
  private String english;

  @Column(name = "swahili", unique = true)
  private String swahili;
}
