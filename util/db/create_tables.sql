SET search_path TO :app_schema;

CREATE TABLE orders (
    id          text PRIMARY KEY,
    date        timestamptz NOT NULL,
    deviations  jsonb NOT NULL
);
