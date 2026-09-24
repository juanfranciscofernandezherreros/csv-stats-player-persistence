# Changelog

## 1.0.0 - 2026-09-24
- Separa la persistencia de STATS_PLAYER del consumer monolítico.
- Consume `stats-player.parsed` en Avro.
- Persiste en PostgreSQL `stats_player` con Flyway.
- Hace upsert por `(match_id, name, team)`.
