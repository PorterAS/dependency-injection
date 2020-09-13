SET search_path TO :app_schema;

CREATE TABLE orders (
    id          uuid PRIMARY KEY,
    date        timestamptz NOT NULL,
    comment     text,
    deviations  jsonb
);
