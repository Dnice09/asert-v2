-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- assessor tables - Stores assessors info

CREATE TABLE education_levels
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID         NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN,
    name       VARCHAR(255) NOT NULL
);

CREATE TABLE institutes
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID         NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN,
    name       VARCHAR(255) NOT NULL,
    country_id    BIGINT       NOT NULL REFERENCES admin_hierarchies (id) ON DELETE CASCADE
);


CREATE TABLE assessors
(
    id                         BIGSERIAL PRIMARY KEY,
    uuid                       UUID          NOT NULL UNIQUE,
    created_at                 TIMESTAMP WITHOUT TIME ZONE,
    created_by                 VARCHAR(255),
    updated_at                 TIMESTAMP WITHOUT TIME ZONE,
    updated_by                 VARCHAR(255),
    is_deleted                 BOOLEAN,
    date_applied               TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    status                     VARCHAR(20) DEFAULT 'Pending',
    title                      VARCHAR(128)  NOT NULL,
    first_name                 VARCHAR(128)  NOT NULL,
    middle_name                VARCHAR(128)  NOT NULL,
    identification_id          VARCHAR(128)  NOT NULL,
    identification_type        VARCHAR(128)  NOT NULL,
    gender                     VARCHAR(10)   NOT NULL,
    phone                      VARCHAR(128)  NOT NULL,
    photo                      VARCHAR(255)  NOT NULL,
    dob                        DATE          NOT NULL,
    email                      VARCHAR(255)  NOT NULL,
    user_id                    BIGINT UNIQUE NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    location_id                BIGINT        NOT NULL REFERENCES admin_hierarchies (id) ON DELETE CASCADE,
    verification_notes         TEXT,
    verified_by                BIGINT,
    date_verified              TIMESTAMP,
    rejection_reason           TEXT
);


CREATE TABLE assessor_references
(
    id           SERIAL PRIMARY KEY,
    assessor_id  BIGINT REFERENCES assessors (id) ON DELETE CASCADE,
    name         VARCHAR(255) not null,
    phone        VARCHAR(255) not null,
    email        VARCHAR(255) not null,
    relationship TEXT
);

CREATE TABLE assessor_education_history
(
    id                 SERIAL PRIMARY KEY,
    assessor_id        BIGINT NOT NULL REFERENCES assessors (id) ON DELETE CASCADE,
    institute_id       BIGINT NOT NULL REFERENCES institutes (id),
    from_date          DATE   NOT NULL,
    to_date            DATE,
    education_level_id BIGINT NOT NULL REFERENCES education_levels (id),
    graduated          BOOLEAN DEFAULT FALSE,
    certificate        TEXT -- file path or description
);

CREATE TABLE assessor_employment_history
(
    id SERIAL PRIMARY KEY,
    assessor_id   INT          NOT NULL REFERENCES assessors (id) ON DELETE CASCADE,
    company  VARCHAR(255) NOT NULL,
    from_date     DATE         NOT NULL,
    to_date       DATE,
    position_held VARCHAR(255) NOT NULL
);



-- Foreign key indexes
CREATE INDEX idx_assessors_user_id ON assessors(user_id);
CREATE INDEX idx_assessors_location_id ON assessors(location_id);
CREATE INDEX idx_education_history_assessor_id ON assessor_education_history(assessor_id);
CREATE INDEX idx_education_history_institute_id ON assessor_education_history(institute_id);
CREATE INDEX idx_education_history_level_id ON assessor_education_history(education_level_id);
CREATE INDEX idx_employment_history_assessor_id ON assessor_employment_history(assessor_id);
CREATE INDEX idx_references_assessor_id ON assessor_references(assessor_id);



-- Assessor status should only be valid states
ALTER TABLE assessors ADD CONSTRAINT chk_assessor_status
    CHECK (status IN ('Pending', 'Approved', 'Rejected'));

-- Gender validation (adjust as needed)
ALTER TABLE assessors ADD CONSTRAINT chk_assessor_gender
    CHECK (gender IN ('Male', 'Female'));






