-- Metadata DHIS System
INSERT INTO public.api_key_metadata(
	uuid, created_at, created_by, updated_at, updated_by, is_deleted, api_key_id, allowed_ips, rate_limit, rate_limit_reset, created_ip)
	VALUES (gen_random_uuid(), now(), 'Seeder', now(), 'Kizito', false, (SELECT id from public.api_keys WHERE code='HMIS_DHIS'), '[' || (SELECT system_ip from public.api_keys WHERE code='HMIS_DHIS') || ']', 1000, (SELECT 'tomorrow'::TIMESTAMP), (SELECT system_ip from public.api_keys WHERE code='HMIS_DHIS'));

-- Metadata CMSA System
INSERT INTO public.api_key_metadata(
	uuid, created_at, created_by, updated_at, updated_by, is_deleted, api_key_id, allowed_ips, rate_limit, rate_limit_reset, created_ip)
	VALUES (gen_random_uuid(), now(), 'Seeder', now(), 'Kizito', false, (SELECT id from public.api_keys WHERE code='CMS_AGENCY'), '[' || (SELECT system_ip from public.api_keys WHERE code='CMS_AGENCY') || ']', 1000, (SELECT 'tomorrow'::TIMESTAMP), (SELECT system_ip from public.api_keys WHERE code='CMS_AGENCY'));

-- Metadata ELMIS System
INSERT INTO public.api_key_metadata(
	uuid, created_at, created_by, updated_at, updated_by, is_deleted, api_key_id, allowed_ips, rate_limit, rate_limit_reset, created_ip)
	VALUES (gen_random_uuid(), now(), 'Seeder', now(), 'Kizito', false, (SELECT id from public.api_keys WHERE code='CMO_ELMIS'), '[' || (SELECT system_ip from public.api_keys WHERE code='CMO_ELMIS') || ']', 1000, (SELECT 'tomorrow'::TIMESTAMP), (SELECT system_ip from public.api_keys WHERE code='CMO_ELMIS'));