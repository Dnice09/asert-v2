-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

UPDATE bed_types SET description = 'A spacious king-size bed, ideal for couples or guests who prefer extra room.'
WHERE name = 'KING';

UPDATE bed_types SET description = 'A comfortable queen-size bed, perfect for solo travelers or couples.'
WHERE name = 'QUEEN';

UPDATE bed_types SET description = 'Two separate twin beds, great for sharing without compromising personal space.'
WHERE name = 'TWIN';

UPDATE bed_types SET description = 'A standard double bed suitable for single or double occupancy.'
WHERE name = 'DOUBLE';

UPDATE bed_types SET description = 'A single bed ideal for one person, commonly found in budget or child-friendly rooms.'
WHERE name = 'SINGLE';

UPDATE bed_types SET description = 'Stacked twin beds, perfect for kids or shared accommodations in hostels or family suites.'
WHERE name = 'BUNK BEDS';

UPDATE bed_types SET description = 'A sofa that folds out into a bed, useful in multi-purpose or compact spaces.'
WHERE name = 'SOFA BED';

UPDATE bed_types SET description = 'A wall-mounted bed that folds away when not in use, ideal for maximizing space in smaller rooms.'
WHERE name = 'MURPHY BED';
