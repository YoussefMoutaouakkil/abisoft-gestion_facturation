
CREATE TABLE IF NOT EXISTS menuitem (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL,
    menu_key VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    target VARCHAR(255),
    service VARCHAR(255),
    grid_def VARCHAR(255),
    tooltip VARCHAR(255),
    image VARCHAR(255),
    expanded BOOLEAN
);