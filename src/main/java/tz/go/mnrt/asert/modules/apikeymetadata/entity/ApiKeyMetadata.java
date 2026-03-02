package tz.go.mnrt.asert.modules.apikeymetadata.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.apikey.entity.ApiKey;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_key_metadata")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class ApiKeyMetadata extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id", nullable = false, foreignKey = @ForeignKey(name = "fk_api_key_metadata_api_key"))
    private ApiKey apiKey;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "usage_count", nullable = false)
    private Long usageCount;

    @Column(name = "created_ip")
    private String createdIp;

    @Type(type = "json")
    @Column(name = "allowed_ips", columnDefinition = "json")
    private List<String> allowedIps;

    @Column(name = "rate_limit", nullable = false)
    private Integer rateLimit;

    @Column(name = "rate_limit_reset", nullable = false)
    private LocalDateTime rateLimitReset;
}
