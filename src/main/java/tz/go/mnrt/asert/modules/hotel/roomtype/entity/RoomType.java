package tz.go.mnrt.asert.modules.hotel.roomtype.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.entity.BedRoomType;
import tz.go.mnrt.asert.modules.hotel.bedtype.entity.BedType;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room_types")
public class RoomType extends BaseModel {
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "size")
    private Double size;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_type_id", nullable = false)
    private BedType type;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_room_type_id", nullable = false)
    private BedRoomType bedRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ElementCollection
    @CollectionTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_type_id"))
    @Column(name = "amenity")
    private Set<String> amenities = new HashSet<>();
}
