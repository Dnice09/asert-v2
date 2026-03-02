alter table assessor_hotels
    drop constraint assessor_hotels_pkey;

alter table assessor_hotels
    add unique (assessor_id, hotel_id);

