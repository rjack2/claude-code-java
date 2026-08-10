CREATE TABLE movie (
    id            BIGINT          PRIMARY KEY,
    title         VARCHAR(500)    NOT NULL,
    vote_average  DOUBLE PRECISION,
    vote_count    INTEGER,
    release_date  DATE,
    revenue       BIGINT,
    runtime       INTEGER,
    backdrop_path VARCHAR(500),
    budget        BIGINT,
    homepage      TEXT,
    overview      TEXT,
    popularity    DOUBLE PRECISION,
    poster_path   VARCHAR(500),
    genres        TEXT[],
    themes        TEXT[]
);

COPY movie
FROM '/docker-entrypoint-initdb.d/movie.csv'
WITH (FORMAT CSV, HEADER, DELIMITER ',');