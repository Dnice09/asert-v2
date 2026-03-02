INSERT INTO institutes (uuid, name, created_at, updated_at,country_id)
VALUES
    (gen_random_uuid(), 'University of Dar es Salaam', NOW(), NOW(),1),
    (gen_random_uuid(), 'Ardhi University', NOW(), NOW(),1),
    (gen_random_uuid(), 'Mzumbe University', NOW(), NOW(),1),
    (gen_random_uuid(), 'Sokoine University of Agriculture', NOW(), NOW(),1),
    (gen_random_uuid(), 'Nelson Mandela African Institution of Science and Technology', NOW(), NOW(),1);
