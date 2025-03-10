CREATE SCHEMA IF NOT EXISTS order_notifications;

CREATE TABLE IF NOT EXISTS order_notifications.notifications
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    order_id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    status character varying(255) COLLATE pg_catalog."default" NOT NULL,
    recipient character varying(255) COLLATE pg_catalog."default" NOT NULL,
    message text COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    is_sent boolean NOT NULL DEFAULT false,
    CONSTRAINT notifications_pkey PRIMARY KEY (id)
);