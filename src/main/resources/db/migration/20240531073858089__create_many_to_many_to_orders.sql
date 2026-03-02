CREATE TABLE order_services
(
    order_id BIGINT NOT NULL,
    service_id      BIGINT NOT NULL,
    CONSTRAINT pk_order_services PRIMARY KEY (order_id, service_id)
);

CREATE TABLE order_attachments
(
    order_id BIGINT NOT NULL,
    attachment_id      BIGINT NOT NULL,
    CONSTRAINT pk_order_attachments PRIMARY KEY (order_id, attachment_id)
);
