# MANUAL DE USUARIO OFICIAL: SISTEMA TIMESCHOOL
## Sistema de Timbre Escolar Automatizado con Enlace IoT y Control de Horarios
**Versión de la Aplicación:** 1.0 (Android Native / Jetpack Compose)  
**Institución Documentada:** Institución Educativa Técnico Industrial San José (Sede Principal - Campus Central)  
**Año Lectivo:** 2026  
**Fecha de Elaboración:** Octubre de 2026  
**Perfil del Manual:** Técnico, Operativo y Administrativo  

---

## TABLA DE CONTENIDO

1. [Portada e Información General](#1-portada-e-información-general)
2. [Introducción y Justificación del Sistema](#2-introducción-y-justificación-del-sistema)
3. [Objetivos del Manual](#3-objetivos-del-manual)
   - 3.1. Objetivo General
   - 3.2. Objetivos Específicos
4. [Descripción General de la Aplicación](#4-descripción-general-de-la-aplicación)
   - 4.1. ¿Para qué sirve TimeSchool?
   - 4.2. Características Principales y Arquitectura Híbrida
   - 4.3. Perfiles de Usuario Destinatarios
5. [Requisitos y Condiciones de Uso](#5-requisitos-y-condiciones-de-uso)
   - 5.1. Requisitos del Dispositivo Móvil
   - 5.2. Requisitos del Hardware Escolar (ESP32 y Campana)
   - 5.3. Permisos del Sistema Operativo Android
6. [Instalación, Acceso e Inicio de la Aplicación](#6-instalación-acceso-e-inicio-de-la-aplicación)
   - 6.1. Instalación del Paquete APK
   - 6.2. Primer Inicio y Verificación de Permisos
   - 6.3. Identificación de la Sesión y Conmutador de Roles
7. [Descripción Detallada de la Interfaz](#7-descripción-detallada-de-la-interfaz)
   - 7.1. Barra Superior (TopAppBar)
   - 7.2. Barra Inferior de Navegación (BottomNavigationBar)
   - 7.3. Paleta Visual e Identidad Dark Tech
8. [Guía de Uso Paso a Paso por Módulos](#8-guía-de-uso-paso-a-paso-por-módulos)
   - 8.1. Módulo 1: Panel Central (Dashboard y Reloj Sincronizado)
   - 8.2. Módulo 2: Configuración Semanal de Horarios (Lunes a Viernes)
   - 8.3. Módulo 3: Programación y Gestión de Toques de Timbre
   - 8.4. Módulo 4: Calendario Escolar y Asignación Fecha a Fecha
   - 8.5. Módulo 5: Enlace IoT Bluetooth Classic SPP y Toque Manual
   - 8.6. Módulo 6: Alarma de Evacuación y Protocolo Antidisparos
   - 8.7. Módulo 7: Bitácora Inmutable de Auditoría
   - 8.8. Módulo 8: Configuración Institucional y Control de Permisos
9. [Catálogo de Mensajes, Validaciones y Solución de Errores](#9-catálogo-de-mensajes-validaciones-y-solución-de-errores)
10. [Recomendaciones de Seguridad y Buenas Prácticas](#10-recomendaciones-de-seguridad-y-buenas-prácticas)
11. [Preguntas Frecuentes (FAQ)](#11-preguntas-frecuentes-faq)
12. [Glosario de Términos Técnicos](#12-glosario-de-términos-técnicos)
13. [Conclusiones del Sistema](#13-conclusiones-del-sistema)

---

## 1. PORTADA E INFORMACIÓN GENERAL

- **Nombre del Proyecto:** TimeSchool - Sistema de Timbre Escolar Automatizado
- **Tipo de Documento:** Manual de Usuario Oficial (Técnico, Operativo y de Configuración)
- **Desarrollo y Plataforma:** Aplicativo Móvil Android Nativo desarrollado en Kotlin con Jetpack Compose y Material Design 3, microcontrolador ESP32 con almacenamiento no volátil en Flash y base de datos Cloud Firestore.
- **Institución Educativa:** Institución Educativa Técnico Industrial San José
- **Sede:** Sede Principal - Campus Central
- **Vigencia:** Año Lectivo 2026
- **Estado de la Aplicación:** Versión Terminada y Desplegada en Formato APK (`TimeSchool-v1.0-debug.apk`)

---

## 2. INTRODUCCIÓN Y JUSTIFICACIÓN DEL SISTEMA

En el entorno escolar cotidiano, la puntualidad en el cambio de clases, el inicio de descansos pedagógicos y la alerta inmediata en situaciones de emergencia son factores críticos para la convivencia y la seguridad institucional. Tradicionalmente, las instituciones dependen de timbres manuales propensos a olvidos humanos o sistemas cableados obsoletos que se desprograman ante cortes eléctricos.

**TimeSchool** surge como una solución integral que combina tecnología móvil moderna con hardware IoT autónomo:
1. **Autonomía Total en Hardware:** Un microcontrolador ESP32 conectado al relé de la campana electromecánica almacena en su memoria Flash todas las matrices de horarios. Una vez programado, no requiere conexión a Internet ni presencia del teléfono para hacer sonar la campana con precisión de reloj de tiempo real (RTC).
2. **Control Móvil Inmediato:** Mediante comunicación Bluetooth Classic (SPP), el personal docente y directivo puede sincronizar tablas, ajustar minutos de timbre, accionar toques manuales táctiles y disparar alarmas de evacuación.
3. **Persistencia Garantizada:** Todas las modificaciones semanales, calendarios y datos institucionales se almacenan de manera no volátil en el dispositivo móvil y en la nube (Cloud Firestore), evitando la pérdida de configuraciones al reiniciar el equipo.
4. **Auditoría Inmutable:** Cada toque programado, acción manual o conmutación queda registrada con nombre del operador, hora exacta y estado de ejecución, impidiendo cualquier manipulación o borrado no autorizado.

---

## 3. OBJETIVOS DEL MANUAL

### 3.1. Objetivo General
Instruir detalladamente al personal directivo, administrativo, de coordinación y de apoyo operativo en la configuración, supervisión, sincronización y manejo integral de la aplicación móvil **TimeSchool**, garantizando la operación autónoma de la campana escolar y el uso seguro de sus alarmas.

### 3.2. Objetivos Específicos
1. Guiar la instalación y navegación fluida a través de las 6 secciones de la aplicación.
2. Explicar el procedimiento para asignar y guardar la rutina semanal escolar (Lunes a Viernes con Horarios Tipo A, B o C).
3. Capacitar al usuario en la creación, edición, duración y desactivación de toques de timbre específicos.
4. Instruir en el enlace inalámbrico Bluetooth SPP con el hardware ESP32 y la transferencia de tablas horarias a la memoria Flash.
5. Detallar el protocolo de seguridad antidisparos para la activación y detención de la alarma de evacuación institucional.
6. Enseñar a consultar, filtrar y verificar los registros de auditoría de la bitácora inmutable.
7. Clarificar las diferencias y alcances de permisos entre el rol de **Administrador** y el rol de **Directivo**.

---

## 4. DESCRIPCIÓN GENERAL DE LA APLICACIÓN

### 4.1. ¿Para qué sirve TimeSchool?
TimeSchool es el centro de mando móvil del timbre del colegio. Permite:
- Programar de manera visual los horarios de entrada, cambios de bloque, recesos y salida.
- Asignar qué horario regirá cada día de la semana (rutina escolar) y modificar fechas especiales en el calendario del año lectivo.
- Transferir dichas programaciones al microcontrolador ESP32 para que este opere de forma 100% independiente sin internet.
- Tocar la campana manualmente desde cualquier punto del colegio cubierto por Bluetooth.
- Disparar una alarma de evacuación intermitente continua (2 segundos activo / 1 segundo silencio) ante sismos, conatos de incendio o emergencias institucionales.
- Monitorear en tiempo real cuántos toques están programados y cuál es el próximo timbre a sonar.

### 4.2. Características Principales y Arquitectura Híbrida
- **Interfaz Dark Tech:** Diseñada con una paleta de alto contraste inspirada en la circuitería del logotipo TimeSchool (`#0B131F`, verde neón `#4EFA94`, cian `#38BDF8` y carmesí `#EF4444`).
- **Resumen Compacto:** Lista de toques delgada y legible que reduce el espacio visual en más del 40%, facilitando la lectura completa de la jornada escolar.
- **Reloj Digital Sincronizado:** Indicador de hora en formato monoespaciado en tiempo real con cuenta regresiva al siguiente evento.
- **Protocolo Serial SPP:** Comunicación estándar sin dependencias de redes Wi-Fi locales bajo el UUID `00001101-0000-1000-8000-00805F9B34FB`.

### 4.3. Perfiles de Usuario Destinatarios
1. **Administrador:** Personal directivo o de sistemas con control total. Puede editar la información institucional del colegio (nombre, sede, año lectivo), crear/modificar programaciones, sincronizar hardware y alternar perfiles.
2. **Directivo / Coordinador:** Personal de coordinación académica o disciplinaria. Puede consultar y modificar horarios semanales, asignar calendarios y registrar toques, pero tiene restringida la modificación de la identidad institucional.
3. **Personal de Apoyo / Guardia:** Operadores encargados del accionamiento de toques imprevistos o de la activación de emergencias mediante el módulo IoT.

---

## 5. REQUISITOS Y CONDICIONES DE USO

### 5.1. Requisitos del Dispositivo Móvil
- **Sistema Operativo:** Android 7.0 (Nougat / API 24) o superior (probado y compatible hasta Android 14 y 15 / API 36).
- **Conectividad:** Bluetooth Classic compatible con el perfil SPP (Serial Port Profile).
- **Almacenamiento Libre:** Mínimo 50 MB para la instalación del APK y almacenamiento de bases de datos locales.
- **Pantalla:** Diseñada de forma adaptativa para teléfonos móviles, plegables y tabletas.

### 5.2. Requisitos del Hardware Escolar
- **Módulo Central:** Microcontrolador ESP32 con firmware TimeSchool instalado.
- **Módulo de Relé:** Relé optoacoplado de 5V/10A conectado a la campana electromecánica del colegio.
- **Alimentación Eléctrica:** Fuente continua para el ESP32 (5V DC) con respaldo opcional de batería o UPS.
- **Identificador de Enlace:** Nombre Bluetooth por defecto `ESP32_TIMESCHOOL_BELL`.

### 5.3. Permisos del Sistema Operativo Android
Para operar adecuadamente, la aplicación requiere los siguientes permisos (declarados en el sistema):
- `BLUETOOTH_CONNECT` y `BLUETOOTH_SCAN`: Necesarios en Android 12+ para buscar y conectar el ESP32.
- `BLUETOOTH` y `BLUETOOTH_ADMIN`: Compatibilidad con versiones anteriores a Android 12.
- `VIBRATE`: Respuesta táctil háptica en los botones de timbre manual y confirmaciones.
- `INTERNET` y `ACCESS_NETWORK_STATE`: Enlace opcional de sincronización con Cloud Firestore.

---

## 6. INSTALACIÓN, ACCESO E INICIO DE LA APLICACIÓN

### 6.1. Instalación del Paquete APK
1. Transfiera el archivo instalador `TimeSchool-v1.0-debug.apk` (ubicado en el directorio `/apk/` del proyecto) a su teléfono inteligente o descárguelo directamente desde el almacenamiento.
2. Abra el archivo en el explorador de su dispositivo. Si el sistema muestra la advertencia *"Instalar aplicaciones de fuentes desconocidas"*, habilite la autorización para su explorador de archivos.
3. Presione **Instalar** y espere a que Android complete la verificación.
4. Identifique en su menú de aplicaciones el icono circular distintivo de **TimeSchool** (campana verde neón con dial tecnológico sobre fondo oscuro).

### 6.2. Primer Inicio
Al pulsar el icono de la aplicación:
1. Se inicializa el servicio local de persistencia (`TimeSchoolPreferences`).
2. Se cargan las matrices horarias base (Tipo A, B y C) y el año lectivo institucional (2026).
3. Se inicia automáticamente el reloj digital sincronizado en el panel principal.

### 6.3. Identificación de la Sesión y Conmutador de Roles
En la esquina superior derecha de la pantalla siempre estará visible una pastilla distintiva de rol:
- **Verde Neón con icono de escudo:** Indica rol **ADMINISTRADOR** (Sesión activa: Carlos Mendoza).
- **Azul Cian con icono escolar:** Indica rol **DIRECTIVO** (Sesión activa: Marta Rojas).
- Al pulsar dicha pastilla, la aplicación conmuta inmediatamente de perfil, permitiendo probar y auditar en tiempo real las restricciones de acceso y seguridad.

---

## 7. DESCRIPCIÓN DETALLADA DE LA INTERFAZ

La interfaz de TimeSchool sigue un esquema de navegación moderno de una sola pantalla con 6 pestañas principales accesibles desde la barra inferior, complementada por una barra superior fija de estado.

```
┌──────────────────────────────────────────────────────────────┐
│ [Logo] TimeSchool • IoT Timbre [BT Pill] [ADMINISTRADOR]     │ <- TopAppBar
├──────────────────────────────────────────────────────────────┤
│                                                              │
│                   ÁREA DE CONTENIDO DINÁMICO                 │
│         (Panel / Horarios / Calendario / IoT / ...)          │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│  [■] Panel  [🕒] Horarios  [📅] Calendario  [🔔] IoT  ...    │ <- BottomBar
└──────────────────────────────────────────────────────────────┘
```

### 7.1. Barra Superior (TopAppBar)
Contiene 4 elementos clave visibles en todo momento:
1. **Isotipo Oficial TimeSchool:** Icono circular con campana iluminada y trazos de microcircuito.
2. **Nombre Institucional:** Texto dinámico que refleja el colegio configurado (`Institución Educativa Técnico Industrial San José`).
3. **Píldora de Estado Bluetooth:** Muestra en tiempo real si el teléfono está *Conectado (SPP)*, *Conectando...*, o *Desconectado* junto a un LED indicador de color. Al pulsarlo, conmuta la conexión con el hardware.
4. **Insignia de Rol:** Etiqueta interactiva con el nivel de privilegios del usuario actual.

### 7.2. Barra Inferior de Navegación (BottomNavigationBar)
Permite el desplazamiento inmediato entre las 6 áreas funcionales:
- **Panel (Dashboard):** Vista global, reloj digital, telemetría y toques inmediatos.
- **Horarios:** Configuración semanal de Lunes a Viernes (A, B, C) y lista compacta de timbres.
- **Calendario:** Asignación diaria fecha a fecha para el año lectivo.
- **IoT Timbre:** Accionamiento manual de campana y disparo de alarma de evacuación.
- **Bitácora:** Historial inmutable de auditoría con buscador y filtros.
- **Sede:** Información institucional y administración de perfiles.

---

## 8. GUÍA DE USO PASO A PASO POR MÓDULOS

---

### 8.1. MÓDULO 1: PANEL CENTRAL (DASHBOARD Y RELOJ SINCRONIZADO)

Este módulo es la pantalla principal que recibe al usuario al abrir la aplicación. Proporciona una vista rápida y consolidada de toda la operación escolar.

#### Figura 1. Pantalla Principal: Panel Central y Reloj Digital Sincronizado
*(Consulte la imagen `image.png` número 1 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  ● SISTEMA AUTÓNOMO ACTIVO       ESP32: Conectado (SPP) ●    │
│                                                              │
│                      12 : 55 : 18                            │
│               Hora sincronizada con RTC y Servidor           │
│                                                              │
│  [🔔] Próximo Timbre:                                        │
│       13:40 - Cambio de Clase - Hora 7 (35s)                 │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Muestra el estado operativo de la campana, el reloj en tiempo real, el próximo toque programado con cuenta regresiva y los botones de acción rápida.

#### Figura 2. Barra de Navegación Inferior: Acceso al Panel
*(Consulte la imagen `image.png` número 2 cargada en el proyecto)*  
*Descripción:* Pestaña **Panel** seleccionada con indicador verde neón activo.

#### Figura 3. Telemetría Operativa y Lista de Eventos Recientes
*(Consulte la imagen `image.png` número 3 cargada en el proyecto)*

*Descripción:* Tarjetas con métricas de toques activos (135 programados), memoria Flash del ESP32 (135 tablas grabadas), último enlace IoT (Bluetooth SPP Classic) y últimos eventos auditados con enlace directo a la bitácora completa.

#### Procedimiento para Monitorear el Sistema y Ejecutar Acciones Inmediatas:
1. Abra la aplicación y sitúese en la pestaña **Panel**.
2. Verifique en el banner superior que el mensaje indique **SISTEMA AUTÓNOMO ACTIVO**.
3. Observe el reloj digital central: la hora se actualiza segundo a segundo y coincide con la hora del microcontrolador RTC.
4. Revise el recuadro **Próximo Timbre**: indica la hora exacta, el bloque escolar que finaliza o inicia y la duración de la campana.
5. **Para activar un toque de cortesía inmediato (3 segundos):**
   - En la sección *Acciones Inmediatas*, pulse el botón verde **Toque 3s**.
   - El relé del ESP32 se activará durante exactamente 3 segundos y se apagará de forma automática.
   - En la parte inferior aparecerá un mensaje de confirmación y el evento se registrará en la bitácora.
6. **Para sincronizar rápidamente los horarios con la campana:**
   - Pulse el botón azul cian **Sincroniza**.
   - La aplicación transmitirá las tablas al ESP32 y confirmará: *"Sincronización exitosa: horarios transmitidos al ESP32"*.

---

### 8.2. MÓDULO 2: CONFIGURACIÓN SEMANAL DE HORARIOS

Este módulo resuelve la organización pedagógica de la semana escolar, permitiendo definir qué régimen de clases regirá cada día (Lunes a Viernes) y almacenar dicha rutina de forma permanente.

#### Figura 4. Módulo de Horarios: Régimen Escolar Semanal y Botón Guardar
*(Consulte la imagen `image.png` número 4 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  [📅] Régimen Escolar Semanal                                │
│  Asigna el tipo de horario (A, B o C) para cada día...       │
│                                                              │
│  [📅] Lunes        [ Horario A ▼ ]                           │
│  [📅] Martes       [ Horario B ▼ ]                           │
│  [📅] Miércoles    [ Horario A ▼ ]                           │
│  [📅] Jueves       [ Horario B ▼ ]                           │
│  [📅] Viernes      [ Horario C ▼ ]                           │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │               💾  GUARDAR HORARIO                      │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Interfaz interactiva donde el usuario puede desplegar las opciones de horario para cada día de la semana y presionar el botón verde neón de guardado definitivo.

#### Figura 5. Barra de Navegación Inferior: Acceso a Horarios
*(Consulte la imagen `image.png` número 5 cargada en el proyecto)*  
*Descripción:* Pestaña **Horarios** activa en la barra de navegación.

#### Procedimiento para Establecer y Guardar la Rutina Semanal:
1. En la barra inferior, pulse el icono **Horarios**.
2. En la tarjeta superior *Régimen Escolar Semanal*, identifique la fila correspondiente al día que desea ajustar.
3. Pulse sobre la pastilla desplegable a la derecha del día (ejemplo: `[ Horario A ▼ ]`).
4. Seleccione en el menú flotante el régimen escolar deseado:
   - **Horario A (Ordinario):** Jornada estándar completa con 7 bloques lectivos y 2 recesos de 20 minutos.
   - **Horario B (Especial):** Bloques recortados de 45 minutos para jornadas pedagógicas o actos cívicos.
   - **Horario C (Reducida):** Jornada intensiva continua hasta las 12:30 pm sin descansos largos.
5. Si realiza modificaciones, observará una pastilla ámbar en la esquina superior que indica **Pendiente**.
6. Una vez configurados los 5 días de la semana, presione el botón verde neón **Guardar Horario**.
7. El sistema guardará la configuración en la base de datos no volátil (`TimeSchoolPreferences`), emitirá un mensaje de confirmación flotante (*"Horario semanal guardado exitosamente"*) y generará el registro correspondiente en la bitácora.
8. Los cambios permanecerán intactos incluso si apaga o reinicia el teléfono.

---

### 8.3. MÓDULO 3: PROGRAMACIÓN Y GESTIÓN DE TOQUES DE TIMBRE

En la misma pantalla de Horarios, justo debajo del panel semanal, se encuentra el selector de inspección de días y la lista compacta de toques.

#### Procedimiento para Inspeccionar los Timbres del Día:
1. En la fila horizontal de chips con los días de la semana (`[Lunes A]`, `[Martes B]`, `[Miércoles A]`, etc.), pulse el día que desea revisar.
2. Observe la tarjeta de resumen del día seleccionado:
   - Indica el nombre del día (ejemplo: *Resumen del Lunes*).
   - Muestra el régimen activo (ejemplo: *Horario Ordinario (Tipo A)*).
   - Informa el número total de toques activos y los segundos acumulados de sonido de campana.
3. Debajo aparece la **lista compacta de timbres**, donde cada cuadro ocupa solo 52dp de altura.

#### Procedimiento para Desactivar o Activar un Timbre sin Borrarlo:
1. Localice el toque que desea suspender (ejemplo: el timbre de cambio de clase de las `07:55`).
2. En el extremo derecho de la fila compacta, mueva el interruptor **Switch**.
3. Al desactivarlo, el badge de la hora se tornará gris atenuado y el timbre no se accionará en el ESP32.
4. Para reactivarlo, vuelva a tocar el interruptor.

#### Procedimiento para Registrar un Nuevo Toque de Timbre:
1. En la pantalla de Horarios, presione el botón flotante verde neón **+ Nuevo Toque**.
2. Se desplegará el cuadro de diálogo modal *Configurar Toque de Timbre*.
3. **Hora:** Ingrese la hora en formato militar de 24 horas (`HH:mm`, ejemplo: `10:15`).
4. **Descripción / Evento:** Escriba el motivo del timbre (ejemplo: *Fin de Descanso / Entrada a Aula*).
5. **Día de la Semana:** Seleccione las iniciales del día correspondiente (`L`, `M`, `X`, `J` o `V`).
6. **Duración de activación:** Deslice la barra graduada entre 1 y 15 segundos (el estándar escolar recomendado es de 3 a 5 segundos).
7. Presione **Guardar Toque**. El nuevo timbre se insertará en orden cronológico en la lista.

#### Procedimiento para Editar o Eliminar un Toque:
- **Para editar:** Pulse el icono de lápiz en la fila del timbre, ajuste los datos en el diálogo y guarde los cambios.
- **Para eliminar:** Pulse el icono de papelera en color rojo carmesí. El toque se eliminará de inmediato y se registrará la acción en la bitácora.

---

### 8.4. MÓDULO 4: CALENDARIO ESCOLAR Y ASIGNACIÓN FECHA A FECHA

Este módulo permite proyectar el año lectivo y realizar excepciones día por día (por ejemplo, definir que un viernes específico regirá Horario B por entrega de boletines, o marcar festivos como Sin Clases).

#### Figura 6. Módulo de Calendario Escolar: Asignación Diaria
*(Consulte la imagen `image.png` número 6 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  [📅] Asignación Diaria de Horarios                          │
│  Asigna qué régimen de toques regirá cada fecha del año...   │
│                                                              │
│  Próximas Fechas Registradas (15 días planificados)          │
│                                                              │
│  [ 15 ] Jueves, 15 de octubre                                │
│  [ 10 ] [ Horario Ordinario (Tipo A) ]               [ ✏️ ]  │
│         Jornada escolar ordinaria                            │
│                                                              │
│  [ 16 ] Viernes, 16 de octubre                               │
│  [ 10 ] [ Horario Ordinario (Tipo A) ]               [ ✏️ ]  │
│                                                              │
│  [ 17 ] Sábado, 17 de octubre                                │
│  [ 10 ] [ Sin Clases / Receso ]                      [ ✏️ ]  │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Vista cronológica fecha a fecha con pastillas de color según el tipo de horario asignado y botón de edición rápida.

#### Figura 7. Barra de Navegación Inferior: Acceso al Calendario
*(Consulte la imagen `image.png` número 7 cargada en el proyecto)*  
*Descripción:* Pestaña **Calendario** seleccionada en el menú inferior.

#### Procedimiento para Modificar el Horario de una Fecha Específica:
1. En la barra inferior, pulse el icono **Calendario**.
2. Desplácese por la lista cronológica de los próximos 15 días planificados.
3. Identifique la fecha que desea modificar (ejemplo: *Lunes, 19 de octubre*).
4. Pulse el botón de edición con icono de calendario (`[ ✏️ ]`) en el costado derecho de la tarjeta.
5. En el diálogo emergente:
   - Seleccione mediante el botón radial el nuevo régimen: *Horario Ordinario (Tipo A)*, *Horario Especial (Tipo B)*, *Jornada Reducida (Tipo C)* o *Sin Clases / Feriado*.
   - Ingrese una observación o motivo (ejemplo: *"Viernes de acto cívico y jornada pedagógica"*).
6. Presione **Guardar Asignación**.
7. La tarjeta se actualizará de inmediato con el nuevo color distintivo y quedará guardada de forma permanente.

---

### 8.5. MÓDULO 5: ENLACE IOT BLUETOOTH CLASSIC SPP Y TOQUE MANUAL

Este módulo gestiona la capa de comunicación directa con el hardware microcontrolador ESP32 y permite el accionamiento táctil voluntario de la campana.

#### Figura 8. Módulo IoT Timbre: Enlace Bluetooth y Toque Manual
*(Consulte la imagen `image.png` número 8 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  [ᛒ] Enlace Hardware (ESP32)           [ ● Conectado (SPP) ] │
│  Dispositivo: ESP32_TIMESCHOOL_BELL                          │
│  Dirección MAC: 24:6F:28:B2:7A:1C                            │
│  UUID SPP: 00001101-0000-1000-8000-00805F9B34FB             │
│                                                              │
│      [ Desconectar ]                 [ 🔄 Sincronizar ]      │
├──────────────────────────────────────────────────────────────┤
│  Toque Manual de Campana                      RELÉ: REPOSO   │
│  Duración toque:  [ 2s ]  [ 3s ]  [ 5s ]  [ 8s ]             │
│                                                              │
│                         ┌────────┐                           │
│                         │   🔔   │                           │
│                         │  TOCAR │                           │
│                         └────────┘                           │
│                       Toca o mantén                          │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Panel técnico con datos de conexión MAC y UUID SPP, botones de enlace y el gran botón circular verde para accionar la campana por pulso o sostenido.

#### Procedimiento para Enlazar y Sincronizar el ESP32:
1. En la barra inferior, seleccione la pestaña **IoT Timbre**.
2. En la tarjeta superior *Enlace Hardware (ESP32)*, compruebe el estado del enlace:
   - Si figura *Desconectado*, pulse el botón **Conectar**.
   - El sistema buscará el dispositivo `ESP32_TIMESCHOOL_BELL` (MAC `24:6F:28:B2:7A:1C`) mediante el canal RFCOMM SPP estándar.
   - El indicador pasará a verde con el texto **Conectado (SPP)**.
3. Para volcar todas las tablas horarias a la memoria Flash del microcontrolador, pulse **Sincronizar**.
4. Una barra de progreso transmitirá las cadenas seriales `SYNC_SCHEDULES` y `SYNC_TIME` con la estampa de tiempo del reloj móvil.

#### Procedimiento para Realizar un Toque Manual de Campana:
1. En la tarjeta *Toque Manual de Campana*, elija la duración predeterminada pulsando uno de los selectores rápidos: **2s**, **3s**, **5s** u **8s**.
2. **Modo Toque Rápido:**
   - Toque una sola vez el gran botón circular verde **TOCAR**.
   - El botón comenzará a pulsar visualmente, el indicador superior cambiará a **RELÉ: ACTIVO** y la campana física sonará durante los segundos seleccionados.
3. **Modo Toque Sostenido (Manual Continuo):**
   - Mantenga presionado con el dedo el botón circular **TOCAR**.
   - La campana sonará de forma ininterrumpida mientras mantenga el dedo sobre la pantalla.
   - Al retirar el dedo, el relé se desconectará al instante.
4. Cada accionamiento manual genera automáticamente un registro en la bitácora indicando el nombre del usuario y la duración exacta.

---

### 8.6. MÓDULO 6: ALARMA DE EVACUACIÓN Y PROTOCOLO ANTIDISPAROS

Este submódulo se encuentra en la parte inferior de la pestaña IoT Timbre. Su función exclusiva es emitir la señal sonora reglamentaria de emergencia escolar ante sismos, incendios o amenazas.

#### Figura 9. Módulo de Seguridad Crítica: Alarma de Evacuación
*(Consulte la imagen `image.png` número 9 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  [⚠️] Alarma de Evacuación                                   │
│  Ciclo intermitente: 2s activo / 1s silencio                 │
│                                                              │
│  Seguro Antidisparo Accidental                               │
│  Bloqueado para prevenir toques involuntarios         [ O ]  │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │       📢  ACTIVAR EVACUACIÓN GENERAL (Deshabilitado)   │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Contenedor con borde rojo carmesí `#EF4444`, interruptor de seguridad de dos pasos y botón de mando de emergencia.

#### Figura 10. Barra de Navegación Inferior: Acceso a IoT Timbre
*(Consulte la imagen `image.png` número 10 cargada en el proyecto)*  
*Descripción:* Pestaña **IoT Timbre** activa en la barra de navegación.

#### Procedimiento de Seguridad para Activar la Alarma de Evacuación:
1. En la pestaña **IoT Timbre**, desplácese hasta la tarjeta inferior de borde rojo *Alarma de Evacuación*.
2. Observe que el botón principal *ACTIVAR EVACUACIÓN GENERAL* se encuentra inicialmente atenuado y bloqueado para evitar pulsaciones involuntarias.
3. **Paso 1 (Desbloqueo de Seguridad):** Deslice hacia la derecha el interruptor **Seguro Antidisparo Accidental**. El texto cambiará a *"Desbloqueado: Botón de alarma habilitado"*.
4. **Paso 2 (Disparo de Alarma):** Presione el botón rojo carmesí **ACTIVAR EVACUACIÓN GENERAL**.
5. **Comportamiento del Sistema:**
   - Toda la interfaz comenzará a parpadear en rojo carmesí neón.
   - El microcontrolador ESP32 ejecutará de forma prioritaria el comando `CMD:EVAC_START`.
   - La campana escolar comenzará a sonar en el **ciclo continuo reglamentario: 2 segundos de timbre activo seguidos de 1 segundo de silencio**, repitiéndose indefinidamente.
   - Se creará una alerta crítica prioritaria en la bitácora inmutable.
6. **Para Detener la Alarma:**
   - El botón principal se transformará en un recuadro verde de alta visibilidad con el texto **DETENER ALARMA DE EVACUACIÓN**.
   - Pulse dicho botón. El ESP32 recibirá la orden `CMD:EVAC_STOP`, apagará el relé de inmediato y el sistema retornará al estado de reposo normal.
   - Vuelva a activar el interruptor de seguro antidisparos para dejar protegido el módulo.

---

### 8.7. MÓDULO 7: BITÁCORA INMUTABLE DE AUDITORÍA

La bitácora es el registro legal e inmutable de todas las acciones ejecutadas por el sistema TimeSchool. Cumple con normativas de seguridad que impiden que cualquier operador, incluso administradores, elimine o modifique registros históricos.

#### Figura 11. Módulo de Bitácora Inmutable (Cloud Firestore)
*(Consulte la imagen `image.png` número 11 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  [🛡️] Bitácora Inmutable (Cloud Firestore)                   │
│  Reglas de seguridad activas: Inserción y lectura            │
│  habilitadas; edición y borrado estrictamente prohibidos.    │
├──────────────────────────────────────────────────────────────┤
│  🔍 Buscar en la bitácora (ej. Carlos, Toque, 07:00)...      │
├──────────────────────────────────────────────────────────────┤
│  [ Todos (4) ]  [ Programado (2) ]  [ Toque Manual (1) ] ... │
├──────────────────────────────────────────────────────────────┤
│  Eventos (4)                            Orden inverso        │
│                                                              │
│  [ 🔄 Sincronización IoT ]        2026-10-15 12:55:47  [🔒]  │
│  Sincronizados 135 toques y reloj RTC con ESP32 vía SPP      │
│  👤 Carlos Mendoza (Administrador)             [ Ejecutado ] │
│                                                              │
│  [ 🕒 Programado ]                2026-10-15 07:00:02  [🔒]  │
│  Disparo automático: Timbre 07:00 (Inicio Hora 1) - 4s       │
│  👤 ESP32 Timer Autónomo                       [ Ejecutado ] │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Interfaz de auditoría con buscador en tiempo real, filtros por chips y tarjetas con estampa de tiempo, autor y candado de inmutabilidad.

#### Figura 12. Barra de Navegación Inferior: Acceso a Bitácora
*(Consulte la imagen `image.png` número 12 cargada en el proyecto)*  
*Descripción:* Pestaña **Bitácora** activa en el menú de navegación.

#### Procedimiento para Consultar y Filtrar Registros de Auditoría:
1. En la barra inferior, pulse el icono **Bitácora**.
2. **Para filtrar por categoría de evento:**
   - Pulse sobre los chips superiores:
     - **Todos:** Muestra el flujo completo en orden cronológico inverso.
     - **Programado:** Filtra los toques disparados de forma automática por el reloj del ESP32.
     - **Toque Manual:** Muestra los toques voluntarios accionados por el personal.
     - **Alarma Evacuación:** Muestra únicamente simulacros o emergencias de evacuación.
     - **Sincronización IoT:** Registra los enlaces Bluetooth, transmisiones de tablas o cambios de rutina semanal.
3. **Para buscar por texto específico:**
   - Escriba en la barra buscadora cualquier término clave (por ejemplo: *"Carlos"*, *"07:00"*, *"Sincronización"* o *"Evacuación"*).
   - La lista filtrará dinámicamente las coincidencias en pantalla.
4. Cada tarjeta detalla:
   - Tipo de evento con icono distintivo.
   - Fecha y hora exacta con segundos (`yyyy-MM-dd HH:mm:ss`).
   - Icono de candado (`[🔒]`) que certifica que el registro es inmutable.
   - Descripción detallada de lo sucedido.
   - Nombre completo y rol del usuario responsable (o *ESP32 Timer Autónomo* si fue automático).
   - Pastilla de estado verde **Ejecutado**.

---

### 8.8. MÓDULO 8: CONFIGURACIÓN INSTITUCIONAL Y CONTROL DE PERMISOS

Este módulo administra la identidad formal de la institución educativa en la aplicación y evidencia el modelo de control de acceso por roles según las reglas de seguridad de Firestore.

#### Figura 13. Configuración Institucional: Sesión Activa de Administrador
*(Consulte la imagen `image.png` número 13 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  Sesión Activa                             [ ADMINISTRADOR ] │
│  👤 Carlos Mendoza                                           │
│     admin@timeschool.edu.co                                  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │   🔄 Cambiar a Rol Directivo (Prueba de Permisos)      │  │
│  └────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────┤
│  Configuración Institucional                                 │
│  [🏫] Nombre del Colegio / Institución                       │
│       Institución Educativa Técnico Industrial San José      │
│  [📍] Sede / Campus: Sede Principal - Campus Central         │
│  [📅] Año Lectivo: 2026                                      │
│                                                              │
│  Última modificación: 15/10/2026 12:54                       │
│  Por: Carlos Mendoza (Administrador)                         │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │          💾  Guardar Cambios Institucionales           │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Vista completa para administradores con campos de texto habilitados y botón de guardado institucional activo.

#### Figura 14. Restricción de Permisos Institucionales: Sesión de Directivo
*(Consulte la imagen `image.png` número 14 cargada en el proyecto)*

```
┌──────────────────────────────────────────────────────────────┐
│  Sesión Activa                                 [ DIRECTIVO ] │
│  👤 Marta Rojas                                              │
│     coordinacion@timeschool.edu.co                           │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  🔒 Modo Directivo: Acceso de solo lectura a la        │  │
│  │     configuración institucional según las reglas de    │  │
│  │     Firestore.                                         │  │
│  └────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │              🔄 Cambiar a Rol Administrador            │  │
│  └────────────────────────────────────────────────────────┘  │
│  (Campos de Nombre, Sede y Año deshabilitados)               │
└──────────────────────────────────────────────────────────────┘
```

*Descripción:* Alerta de seguridad y campos bloqueados cuando la sesión activa corresponde a un directivo.

#### Figura 15. Barra de Navegación Inferior: Acceso a Sede
*(Consulte la imagen `image.png` número 15 cargada en el proyecto)*  
*Descripción:* Pestaña **Sede** activa en el menú de navegación.

#### Procedimiento para Modificar los Datos Institucionales (Solo Administrador):
1. En la barra inferior, pulse el icono **Sede**.
2. Verifique que su sesión indique **ADMINISTRADOR** (Carlos Mendoza).
3. Modifique los campos requeridos:
   - **Nombre del Colegio:** Ingrese la razón social de la institución.
   - **Sede / Campus:** Especifique el campus o bloque físico.
   - **Año Lectivo:** Ingrese el año correspondiente (ejemplo: `2026`).
4. Pulse el botón verde neón **Guardar Cambios Institucionales**.
5. El sistema actualizará el nombre en la barra superior en tiempo real, almacenará la configuración en disco permanente y registrará la modificación con estampa de tiempo en la bitácora.

#### Procedimiento para Verificar las Restricciones de Rol Directivo:
1. En la tarjeta *Sesión Activa*, presione el botón **Cambiar a Rol Directivo (Prueba de Permisos)**.
2. La sesión pasará a nombre de **Marta Rojas (Directivo)**.
3. Aparecerá de inmediato el banner amarillo de advertencia: *"Modo Directivo: Acceso de solo lectura..."*.
4. Compruebe que las cajas de texto de Nombre, Sede y Año se bloquean automáticamente y el botón de guardar desaparece, demostrando el cumplimiento estricto de las reglas de seguridad.

---

## 9. CATÁLOGO DE MENSAJES, VALIDACIONES Y SOLUCIÓN DE ERRORES

| Mensaje / Comportamiento en Pantalla | Causa / Significado | Acción Correctiva Recomendada |
|---|---|---|
| *"Horario semanal guardado exitosamente"* | Confirmación tras pulsar **Guardar Horario**. Los 5 días quedaron grabados en disco. | Ninguna. La configuración es permanente y resistente a reinicios. |
| *"Pendiente"* (Pastilla ámbar en Horarios) | Se cambió el tipo de horario de algún día pero aún no se ha presionado el botón de guardar. | Pulse **Guardar Horario** para aplicar los cambios en la base de datos. |
| *"Bluetooth ESP32 desconectado"* | El teléfono perdió el enlace inalámbrico con el módulo de la campana. | Sitúese dentro del rango de alcance (aprox. 15 metros) y pulse **Conectar**. |
| *"Sincronización exitosa: horarios transmitidos al ESP32"* | Las matrices horarias se grabaron en la memoria Flash no volátil del microcontrolador. | La campana funcionará de forma autónoma según lo programado. |
| *"Acceso Denegado: Solo el Administrador puede modificar estos datos"* | Un usuario con rol Directivo intentó editar el nombre del colegio o la sede. | Conmute al rol Administrador en la pestaña Sede para editar datos institucionales. |
| *"Botón ACTIVAR EVACUACIÓN bloqueado"* | El seguro antidisparos está activado para proteger contra toques accidentales. | Deslice primero el switch **Seguro Antidisparo Accidental** antes de pulsar el botón. |
| *"Relé: Reposo"* | La campana física no está recibiendo corriente (circuito abierto). | Estado normal de reposo entre clases. |
| *"Relé: Activo"* | La campana física está sonando (circuito cerrado). | Finalizará automáticamente al cumplirse la duración configurada. |

---

## 10. RECOMENDACIONES DE SEGURIDAD Y BUENAS PRÁCTICAS

1. **Sincronización Periódica del Reloj RTC:** Aunque el ESP32 cuenta con oscilador de alta precisión, se aconseja conectar la aplicación una vez por semana para contrastar la hora con la hora de red del teléfono.
2. **Cuidado con el Seguro de Evacuación:** Mantenga siempre apagado el switch de seguro antidisparos para evitar que una pulsación inadvertida en el bolsillo genere una falsa alarma de desalojo en el plantel.
3. **Respaldo de Energía para la Campana:** Conecte el microcontrolador ESP32 y el circuito del relé a un sistema ininterrumpido de potencia (UPS) para que la campana continúe sonando puntualmente incluso durante apagones.
4. **Verificación de Duración de Toques:** No configure toques manuales o programados superiores a 8 segundos para timbres estándar, a fin de evitar el sobrecalentamiento de la bobina electromagnética de la campana.

---

## 11. PREGUNTAS FRECUENTES (FAQ)

### ¿Qué ocurre con la campana si el teléfono móvil se apaga o se queda sin batería?
**Respuesta:** Nada negativo. El ESP32 es 100% autónomo. Una vez que se pulsa *Sincronizar*, los horarios se graban en la memoria Flash del circuito, por lo que el colegio seguirá sonando en sus horas exactas aunque el teléfono no esté presente.

### ¿Por qué al cambiar de día en la pestaña Horarios cambian los timbres mostrados abajo?
**Respuesta:** Porque la aplicación vincula cada día con su régimen respectivo. Si el Lunes tiene asignado Horario A, verá los toques del Horario A; si el Martes tiene Horario B, la lista compacta mostrará automáticamente la secuencia recortada del Horario B.

### ¿Puedo borrar un registro de la bitácora si me equivoqué al tocar la campana?
**Respuesta:** No. Las reglas de seguridad de TimeSchool garantizan la inmutabilidad de la bitácora (`allow update, delete: if false`). Todo evento queda registrado para transparencia y auditoría del colegio.

### ¿Se requiere conexión a Internet para que suene la campana?
**Respuesta:** No. La comunicación entre el teléfono y la campana utiliza Bluetooth Classic local (SPP). No se necesita saldo, datos móviles ni conexión Wi-Fi para operar el sistema.

---

## 12. GLOSARIO DE TÉRMINOS TÉCNICOS

- **ESP32:** Microcontrolador de bajo costo y alto rendimiento equipado con procesador de doble núcleo, memoria Flash y radio Bluetooth/Wi-Fi integrado.
- **Relé (Relay):** Interruptor electromecánico accionado por una señal digital del microcontrolador que abre o cierra el paso de corriente de 110V/220V hacia la campana.
- **RTC (Real-Time Clock):** Reloj de tiempo real que mantiene la cuenta exacta de horas, minutos y segundos en el hardware.
- **Bluetooth SPP (Serial Port Profile):** Protocolo de comunicación que emula un puerto serie inalámbrico para transferir comandos y datos entre Android y el microcontrolador.
- **Memoria Flash No Volátil:** Memoria de almacenamiento permanente en el ESP32 que conserva los horarios grabados incluso cuando se desconecta de la corriente eléctrica.
- **Cloud Firestore:** Base de datos en la nube de Google Firebase utilizada para respaldar los usuarios, calendarios y la bitácora inmutable.
- **SharedPreferences:** Sistema de almacenamiento local y permanente de Android que retiene las configuraciones en el dispositivo del usuario.

---

## 13. CONCLUSIONES DEL SISTEMA

El sistema **TimeSchool** representa un salto cualitativo fundamental en la gestión de tiempos y seguridad de la **Institución Educativa Técnico Industrial San José**. Al unificar una aplicación móvil moderna, intuitiva y atractiva con la solidez de un hardware autónomo de bajo costo, se garantiza:
1. **Eliminación total del error humano** en el cumplimiento de la jornada académica.
2. **Capacidad de respuesta inmediata y protegida** ante contingencias y evacuaciones de emergencia.
3. **Flexibilidad pedagógica** para alternar jornadas ordinarias, especiales o continuas en segundos.
4. **Tranquilidad institucional** respaldada por un sistema de auditoría inmutable y persistencia garantizada.
