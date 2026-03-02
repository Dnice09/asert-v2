package tz.go.mnrt.asert.constants;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class Constant {
    public static final String API_V1 = "/api/v1";
    public static final String DEFAULT_SIZE = "10";
    public static final String DEFAULT_PAGE = "0";

    public static final String CREATE_SUCCESS = "CREATED_SUCCESSFULLY";
    public static final String UPLOAD_SUCCESS = "FILE_UPLOADED_SUCCESSFULLY";
    public static final String UPDATE_SUCCESS = "UPDATED_SUCCESSFULLY";
    public static final String DELETE_SUCCESS = "DELETED_SUCCESSFULLY";
    public static final String EXISTS = "ALREADY_EXISTS";
    public static final String MUST_BE_PRESENT = "MUST_BE_PRESENT";
    public static final String ENTITY_NOT_FOUND = "SPECIFIED_ENTITY_NOT_FOUND";
}
