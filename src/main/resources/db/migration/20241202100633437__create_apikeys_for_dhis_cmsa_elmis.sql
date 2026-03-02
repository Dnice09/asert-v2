INSERT INTO public.api_keys(
	uuid, created_at, created_by, is_deleted, system_name, description, expiry_date, api_key, status, code, contact_email, is_approved, system_ip, is_retired)
	VALUES (gen_random_uuid(), now(), 'Seeder', false, 'DHIS2', 'District Health Information System', now() + interval '1 year', 'hEHeJNCW_mSTMbe8LyesH2XUa93HTmCepgtTorM2JSQ', 'REGISTERED', 'HMIS_DHIS', 'info@mohz.go.tz', false, '41.59.190.190', false);
INSERT INTO public.api_keys(
	uuid, created_at, created_by, is_deleted, system_name, description, expiry_date, api_key, status, code, contact_email, is_approved, system_ip, is_retired)
	VALUES (gen_random_uuid(), now(), 'Seeder', false, 'CMSA System', 'Central Medical Stores Agency System', now() + interval '1 year', 'jGs5hC2HHfpoaWCw_QthXU3_eB6RMUwd698kSccQkgE', 'REGISTERED', 'CMS_AGENCY', 'info@cmsa.go.tz', false, '41.59.190.191', false);
INSERT INTO public.api_keys(
	uuid, created_at, created_by, is_deleted, system_name, description, expiry_date, api_key, status, code, contact_email, is_approved, system_ip, is_retired)
	VALUES (gen_random_uuid(), now(), 'Seeder', false, 'ELMIS', 'Electronic Logistics Management System', now() + interval '1 year', '-fwQYBl5uY78SjCehbQcEbafQ-P-wbbJpfm3pWE-Qxo', 'REGISTERED', 'CMO_ELMIS', 'info@cmo.mohz.go.tz', false, '41.59.190.192', false);