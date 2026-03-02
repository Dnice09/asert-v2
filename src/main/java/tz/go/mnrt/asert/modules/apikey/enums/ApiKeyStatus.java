package tz.go.mnrt.asert.modules.apikey.enums;

import java.io.Serializable;

@SuppressWarnings("unused")
public enum ApiKeyStatus implements Serializable {
    REGISTERED,
    APPROVED,
    DEACTIVATED,
    RETIRED
}
