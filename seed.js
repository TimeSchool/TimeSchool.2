/**
 * TimeSchool - Script de Inicialización de Datos Base (Cloud Firestore)
 * Etapa 1: Backend, Modelo NoSQL y Carga Inicial
 * 
 * Requisitos:
 * npm install firebase-admin
 * export GOOGLE_APPLICATION_CREDENTIALS="/ruta/a/serviceAccountKey.json"
 * node seed.js
 */

const admin = require('firebase-admin');

if (!admin.apps.length) {
  admin.initializeApp();
}

const db = admin.firestore();

async function seedDatabase() {
  console.log('🚀 Iniciando semillero de datos base para TimeSchool...');

  try {
    // 1. Configuración Institucional
    console.log('🏫 Configurando institución inicial...');
    await db.doc('configuracion/institucion').set({
      nombre_colegio: 'Institución Educativa Técnico Industrial San José',
      sede: 'Sede Principal - Campus Central',
      ano_lectivo: 2026,
      actualizado_por: 'admin_master',
      fecha_actualizacion: admin.firestore.FieldValue.serverTimestamp()
    });

    // 2. Usuarios Base (Administrador y Directivo de Prueba)
    console.log('👤 Registrando usuarios base...');
    const adminUid = 'admin_uid_timeschool_01';
    await db.doc(`usuarios/${adminUid}`).set({
      nombre: 'Carlos',
      apellido: 'Mendoza',
      correo: 'admin@timeschool.edu.co',
      rol: 'administrador',
      estado: true,
      fecha_creacion: admin.firestore.FieldValue.serverTimestamp()
    });

    const directivoUid = 'directivo_uid_timeschool_02';
    await db.doc(`usuarios/${directivoUid}`).set({
      nombre: 'Marta',
      apellido: 'Rojas',
      correo: 'coordinacion@timeschool.edu.co',
      rol: 'directivo',
      estado: true,
      fecha_creacion: admin.firestore.FieldValue.serverTimestamp()
    });

    // 3. Tipos de Horario (A, B, C)
    console.log('⏰ Creando tipos de horario (A, B, C)...');
    const tipos = [
      {
        id: 'TIPO_A',
        nombre: 'Horario Ordinario (Tipo A)',
        descripcion: 'Jornada escolar estándar completa con 2 descansos de 20 min.'
      },
      {
        id: 'TIPO_B',
        nombre: 'Horario Especial / Actos (Tipo B)',
        descripcion: 'Periodos de clase recortados de 45 min para jornadas pedagógicas o actos cívicos.'
      },
      {
        id: 'TIPO_C',
        nombre: 'Jornada Continua / Reducida (Tipo C)',
        descripcion: 'Jornada intensiva hasta las 12:30 pm sin recreos intermedios largos.'
      }
    ];

    for (const tipo of tipos) {
      await db.doc(`tipos_horario/${tipo.id}`).set(tipo);
    }

    // 4. Programaciones de Timbre para Horario Tipo A (Lunes a Viernes)
    console.log('🔔 Sembrando programaciones de timbre para Horario Tipo A...');
    const programacionesTipoA = [
      { hora: '06:45', duracion_segundos: 5, obs: 'Entrada y formación inicial' },
      { hora: '07:00', duracion_segundos: 4, obs: 'Inicio Hora 1' },
      { hora: '07:55', duracion_segundos: 3, obs: 'Cambio de clase - Hora 2' },
      { hora: '08:50', duracion_segundos: 3, obs: 'Cambio de clase - Hora 3' },
      { hora: '09:45', duracion_segundos: 5, obs: 'Inicio Primer Descanso' },
      { hora: '10:15', duracion_segundos: 4, obs: 'Fin de descanso / Inicio Hora 4' },
      { hora: '11:10', duracion_segundos: 3, obs: 'Cambio de clase - Hora 5' },
      { hora: '12:05', duracion_segundos: 5, obs: 'Inicio Segundo Descanso / Almuerzo' },
      { hora: '12:45', duracion_segundos: 4, obs: 'Fin descanso / Inicio Hora 6' },
      { hora: '13:40', duracion_segundos: 3, obs: 'Cambio de clase - Hora 7' },
      { hora: '14:30', duracion_segundos: 6, obs: 'Salida General de Estudiantes' }
    ];

    let progCount = 1;
    // Lunes (1) a Viernes (5)
    for (let dia = 1; dia <= 5; dia++) {
      for (const prog of programacionesTipoA) {
        const progId = `prog_a_d${dia}_${prog.hora.replace(':', '')}`;
        await db.doc(`programaciones/${progId}`).set({
          id_tipo_horario: 'TIPO_A',
          dia_semana: dia,
          hora: prog.hora,
          duracion_segundos: prog.duracion_segundos,
          id_timbre: 'CAMPANA_CENTRAL',
          activo: true
        });
        progCount++;
      }
    }

    // 5. Calendario Inicial (Asignaciones para la semana actual)
    console.log('📅 Asignando calendario base de la semana...');
    const hoy = new Date();
    for (let i = 0; i < 7; i++) {
      const d = new Date(hoy);
      d.setDate(hoy.getDate() + i);
      const isoDate = d.toISOString().split('T')[0];
      const diaSem = d.getDay(); // 0 domingo, 6 sabado

      let tipoAsignado = 'TIPO_A';
      let observacion = 'Jornada escolar estándar';
      if (diaSem === 0 || diaSem === 6) {
        tipoAsignado = 'SIN_CLASES';
        observacion = 'Fin de semana institucional';
      }

      await db.doc(`calendario_dias/${isoDate}`).set({
        fecha: isoDate,
        id_tipo_horario: tipoAsignado,
        observacion: observacion
      });
    }

    // 6. Registro Inicial de Bitácora
    console.log('📜 Generando registro inmutable inicial en bitácora...');
    await db.collection('registros').add({
      fecha: new Date().toISOString().split('T')[0],
      hora: new Date().toTimeString().split(' ')[0],
      tipo_evento: 'SINCRONIZACION',
      usuario_uid: adminUid,
      nombre_usuario: 'Carlos Mendoza (Administrador)',
      observacion: 'Inicialización de base de datos y configuración del sistema TimeSchool',
      ejecutado: true
    });

    console.log('✅ Semillero completado con éxito.');
  } catch (error) {
    console.error('❌ Error al ejecutar el semillero:', error);
  }
}

seedDatabase();
