CREATE TABLE usage_records
(
    hour               TIMESTAMP WITH TIME ZONE NOT NULL,
    community_produced DOUBLE PRECISION NOT NULL,
    community_used     DOUBLE PRECISION NOT NULL,
    grid_used          DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_usage_records PRIMARY KEY (hour)
);
