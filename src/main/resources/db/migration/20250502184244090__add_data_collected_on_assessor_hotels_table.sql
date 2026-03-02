ALTER TABLE assessor_hotels
    ADD COLUMN IF NOT EXISTS data_collected boolean not null default false;
