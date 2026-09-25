Current version: **1.0.3**

# csv-stats-player-persistence

Microservicio de persistencia separado de `csv-stats-player-consumer`.

```text
stats-player.parsed -> ParsedStatsPlayerConsumer -> JPA -> PostgreSQL stats_player
```

Consume exactamente `StatsPlayerKey` / `StatsPlayerValue` publicados por `csv-stats-player-parser`.

La tabla conserva la estructura histórica `stats_player` y la unicidad `(match_id, name, team)`. Ante una reentrega, el servicio hace upsert y conserva el último `source_event_id`.

Hibernate usa identificadores entrecomillados para mantener compatibilidad con las columnas históricas `"or"` y `"to"` de PostgreSQL.

Variables: `DB_URL`, `DB_USER`, `DB_PASS`, `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_SCHEMA_REGISTRY_URL`, `KAFKA_PARSED_STATS_PLAYER_TOPIC`.

Tests: `mvn -B test`. Integración PostgreSQL: `mvn -B verify -Pintegration`.
