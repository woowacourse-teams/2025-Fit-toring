CREATE TABLE member
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id     VARCHAR(255) NOT NULL UNIQUE,
    gender       VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    `role`       VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    password     LONGTEXT NULL
);

CREATE TABLE mentoring
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    price        INT          NOT NULL,
    career       INT NULL,
    content      TEXT         NOT NULL,
    introduction VARCHAR(255) NOT NULL,
    mentor_id    BIGINT       NOT NULL,
    CONSTRAINT fk_mentoring_member FOREIGN KEY (mentor_id) REFERENCES member (id)
);

CREATE TABLE category
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE category_mentoring
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id  BIGINT NOT NULL,
    mentoring_id BIGINT NOT NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_category_mentoring FOREIGN KEY (mentoring_id) REFERENCES mentoring (id) ON DELETE CASCADE
);

CREATE TABLE certificate
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    type                VARCHAR(255) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    verification_status VARCHAR(255) NOT NULL,
    created_at          DATETIME     NOT NULL,
    mentoring_id        BIGINT       NOT NULL,
    CONSTRAINT fk_certificate_mentoring FOREIGN KEY (mentoring_id) REFERENCES mentoring (id) ON DELETE CASCADE
);

CREATE TABLE image
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    url         TEXT         NOT NULL,
    image_type  VARCHAR(255) NOT NULL,
    relation_id BIGINT       NOT NULL
);

CREATE TABLE phone_verification
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(255) NOT NULL,
    expire_at    DATETIME     NOT NULL,
    phone_number VARCHAR(255) NOT NULL
);

CREATE TABLE refresh_token
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT       NOT NULL,
    token_value VARCHAR(255) NOT NULL,
    create_at   DATETIME     NOT NULL,
    CONSTRAINT fk_refresh_token_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE reservation
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    content      VARCHAR(255) NULL,
    created_at   DATETIME NOT NULL,
    status       SMALLINT NOT NULL,
    mentoring_id BIGINT   NOT NULL,
    mentee_id    BIGINT   NOT NULL,
    CONSTRAINT fk_reservation_mentoring FOREIGN KEY (mentoring_id) REFERENCES mentoring (id) ON DELETE CASCADE,
    CONSTRAINT fk_reservation_member FOREIGN KEY (mentee_id) REFERENCES member (id)
);

CREATE TABLE review
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating         TINYINT  NOT NULL,
    content        TEXT     NOT NULL,
    created_at     DATETIME NOT NULL,
    reservation_id BIGINT   NOT NULL UNIQUE,
    mentee_id      BIGINT   NOT NULL,
    CONSTRAINT fk_review_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_member FOREIGN KEY (mentee_id) REFERENCES member (id)
);
