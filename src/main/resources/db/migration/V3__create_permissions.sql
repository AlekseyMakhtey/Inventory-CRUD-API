CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_permissions (
    user_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, permission_id),

    CONSTRAINT fk_user_permissions_user
        FOREIGN KEY (user_id)
        REFERENCES app_users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

INSERT INTO permissions (name) VALUES
    ('ITEM_READ'),
    ('ITEM_CREATE'),
    ('ITEM_UPDATE'),
    ('ITEM_DELETE');

INSERT INTO user_permissions (user_id, permission_id)
SELECT u.id, p.id
FROM app_users u
CROSS JOIN permissions p
WHERE u.role = 'ADMINISTRATOR';

INSERT INTO user_permissions (user_id, permission_id)
SELECT u.id, p.id
FROM app_users u
JOIN permissions p ON p.name = 'ITEM_READ'
WHERE u.role = 'VIEWER';
