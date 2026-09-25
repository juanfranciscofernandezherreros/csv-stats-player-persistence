# Changelog

## 1.0.5 - 2026-09-25

- [patch] Refuerza AGENTS.md: lectura obligatoria por tarea, flujo autónomo y prohibición absoluta de escrituras directas en main.

## 1.0.4

- [patch] Estandariza la automatización del repositorio con el flujo autónomo de csv-results-parser.

## 1.0.3 - 2026-09-25

- [patch] Exige confirmar rama y nivel SemVer antes de cualquier cambio.
- [patch] Alinea Maven CI-friendly con revision, sha1 y changelist.

## 1.0.2 - 2026-09-24
- Corrige el escape inválido de las anotaciones JPA.
- Activa `hibernate.globally_quoted_identifiers` para soportar correctamente las columnas reservadas `"or"` y `"to"`.

## 1.0.1 - 2026-09-24
- Corrige el mapeo JPA de las columnas PostgreSQL reservadas `"or"` y `"to"`.

## 1.0.0 - 2026-09-24
- Separa la persistencia de STATS_PLAYER del consumer monolítico.
- Consume `stats-player.parsed` en Avro.
- Persiste en PostgreSQL `stats_player` con Flyway.
- Hace upsert por `(match_id, name, team)`.
