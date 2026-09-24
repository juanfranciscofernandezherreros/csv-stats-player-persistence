CREATE SEQUENCE stats_player_seq START WITH 1 INCREMENT BY 100;

CREATE TABLE stats_player (
 id BIGINT PRIMARY KEY,
 match_id VARCHAR(255) NOT NULL,
 name VARCHAR(255) NOT NULL,
 team VARCHAR(255) NOT NULL,
 pts INTEGER, reb INTEGER, ast INTEGER, min VARCHAR(255),
 fgm INTEGER, fga INTEGER, twopm INTEGER, twopa INTEGER,
 threepm INTEGER, threepa INTEGER, ftm INTEGER, fta INTEGER,
 plus_minus INTEGER, "or" INTEGER, dr INTEGER, pf INTEGER, st INTEGER, "to" INTEGER,
 bs INTEGER, ba INTEGER, tfs INTEGER,
 source_event_id VARCHAR(255) NOT NULL,
 CONSTRAINT uk_stats_player_match_player_team UNIQUE (match_id,name,team)
);

CREATE INDEX idx_stats_player_match_id ON stats_player(match_id);
