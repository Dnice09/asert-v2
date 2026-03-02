alter table assessors
    alter column identification_id drop not null;

alter table assessors
    alter column identification_type drop not null;

alter table assessors
    alter column rejection_reason_id drop not null;

alter table assessors
    alter column photo drop not null;






