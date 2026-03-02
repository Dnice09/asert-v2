package tz.go.mnrt.asert.modules.apikey.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_keys")
public class ApiKey extends BaseModel {
    @Column(name = "system_name", nullable = false)
    private String systemName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "expiry_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiryDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @Column(name = "is_retired", nullable = false)
    private Boolean isRetired;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "system_ip")
    private String systemIp;

    @Column(name = "is_published", nullable = true)
    private Boolean isPublished;
}
