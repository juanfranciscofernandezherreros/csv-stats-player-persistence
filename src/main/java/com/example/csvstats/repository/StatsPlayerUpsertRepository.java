package com.example.csvstats.repository;

import com.example.csvstats.entity.StatsPlayer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StatsPlayerUpsertRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO stats_player (
                id, match_id, name, team, pts, reb, ast, min,
                fgm, fga, twopm, twopa, threepm, threepa, ftm, fta,
                plus_minus, "or", dr, pf, st, "to", bs, ba, tfs, source_event_id
            ) VALUES (
                nextval('stats_player_seq'), ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            ON CONFLICT (match_id, name, team) DO UPDATE SET
                pts = EXCLUDED.pts,
                reb = EXCLUDED.reb,
                ast = EXCLUDED.ast,
                min = EXCLUDED.min,
                fgm = EXCLUDED.fgm,
                fga = EXCLUDED.fga,
                twopm = EXCLUDED.twopm,
                twopa = EXCLUDED.twopa,
                threepm = EXCLUDED.threepm,
                threepa = EXCLUDED.threepa,
                ftm = EXCLUDED.ftm,
                fta = EXCLUDED.fta,
                plus_minus = EXCLUDED.plus_minus,
                "or" = EXCLUDED."or",
                dr = EXCLUDED.dr,
                pf = EXCLUDED.pf,
                st = EXCLUDED.st,
                "to" = EXCLUDED."to",
                bs = EXCLUDED.bs,
                ba = EXCLUDED.ba,
                tfs = EXCLUDED.tfs,
                source_event_id = EXCLUDED.source_event_id
            """;

    private final JdbcTemplate jdbcTemplate;

    public StatsPlayerUpsertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsert(StatsPlayer player) {
        jdbcTemplate.update(
                UPSERT_SQL,
                player.getMatchId(),
                player.getName(),
                player.getTeam(),
                player.getPts(),
                player.getReb(),
                player.getAst(),
                player.getMin(),
                player.getFgm(),
                player.getFga(),
                player.getTwopm(),
                player.getTwopa(),
                player.getThreepm(),
                player.getThreepa(),
                player.getFtm(),
                player.getFta(),
                player.getPlusMinus(),
                player.getOr(),
                player.getDr(),
                player.getPf(),
                player.getSt(),
                player.getTo(),
                player.getBs(),
                player.getBa(),
                player.getTfs(),
                player.getSourceEventId()
        );
    }
}
