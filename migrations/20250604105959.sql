INSERT INTO publisher_role
VALUES (9056664623308800, null, 'Master', true, 'The king, can do everything and promote everyone');

WITH group_ins AS (
INSERT INTO publisher_permission_group (name, description)
VALUES ('Master', 'Can create or update games, exclude some sensitive data')
    RETURNING id),
    perm_ins AS (
INSERT INTO publisher_permission (group_id, name, description, authorities)
SELECT id,
    'Master',
    'Can do everything',
    '[
      "PUBLISHER_MASTER"
    ]'
FROM group_ins
    RETURNING id)

INSERT
INTO publisher_permission_role(publisher_permission_id, publisher_role_id)
SELECT id, 9056664623308800
FROM perm_ins;

INSERT INTO admin_role
VALUES (9056664623308801, 'Master', true, 'Super admin');

WITH admin_group_ins AS (
INSERT INTO admin_permission_group (name, description)
VALUES ('Master', 'Can do everything')
    RETURNING id),
    admin_perm_ins AS (
INSERT INTO admin_permission (group_id, name, description, authorities)
SELECT id,
    'Master',
    'Can do everything',
    '[
      "PUBLISHER_MASTER"
    ]'
FROM admin_group_ins
    RETURNING id)
INSERT INTO admin_permission_role(admin_role_id, admin_permission_id)
SELECT 9056664623308801, id
FROM admin_perm_ins;

INSERT INTO admin_account(id, username, password, email)
VALUES (9056664623308803,
        'superadmin',
        '$2a$14$sk8ki1ovZNV5fx9GqQOE.eRAsuiKwLMDmCYFEA5qA8zxR2j6AMlw2',
        'quocbao2k5xtt@gmail.com');

INSERT INTO admin_account_role(admin_account_id, admin_role_id)
VALUES (9056664623308803, 9056664623308801);
