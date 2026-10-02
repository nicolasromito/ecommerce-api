INSERT INTO app_user (username, email, password, role_id)
SELECT 'admin', 'admin@ecommerce.local',
       '$2b$10$OCHEiJa/LpVlsewiKs5CqOJW2jwaTz.Egnq8HqvoILpYKUiW3oAB2',
       id
FROM role
WHERE name = 'ADMIN';