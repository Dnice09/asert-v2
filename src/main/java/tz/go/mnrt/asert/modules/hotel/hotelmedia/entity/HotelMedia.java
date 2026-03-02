package tz.go.mnrt.asert.modules.hotel.hotelmedia.entity;

import lombok.*;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;
import tz.go.mnrt.asert.modules.user.entity.User;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_media")
public class HotelMedia extends BaseModel {

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @Column(name = "media_id")
    private Long mediaId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", insertable = false, updatable = false)
    private FileUpload attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false, insertable = false, updatable = false)
    private Hotel hotel;
}
