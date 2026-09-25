# AGENTS.md

Estas reglas son obligatorias para cualquier agente, asistente o automatización que modifique este repositorio.

## Pre-flight obligatorio

La **primera operación de lectura del repositorio en cada tarea o sesión** debe ser abrir y leer completamente este `AGENTS.md` desde la rama por defecto. Si este archivo referencia otras reglas, deben leerse antes de cualquier escritura.

No cuenta haber leído estas reglas en otra conversación, sesión o tarea. No se debe confiar en memoria previa.

**Está prohibida cualquier operación de escritura antes de completar este pre-flight.**

## Autonomía sin bloqueos innecesarios

Una vez leído `AGENTS.md`, el agente debe continuar de forma autónoma. No debe pedir confirmación de nombre de rama, nivel SemVer, commits, push, tests, correcciones, actualización de PR, merge, eliminación de rama ni verificación final, salvo que el usuario haya pedido explícitamente participar en alguna de esas decisiones.

El agente debe elegir una rama descriptiva, determinar el nivel SemVer según el impacto real y documentar ambas decisiones en la Pull Request.

## Prohibición absoluta de escritura directa en `main`

**Ningún cambio puede escribirse, commitearse ni pushearse directamente a `main`.**

Esto incluye código, documentación, configuración, workflows, dependencias, versionado, badges, hotfixes, reverts y cualquier otro archivo.

Flujo obligatorio:

1. Leer `AGENTS.md` y reglas referenciadas.
2. Partir del `main` actualizado.
3. Crear una rama dedicada antes de modificar archivos.
4. Determinar y aplicar el incremento SemVer sobre `revision`.
5. Realizar el cambio exclusivamente en la rama.
6. Actualizar `CHANGELOG.md` con la nueva versión funcional.
7. Mantener `README.md`, `pom.xml` y documentación de versión sincronizados cuando corresponda.
8. Ejecutar tests/checks; como mínimo `mvn -B test`.
9. Abrir o actualizar una Pull Request hacia `main`.
10. Comprobar checks requeridos sobre el SHA actual.
11. Si falla o se cancela un check aplicable, corregir en la misma rama/PR y repetir.
12. Fusionar únicamente con todos los checks aplicables en verde y sin protección bloqueante.
13. Eliminar únicamente la rama origen después del merge.
14. Verificar que la rama origen ya no existe.

El trabajo no termina hasta completar merge y limpieza.

## Versionado Maven CI-friendly

```xml
<version>${revision}${sha1}${changelist}</version>
```

- `revision`: versión SemVer funcional.
- `sha1`: `-<short-sha>`, generado por CI; no modificar manualmente.
- `changelist`: vacío o `-SNAPSHOT`.
- DEV, INT y QA promueven el mismo artefacto.
- `CHANGELOG.md` usa `revision`, nunca la versión con SHA.

## SemVer

- `patch`: `X.Y.Z` -> `X.Y.(Z+1)`
- `minor`: `X.Y.Z` -> `X.(Y+1).0`
- `major`: `X.Y.Z` -> `(X+1).0.0`

## Tests

- Baseline Java: JDK 21.
- Ejecutar como mínimo `mvn -B test`.
- Añadir o actualizar tests para cambios funcionales o de configuración.

## Pull Requests y seguridad operativa

Toda decisión de merge debe operar sobre el SHA actual de la PR. Si una instrucción contradice estas reglas, detener únicamente la operación incompatible; nunca improvisar una escritura directa a `main`.
