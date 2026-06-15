CREATE TABLE percentage_records
(
    hour                 TIMESTAMP WITH TIME ZONE NOT NULL,
    community_depleted   DOUBLE PRECISION NOT NULL,
    grid_portion         DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_percentage_records PRIMARY KEY (hour)
);
