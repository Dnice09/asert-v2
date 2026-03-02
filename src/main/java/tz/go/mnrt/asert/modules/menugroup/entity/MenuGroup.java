package tz.go.mnrt.asert.modules.menugroup.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "menu_groups")
@FilterDef(
    name = "inId",
    parameters = {
      @ParamDef(name = "itemIds", type = "long"),
      @ParamDef(name = "ids", type = "long"),
    })
@Filter(name = "inId", condition = "id IN (:ids)")
public class MenuGroup extends BaseModel {

  @Column(name = "name")
  private String name;

  @Column(name = "icon")
  private String icon;

  @Column(name = "state")
  private String state;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @Column(name = "translation_label")
  private String translationLabel;

  @OneToMany(mappedBy = "menuGroup")
  @Filter(name = "inId", condition = "id IN (:itemIds)")
  @JsonIgnoreProperties({"menuGroup"})
  private Set<MenuItem> menuItems;

  public MenuGroup(Long id, String name, String icon, String state, Integer sortOrder) {
    this.setId(id);
    this.name = name;
    this.icon = icon;
    this.state = state;
    this.sortOrder = sortOrder;
  }
}
