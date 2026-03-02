-- Certificate Courses
INSERT INTO education_courses (uuid, is_deleted, name, education_level_id, created_at, updated_at)
VALUES
    (gen_random_uuid(), false, 'Certificate in Tourism Management', 1, NOW(), NOW()),
    (gen_random_uuid(), false, 'Certificate in Travel & Tour Operations', 1, NOW(), NOW()),
    (gen_random_uuid(), false, 'Certificate in Hotel Operations', 1, NOW(), NOW());

-- Diploma Courses
INSERT INTO education_courses (uuid,is_deleted, name, education_level_id, created_at, updated_at)
VALUES
    (gen_random_uuid(), false, 'Diploma in Tourism and Travel Management', 2, NOW(), NOW()),
    (gen_random_uuid(), false, 'Diploma in Hotel and Hospitality Management', 2, NOW(), NOW()),
    (gen_random_uuid(), false, 'Diploma in Culinary Arts', 2, NOW(), NOW());

-- Advanced Diploma
INSERT INTO education_courses (uuid,is_deleted, name, education_level_id, created_at, updated_at)
VALUES
    (gen_random_uuid(), false, 'Advanced Diploma in Hotel Management', 3, NOW(), NOW()),
    (gen_random_uuid(), false, 'Advanced Diploma in Tourism Development', 3, NOW(), NOW());

-- Bachelor Degree Courses
INSERT INTO education_courses (uuid,is_deleted, name, education_level_id, created_at, updated_at)
VALUES
    (gen_random_uuid(), false, 'Bachelor of Tourism Management', 4, NOW(), NOW()),
    (gen_random_uuid(), false, 'Bachelor of Hospitality and Hotel Management', 4, NOW(), NOW()),
    (gen_random_uuid(), false, 'Bachelor of Travel and Tour Operations', 4, NOW(), NOW()),
    (gen_random_uuid(), false, 'Bachelor of International Hospitality Business', 4, NOW(), NOW()),
    (gen_random_uuid(), false, 'Bachelor of Sustainable Tourism Management', 4, NOW(), NOW());

-- Masters Courses
INSERT INTO education_courses (uuid,is_deleted, name, education_level_id, created_at, updated_at)
VALUES
    (gen_random_uuid(), false, 'Masters in Tourism and Hospitality Management', 6, NOW(), NOW()),
    (gen_random_uuid(), false, 'Masters in Sustainable Tourism', 6, NOW(), NOW()),
    (gen_random_uuid(), false, 'MBA in Hospitality Leadership', 6, NOW(), NOW());

-- Doctorate Courses
INSERT INTO education_courses (uuid,is_deleted, name, education_level_id, created_at, updated_at)
VALUES
    (gen_random_uuid(), false, 'PhD in Tourism and Hotel Management', 7, NOW(), NOW()),
    (gen_random_uuid(), false, 'PhD in Hospitality and Leisure Studies', 7, NOW(), NOW());
