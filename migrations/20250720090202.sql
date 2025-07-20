INSERT INTO publisher_permission_group(name, description)
VALUES
    ('MEMBER MANAGEMENT', 'Manage members of the publisher'),
    ('GAME CONTENT MANAGEMENT', 'Manage content of games of the publisher'),
    ('GAME MANAGEMENT', 'Manage games of the publisher'),
    ('HUB MANAGEMENT', 'Manage hubs of the publisher'),
    ('LOG MANAGEMENT', 'Manage logs of the publisher'),
    ('STATISTICS MANAGEMENT', 'Manage statistics of the publisher'),
    ('PUBLISHER INFO MANAGEMENT', 'Manage publisher information');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'MEMBER MANAGEMENT'),
     'READ MEMBER', 'Create members of the publisher', '["PUBLISHER_READ_MEMBERS"]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'MEMBER MANAGEMENT'),
     'WRITE MEMBER', 'Update members of the publisher', '[
      "PUBLISHER_READ_MEMBERS",
      "PUBLISHER_WRITE_MEMBERS"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'MEMBER MANAGEMENT'),
     'MANAGE MEMBER', 'Manage members of the publisher', '[
      "PUBLISHER_READ_MEMBERS",
      "PUBLISHER_WRITE_MEMBERS",
      "PUBLISHER_DELETE_MEMBERS"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'GAME MANAGEMENT'),
     'READ GAME', 'Read games of the publisher', '["PUBLISHER_READ_GAMES"]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'GAME MANAGEMENT'),
     'WRITE GAME CONTENT', 'Update content of games of the publisher', '[
      "PUBLISHER_READ_GAMES",
      "PUBLISHER_WRITE_GAME_INFO"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'GAME MANAGEMENT'),
     'WRITE GAME', 'Update games of the publisher', '[
      "PUBLISHER_READ_GAMES",
      "PUBLISHER_CREATE_GAME",
      "PUBLISHER_WRITE_GAME_INFO",
      "PUBLISHER_WRITE_GAME_PRICE"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'GAME MANAGEMENT'),
     'MANAGE GAME', 'Manage games of the publisher', '[
      "PUBLISHER_READ_GAMES",
      "PUBLISHER_CREATE_GAME",
      "PUBLISHER_WRITE_GAME_INFO",
      "PUBLISHER_WRITE_GAME_PRICE",
      "PUBLISHER_MANAGE_GAMES"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'STATISTICS MANAGEMENT'),
        'READ GAME STATISTICS', 'Read game statistics of the publisher',
     '["PUBLISHER_READ_GAME_STATISTIC"]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'STATISTICS MANAGEMENT'),
        'READ REVENUE STATISTICS', 'Read revenue statistics of the publisher',
     '["PUBLISHER_READ_REVENUE_STATISTIC"]');


INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'HUB MANAGEMENT'),
     'READ HUB', 'Read hubs of the publisher', '["PUBLISHER_READ_HUBS"]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'HUB MANAGEMENT'),
     'WRITE HUB', 'Update hubs of the publisher', '[
      "PUBLISHER_READ_HUBS",
      "PUBLISHER_WRITE_HUBS"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'HUB MANAGEMENT'),
     'MANAGE HUB', 'Manage hubs of the publisher', '[
      "PUBLISHER_READ_HUB",
      "PUBLISHER_WRITE_HUB",
      "PUBLISHER_DELETE_HUB"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'PUBLISHER INFO MANAGEMENT'),
     'READ PUBLISHER INFO', 'Read information of the publisher', '["PUBLISHER_READ_INFO"]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'PUBLISHER INFO MANAGEMENT'),
     'WRITE PUBLISHER INFO', 'Update information of the publisher', '[
      "PUBLISHER_READ_INFO",
      "PUBLISHER_WRITE_INFO"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'PUBLISHER INFO MANAGEMENT'),
     'WRITE SENSITIVE PUBLISHER INFO', 'Write sensitive information of the publisher', '[
      "PUBLISHER_READ_SENSITIVE_INFO",
      "PUBLISHER_WRITE_SENSITIVE_INFO"
    ]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'LOG MANAGEMENT'),
     'READ MY LOGS', 'Read my logs of the publisher', '["PUBLISHER_READ_MY_LOGS"]');

INSERT INTO publisher_permission(group_id, name, description, authorities)
VALUES
    ((SELECT id FROM publisher_permission_group WHERE name = 'LOG MANAGEMENT'),
     'READ LOGS', 'Read logs of the publisher', '["PUBLISHER_READ_ALL_LOGS"]');
