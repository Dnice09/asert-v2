-- Migration to update rating_criteria table for property type specific criteria

-- Drop the existing table to recreate with new structure
DROP TABLE IF EXISTS rating_criteria CASCADE;

-- Create the new rating_criteria table with property type relationship
CREATE TABLE rating_criteria (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    property_type VARCHAR(50) NOT NULL,
    star_level INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    min_score DOUBLE PRECISION NOT NULL,
    max_score DOUBLE PRECISION NOT NULL,
    total_possible_score DOUBLE PRECISION NOT NULL,
    criteria_description TEXT,
    percentage_required DOUBLE PRECISION,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    -- Ensure unique combination of property type and star level
    CONSTRAINT unique_property_type_star_level UNIQUE (property_type, star_level)
);

-- Add indexes for better performance
CREATE INDEX idx_rating_criteria_property_type ON rating_criteria(property_type);
CREATE INDEX idx_rating_criteria_star_level ON rating_criteria(star_level);
CREATE INDEX idx_rating_criteria_property_star ON rating_criteria(property_type, star_level);

-- Insert the rating criteria data based on the CSV structure
-- VACATION HOTEL - Total Score 5,560
INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text, 'VACATION_HOTEL', 1, '1 Star Vacation Hotel', 1125.0, 2250.0, 5560.0, '100% on essential items + minimum 50% points out of possible 2,250 score', 50.0),
(gen_random_uuid()::text, 'VACATION_HOTEL', 2, '2 Star Vacation Hotel', 1464.0, 2440.0, 5560.0, '100% on essential items + minimum 60% points out of possible 2,440 score', 60.0),
(gen_random_uuid()::text, 'VACATION_HOTEL', 3, '3 Star Vacation Hotel', 2508.0, 4180.0, 5560.0, '100% on essential items + minimum 30% per section + minimum 60% of 4,180 points', 60.0),
(gen_random_uuid()::text, 'VACATION_HOTEL', 4, '4 Star Vacation Hotel', 3984.0, 4980.0, 5560.0, '100% on essential items + minimum 40% per section + 80% of 4,980 points', 80.0),
(gen_random_uuid()::text, 'VACATION_HOTEL', 5, '5 Star Vacation Hotel', 4448.0, 5560.0, 5560.0, '100% on essential items + minimum 50% per section + minimum 80% of 5,560 points', 80.0);

-- LODGE - Total Score 4,830
INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'LODGE', 1, '1 Star Lodge', 1040.0, 2080.0, 4830.0, '100% on essential items + minimum 50% points out of possible 2,080 score', 50.0),
(gen_random_uuid()::text::text, 'LODGE', 2, '2 Star Lodge', 1374.0, 2290.0, 4830.0, '100% on essential items + minimum 60% points out of possible 2,290 score', 60.0),
(gen_random_uuid()::text::text, 'LODGE', 3, '3 Star Lodge', 2208.0, 3680.0, 4830.0, '100% on essential items + minimum 30% per section + minimum 60% of 3,680 points', 60.0),
(gen_random_uuid()::text::text, 'LODGE', 4, '4 Star Lodge', 3045.0, 4350.0, 4830.0, '100% on essential items + minimum 40% per section + 70% of 4,350 points', 70.0),
(gen_random_uuid()::text::text, 'LODGE', 5, '5 Star Lodge', 3864.0, 4830.0, 4830.0, '100% on essential items + minimum 50% per section + minimum 80% of 4,830 points', 80.0);

-- VILLA, COTTAGE, SERVICED_APARTMENT - Total Score 2,130
INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'VILLA', 1, '1 Star Villa', 570.0, 1140.0, 2130.0, '100% on essential items + minimum 50% points out of possible 1,140 score', 50.0),
(gen_random_uuid()::text::text, 'VILLA', 2, '2 Star Villa', 756.0, 1260.0, 2130.0, '100% on essential items + minimum 60% points out of possible 1,260 score', 60.0),
(gen_random_uuid()::text::text, 'VILLA', 3, '3 Star Villa', 978.0, 1630.0, 2130.0, '100% on essential items + minimum 30% per section + minimum 60% of 1,630 points', 60.0),
(gen_random_uuid()::text::text, 'VILLA', 4, '4 Star Villa', 1330.0, 1900.0, 2130.0, '100% on essential items + minimum 40% per section + 70% of 1,900 points', 70.0),
(gen_random_uuid()::text::text, 'VILLA', 5, '5 Star Villa', 1704.0, 2130.0, 2130.0, '100% on essential items + minimum 50% per section + minimum 80% of 2,130 points', 80.0);

INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'COTTAGE', 1, '1 Star Cottage', 570.0, 1140.0, 2130.0, '100% on essential items + minimum 50% points out of possible 1,140 score', 50.0),
(gen_random_uuid()::text::text, 'COTTAGE', 2, '2 Star Cottage', 756.0, 1260.0, 2130.0, '100% on essential items + minimum 60% points out of possible 1,260 score', 60.0),
(gen_random_uuid()::text::text, 'COTTAGE', 3, '3 Star Cottage', 978.0, 1630.0, 2130.0, '100% on essential items + minimum 30% per section + minimum 60% of 1,630 points', 60.0),
(gen_random_uuid()::text::text, 'COTTAGE', 4, '4 Star Cottage', 1330.0, 1900.0, 2130.0, '100% on essential items + minimum 40% per section + 70% of 1,900 points', 70.0),
(gen_random_uuid()::text::text, 'COTTAGE', 5, '5 Star Cottage', 1704.0, 2130.0, 2130.0, '100% on essential items + minimum 50% per section + minimum 80% of 2,130 points', 80.0);

INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'SERVICED_APARTMENT', 1, '1 Star Serviced Apartment', 570.0, 1140.0, 2130.0, '100% on essential items + minimum 50% points out of possible 1,140 score', 50.0),
(gen_random_uuid()::text::text, 'SERVICED_APARTMENT', 2, '2 Star Serviced Apartment', 756.0, 1260.0, 2130.0, '100% on essential items + minimum 60% points out of possible 1,260 score', 60.0),
(gen_random_uuid()::text::text, 'SERVICED_APARTMENT', 3, '3 Star Serviced Apartment', 978.0, 1630.0, 2130.0, '100% on essential items + minimum 30% per section + minimum 60% of 1,630 points', 60.0),
(gen_random_uuid()::text::text, 'SERVICED_APARTMENT', 4, '4 Star Serviced Apartment', 1330.0, 1900.0, 2130.0, '100% on essential items + minimum 40% per section + 70% of 1,900 points', 70.0),
(gen_random_uuid()::text::text, 'SERVICED_APARTMENT', 5, '5 Star Serviced Apartment', 1704.0, 2130.0, 2130.0, '100% on essential items + minimum 50% per section + minimum 80% of 2,130 points', 80.0);

-- MOTEL - Total Score 3,050 (only 1-3 stars)
INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'MOTEL', 1, '1 Star Motel', 1100.0, 2200.0, 3050.0, '100% on essential items + minimum 50% points out of possible 2,200 score', 50.0),
(gen_random_uuid()::text::text, 'MOTEL', 2, '2 Star Motel', 1464.0, 2440.0, 3050.0, '100% on essential items + minimum 60% points out of possible 2,440 score', 60.0),
(gen_random_uuid()::text::text, 'MOTEL', 3, '3 Star Motel', 1830.0, 3050.0, 3050.0, '100% on essential items + minimum 30% per section + minimum 60% of 3,050 points', 60.0);

-- TOWN_HOTEL - Total Score 5,530
INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'TOWN_HOTEL', 1, '1 Star Town Hotel', 1075.0, 2150.0, 5530.0, '100% on essential items + minimum 50% points out of possible 2,150 score', 50.0),
(gen_random_uuid()::text::text, 'TOWN_HOTEL', 2, '2 Star Town Hotel', 1434.0, 2390.0, 5530.0, '100% on essential items + minimum 60% points out of possible 2,390 score', 60.0),
(gen_random_uuid()::text::text, 'TOWN_HOTEL', 3, '3 Star Town Hotel', 2484.0, 4140.0, 5530.0, '100% on essential items + minimum 30% per section + minimum 60% of 4,140 points', 60.0),
(gen_random_uuid()::text::text, 'TOWN_HOTEL', 4, '4 Star Town Hotel', 3976.0, 4970.0, 5530.0, '100% on essential items + minimum 40% per section + 80% of 4,970 points', 80.0),
(gen_random_uuid()::text::text, 'TOWN_HOTEL', 5, '5 Star Town Hotel', 4424.0, 5530.0, 5530.0, '100% on essential items + minimum 50% per section + minimum 80% of 5,530 points', 80.0);

-- RESTAURANT - Total Score 1,680 (only 3-5 stars)
INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'RESTAURANT', 3, '3 Star Restaurant', 762.0, 1270.0, 1680.0, '100% on essential items + minimum 60% points out of possible 1,270 score', 60.0),
(gen_random_uuid()::text::text, 'RESTAURANT', 4, '4 Star Restaurant', 1036.0, 1480.0, 1680.0, '100% on essential items + minimum 40% per section + minimum 70% of 1,480 points', 70.0),
(gen_random_uuid()::text::text, 'RESTAURANT', 5, '5 Star Restaurant', 1344.0, 1680.0, 1680.0, '100% on essential items + minimum 50% per section + minimum 80% of 1,680 points', 80.0);

-- TENTED_CAMP (using similar structure to LODGE as they are similar property types)
INSERT INTO rating_criteria (uuid, property_type, star_level, name, min_score, max_score, total_possible_score, criteria_description, percentage_required) VALUES
(gen_random_uuid()::text::text, 'TENTED_CAMP', 1, '1 Star Tented Camp', 1040.0, 2080.0, 4830.0, '100% on essential items + minimum 50% points out of possible 2,080 score', 50.0),
(gen_random_uuid()::text::text, 'TENTED_CAMP', 2, '2 Star Tented Camp', 1374.0, 2290.0, 4830.0, '100% on essential items + minimum 60% points out of possible 2,290 score', 60.0),
(gen_random_uuid()::text::text, 'TENTED_CAMP', 3, '3 Star Tented Camp', 2208.0, 3680.0, 4830.0, '100% on essential items + minimum 30% per section + minimum 60% of 3,680 points', 60.0),
(gen_random_uuid()::text::text, 'TENTED_CAMP', 4, '4 Star Tented Camp', 3045.0, 4350.0, 4830.0, '100% on essential items + minimum 40% per section + 70% of 4,350 points', 70.0),
(gen_random_uuid()::text::text, 'TENTED_CAMP', 5, '5 Star Tented Camp', 3864.0, 4830.0, 4830.0, '100% on essential items + minimum 50% per section + minimum 80% of 4,830 points', 80.0);

COMMENT ON TABLE rating_criteria IS 'Property type specific star rating criteria with scoring thresholds';
COMMENT ON COLUMN rating_criteria.property_type IS 'Type of accommodation property (VACATION_HOTEL, LODGE, VILLA, etc.)';
COMMENT ON COLUMN rating_criteria.star_level IS 'Star rating level (1-5 stars)';
COMMENT ON COLUMN rating_criteria.total_possible_score IS 'Maximum possible score for this property type';
COMMENT ON COLUMN rating_criteria.percentage_required IS 'Minimum percentage required for this star level';