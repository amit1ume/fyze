INSERT INTO advisors (id, "name", advisor_type, is_nism_certified, is_sebi_registered, emails, phones, social_handles, is_active, created_at, updated_at)
VALUES
  (1, 'Akshat Shrivastav', 'INFLUENCER', FALSE, FALSE, '{"akshat@email.com"}', '{"9876543210"}', '[{"platform": "TWITTER", "handle": "@akshatshrivastav"}]', TRUE, NOW(), NOW()),
  (2, 'KotakSecurities', 'FIRM', TRUE, TRUE, '{"contact@kotaksecurities.com"}', '{"1112223333"}', '[{"platform": "TWITTER", "handle": "@kotaksecurities"}]', TRUE, NOW(), NOW());
