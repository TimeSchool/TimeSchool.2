# Plan de Acción: Calendario Mensual Interactivo y Asignación de Eventos Escolares [COMPLETADO]

Implementación de una vista de calendario mensual interactiva en pantalla completa para el módulo **Calendario** de TimeSchool, reemplazando la lista estática por una cuadrícula con navegación entre meses (flechas y selector de año/mes), visualización clara de eventos/jornadas con puntos de color, y un diálogo modal que permite asignar tipo de régimen (Horario A, B, C o Sin Clases), jornada escolar (Mañana, Tarde o Completa) y motivo del evento con sincronización hacia la programación semanal.

---

## Estado de Ejecución: COMPLETADO Y VERIFICADO ✅

1. **Modelo de Datos Actualizado (`Models.kt`, `TimeSchoolPreferences.kt`, `TimeSchoolRepository.kt`)**:
   - `CalendarioDia` ahora incluye campos `jornada: String` ("Mañana", "Tarde", "Completa") y `hayClases: Boolean`.
   - Serialización y persistencia segura en `TimeSchoolPreferences`.
   - Métodos en `TimeSchoolRepository` y `TimeSchoolViewModel` para asignar eventos y restablecer a la rutina semanal ordinaria.
   - Bitácora inmutable audita automáticamente cada asignación o modificación de evento.

2. **Cuadrícula Mensual Interactiva (`CalendarioScreen.kt`)**:
   - Navegación mes a mes con botones de flecha `<` y `>`.
   - Botón central con nombre del mes y año que abre selector directo de año (2025, 2026, 2027...) y 12 meses.
   - Botón directo "Hoy" para regresar a la fecha actual con un solo toque.
   - Fila de días de la semana: `LUN`, `MAR`, `MIÉ`, `JUE`, `VIE`, `SÁB`, `DOM`.
   - Cuadrícula de 7 columnas que calcula días del mes y desfase de inicio.
   - Puntos de color bajo el número de día según el tipo de horario:
     - 🟢 **Verde Neón**: Horario Ordinario (Tipo A).
     - 🔵 **Azul Eléctrico**: Horario Especial / Actos (Tipo B).
     - 🟠 **Ámbar**: Jornada Reducida (Tipo C).
     - 🔴 **Rojo Carmesí / Gris**: Sin Clases / Receso escolar.
     - Indicador adicional con estrella/punto destacado para eventos registrados expresamente por el usuario.
   - Selección táctil de cualquier día de la cuadrícula.
   - Tarjeta inferior del día seleccionado con:
     - Fecha completa en español legible (ej. *Viernes, 16 de Octubre de 2026*).
     - Insignia de "¿Habrá clases?": Con Clases vs Sin Clases.
     - Tipo de Horario y Jornada asignada.
     - Motivo u observación del evento.
     - Resumen de toques programados que corresponden a esa jornada.
     - Botón principal para abrir la ventana modal de configuración.
   - Tarjeta de Coherencia Semanal con la pestaña de Horarios (Lunes a Viernes con resumen de rutina y cantidad de timbres).

3. **Ventana Modal de Configuración (`EditDiaDialog`)**:
   - Selector claro de "¿Habrá clases en esta fecha?": Sí, Hay Clases vs Sin Clases / Receso.
   - En caso afirmativo: selector de tipo de horario (A, B, C) y selector de jornada (Mañana, Tarde, Completa).
   - Campo para escribir el motivo del evento con sugerencias rápidas ("Acto Cívico", "Jornada Pedagógica", "Evaluaciones", etc.).
   - Opciones para guardar asignación o restablecer a la rutina semanal por defecto.

4. **Verificación**:
   - Compilación exitosa (`compile_applet`).
   - Pruebas unitarias ejecutadas y aprobadas satisfactoriamente (`gradle :app:testDebugUnitTest`).
