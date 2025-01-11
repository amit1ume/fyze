CREATE TABLE advisors (
    id BIGSERIAL NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    advisor_type VARCHAR(255) NOT NULL,
    is_nism_certified BOOLEAN NOT NULL,
    is_sebi_registered BOOLEAN NOT NULL,
    emails TEXT[] NULL,
    phones TEXT[] NULL,
    social_handles JSONB NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_advisors_id PRIMARY KEY (id)
);
