# A.L.P.H.A.

**A**utonomous **L**earning **P**roactive **H**euristic **A**gent

*"Agente autónomo que aprende, se adelanta y te jode con inteligencia."*

---

Asistente de IA para Android que corre 24/7 usando AccessibilityService, Overlay y Gemini 2.5 Flash.

## Stack

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| Arquitectura | MVVM |
| DB local | Room |
| Async | Corrutinas + StateFlow |
| Overlay | WindowManager |
| Acciones | AccessibilityService |
| Captura pantalla | MediaProjection |
| Modelo | Gemini 2.5 Flash |
| Build | Kotlin DSL + Gradle |

## Fases

- **Fase 0** — Esqueleto: build system, manifest, servicios base
- **Fase 1** — Memoria: Room DB + eventos de accesibilidad
- **Fase 2** — Overlay: burbuja flotante
- **Fase 3** — Alpha: conexión Gemini 2.5 Flash + personalidad
- **Fase 4** — Acciones: TAP, SWIPE, TYPE, BACK, HOME
- **Fase 5** — Captura de pantalla con MediaProjection
- **Fase 6** — Highlight: dibujar en pantalla
- **Fase 7** — Patrones: detección de comportamiento
- **Fase 8** — Trigger: intervenciones proactivas
- **Fase 9** — Voz: TTS/STT (opt-in)
