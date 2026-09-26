# Changelog

## 1.2.0 - 2026-09-26

- [minor] KAN-53 sustituye `findByMatchIdAndNameAndTeam() + save()` por un upsert PostgreSQL atómico.
- [minor] Define `(match_id, name, team)` como clave natural respaldada por la constraint existente.
- [minor] Actualiza `source_event_id` con la reimportación que produjo el estado vigente.
- [minor] Añade tests de integración de insert/update y redelivery concurrente.

## 1.1.1 - 2026-09-26

- [patch] KAN-109 captura fallos de deserialización Avro mediante `ErrorHandlingDeserializer`.
- [patch] Permite publicar en DLT objetos Avro y bytes crudos mediante `DelegatingByTypeSerializer`.
- [patch] Deja que Kafka seleccione la partición DLT y propaga fallos de publicación en DLT.
- [patch] Añade cobertura del flujo deserialización fallida → DLT conservando los bytes originales.

## 1.1.0 - 2026-09-26

- [minor] KAN-109 aplica la estrategia común de errores Kafka de KAN-18.
- [minor] Clasifica errores de datos/integridad como non-retryable y fallos transitorios de PostgreSQL como retryable.
- [minor] Configura retries/backoff y DLT `stats-player.parsed.DLT`.
- [minor] Añade tests de error permanente y transitorio.


## 1.0.6 - 2026-09-25

- [patch] KAN-83 sustituye los schemas locales StatsPlayer por `basketball-event-contracts:1.0.2`.
- [patch] Elimina la generación Avro local y configura Maven/CI para leer GitHub Packages.
- [patch] Mantiene sin cambios los namespaces, campos y semántica Kafka existentes.

## 1.0.5 - 2026-09-25

- [patch] Refuerza AGENTS.md: lectura obligatoria por tarea, flujo autónomo y prohibición absoluta de escrituras directas en main.

## 1.0.4

- [patch] Estandariza la automatización del repositorio con el flujo autónomo de csv-results-parser.

## 1.0.3 - 2026-09-25

- [patch] Exige confirmar rama y nivel SemVer antes de cualquier cambio.
- [patch] Alinea Maven CI-friendly con revision, sha1 y changelist.

## 1.0.2 - 2026-09-24
- Corrige el escape inválido de las anotaciones JPA.
- Activa `hibernate.globally_quoted_identifiers` para soportar correctamente las columnas reservadas `"or"` y `"to"` de PostgreSQL.

## 1.0.1 - 2026-09-24
- Corrige el mapeo JPA de las columnas PostgreSQL reservadas `"or"` y `"to"`.

## 1.0.0 - 2026-09-24
- Separa la persistencia de STATS_PLAYER del consumer monolítico.
- Consume `stats-player.parsed` en Avro.
- Persiste en PostgreSQL `stats_player` con Flyway.
- Hace upsert por `(match_id, name, team)`.
