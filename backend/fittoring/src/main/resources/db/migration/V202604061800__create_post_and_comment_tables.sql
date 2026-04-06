CREATE TABLE post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    member_id BIGINT NULL,
    nickname VARCHAR(50) NOT NULL,
    guest_password VARCHAR(255) NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(6) NULL,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    member_id BIGINT NULL,
    nickname VARCHAR(50) NOT NULL,
    guest_password VARCHAR(255) NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    root_id BIGINT NULL,
    parent_id BIGINT NULL,
    created_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(6) NULL,
    FOREIGN KEY (post_id) REFERENCES post(id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (root_id) REFERENCES comment(id),
    FOREIGN KEY (parent_id) REFERENCES comment(id)
);

CREATE INDEX idx_post_created_at ON post(created_at DESC, id DESC);
CREATE INDEX idx_comment_post_id ON comment(post_id);
CREATE INDEX idx_comment_root_id ON comment(root_id);
