-- Seed data for testing. Only inserts if the agency_code does not already exist.
INSERT INTO agents (agency_code, name, role, consent_given, is_registered, created_at, updated_at)
SELECT 'AG001', 'Rajesh Kumar', 'AGENT', false, false, NOW(), NOW()
FROM dual WHERE NOT EXISTS (SELECT 1 FROM agents WHERE agency_code = 'AG001');

INSERT INTO agents (agency_code, name, role, consent_given, is_registered, created_at, updated_at)
SELECT 'AG002', 'Priya Sharma', 'DO', false, false, NOW(), NOW()
FROM dual WHERE NOT EXISTS (SELECT 1 FROM agents WHERE agency_code = 'AG002');

INSERT INTO agents (agency_code, name, role, consent_given, is_registered, created_at, updated_at)
SELECT 'AG003', 'Amit Patel', 'CLIA', false, false, NOW(), NOW()
FROM dual WHERE NOT EXISTS (SELECT 1 FROM agents WHERE agency_code = 'AG003');

INSERT INTO agents (agency_code, name, role, consent_given, is_registered, created_at, updated_at)
SELECT 'AG004', 'Sneha Reddy', 'LICA', false, false, NOW(), NOW()
FROM dual WHERE NOT EXISTS (SELECT 1 FROM agents WHERE agency_code = 'AG004');
