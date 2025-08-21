# SISTEMA DE GESTIÓN DE RESTAURANTE - FUNCIONALIDAD POR ROLES DE USUARIO

## RESUMEN EJECUTIVO

El sistema de gestión del restaurante maneja tres tipos principales de usuarios con diferentes niveles de acceso y responsabilidades, siguiendo una jerarquía organizacional clara:

```
ADMIN (Administrador)
    ↓
LIDER DE MESERO (Jefe de Meseros)
    ↓
MESERO (Mesero)
```

---

## 1. ADMIN (ADMINISTRADOR) - MÁXIMO NIVEL DE ACCESO

### **Funcionalidades Principales:**

#### **1.1 Gestión de Mesas**
- **Crear nuevas mesas**: Definir número, capacidad, área/ubicación
- **Editar mesas existentes**: Modificar propiedades de las mesas
- **Eliminar mesas**: Remover mesas del sistema
- **Visualizar estado**: Ver todas las mesas y su estado (DISPONIBLE, OCUPADA, LIBRE)

#### **1.2 Gestión de Usuarios**
- **Crear usuarios**: Registrar nuevos empleados (ADMIN, LIDER DE MESERO, MESERO)
- **Editar usuarios**: Modificar información personal, roles, estado, sueldo
- **Eliminar usuarios**: Dar de baja empleados
- **Gestión de roles**: Asignar roles específicos a cada usuario
- **Control de estados**: Activar/desactivar usuarios

#### **1.3 Gestión de Categorías de Platillos**
- **Crear categorías**: Definir nuevas categorías de comida
- **Editar categorías**: Modificar nombre y descripción
- **Eliminar categorías**: Remover categorías del sistema

#### **1.4 Gestión de Platillos**
- **Agregar platillos**: Crear nuevos elementos del menú
- **Editar platillos**: Modificar precio, descripción, categoría
- **Eliminar platillos**: Remover elementos del menú
- **Asignación de categorías**: Vincular platillos con categorías

#### **1.5 Estadísticas y Reportes**
- **Ingresos totales**: Visualizar dinero generado por período
- **Cuentas cerradas/abiertas**: Estadísticas de transacciones
- **Solicitudes de modificación**: Monitoreo de cambios solicitados
- **Filtros por fecha**: Análisis temporal de rendimiento
- **Detalles por mesa y mesero**: Análisis granular de operaciones

### **Interfaz de Menú del Admin:**
```
┌─────────────────────────────────────┐
│  MENÚ PRINCIPAL ADMINISTRADOR       │
├─────────────────────────────────────┤
│  🏠 Mesas                          │
│  📊 Estadísticas                   │
│  📂 Categorías                     │
│  🍽️ Platillos                      │
│  👥 Usuarios                       │
│  🚪 Salir                          │
└─────────────────────────────────────┘
```

---

## 2. LIDER DE MESERO (JEFE DE MESEROS) - NIVEL SUPERVISORIO

### **Funcionalidades Principales:**

#### **2.1 Gestión de Solicitudes de Modificación**
- **Revisar solicitudes**: Ver peticiones de cambios en cuentas
- **Aprobar solicitudes**: Autorizar modificaciones solicitadas por meseros
- **Denegar solicitudes**: Rechazar peticiones con justificación
- **Ver detalles**: Visualizar información completa de cuentas y platillos

#### **2.2 Planificación Diaria (Plan del Día)**
- **Asignar mesas a meseros**: Distribuir responsabilidades diarias
- **Gestionar asignaciones**: Crear, editar, eliminar asignaciones
- **Verificar duplicados**: Evitar conflictos en asignaciones
- **Historial de asignaciones**: Ver registro completo de asignaciones

#### **2.3 Supervisión de Personal**
- **Monitorear meseros**: Ver estado de todos los meseros
- **Gestionar disponibilidad**: Controlar meseros activos/inactivos
- **Supervisar mesas**: Ver estado general de todas las mesas

#### **2.4 Revisión de Operaciones**
- **Estado de mesas**: Monitorear ocupación y disponibilidad
- **Control de cuentas**: Supervisar flujo de cuentas activas
- **Gestión de solicitudes**: Autorizar cambios en el servicio

### **Interfaz de Menú del Líder de Meseros:**
```
┌─────────────────────────────────────┐
│  MENÚ LÍDER DE MESEROS              │
├─────────────────────────────────────┤
│  🏠 Inicio                         │
│  📅 Plan del Día                   │
│  🚪 Salir                          │
├─────────────────────────────────────┤
│  FUNCIONES PRINCIPALES:             │
│  • Aprobar/Denegar Solicitudes     │
│  • Asignar Mesas a Meseros         │
│  • Supervisar Personal             │
│  • Gestionar Horarios              │
└─────────────────────────────────────┘
```

---

## 3. MESERO (MESERO) - NIVEL OPERATIVO

### **Funcionalidades Principales:**

#### **3.1 Gestión de Horarios y Asignaciones**
- **Ver mesas asignadas**: Consultar mesas bajo su responsabilidad
- **Ver turnos asignados**: Revisar horarios de trabajo
- **Estado de mesas**: Monitorear mesas DISPONIBLE/OCUPADA

#### **3.2 Toma de Órdenes (Nueva Orden)**
- **Crear cuentas nuevas**: Iniciar nueva cuenta para mesa asignada
- **Agregar platillos**: Seleccionar elementos del menú
- **Eliminar platillos**: Remover elementos de la cuenta
- **Enviar a cocina**: Confirmar orden y cambiar estado de mesa a OCUPADA
- **Gestión de detalles**: Ver y modificar detalles de la cuenta

#### **3.3 Gestión de Cuentas**
- **Ver cuentas activas**: Consultar cuentas abiertas por mesa
- **Calcular totales**: Determinar monto total de la cuenta
- **Solicitar modificaciones**: Pedir autorización para cambios
- **Procesar pagos**: Cerrar cuentas y liberar mesas
- **Calificación de servicio**: Gestionar evaluación del mesero

#### **3.4 Solicitudes de Modificación**
- **Crear solicitudes**: Pedir cambios en cuentas al líder
- **Ver estado**: Monitorear PENDIENTE/ACEPTADA/DENEGADA
- **Editar cuentas autorizadas**: Modificar solo si está ACEPTADA

### **Interfaz de Menú del Mesero:**
```
┌─────────────────────────────────────┐
│  MENÚ MESERO                        │
├─────────────────────────────────────┤
│  ⏰ Horario Laboral                │
│  🍽️ Mesas Asignadas                │
│  📝 Tomar Orden                    │
│  🚪 Salir                          │
├─────────────────────────────────────┤
│  FUNCIONES PRINCIPALES:             │
│  • Tomar Órdenes                   │
│  • Gestionar Cuentas               │
│  • Solicitar Modificaciones        │
│  • Procesar Pagos                  │
└─────────────────────────────────────┘
```

---

## JERARQUÍA Y DEPENDENCIAS ENTRE ROLES

### **Flujo de Autoridad:**

1. **ADMIN → LIDER DE MESERO → MESERO**
   - El Admin crea y gestiona todos los usuarios
   - El Líder supervisa y autoriza acciones de los meseros
   - Los meseros ejecutan operaciones autorizadas

### **Dependencias Funcionales:**

#### **Mesero depende de:**
- **Líder de Mesero**: Para aprobación de modificaciones
- **Líder de Mesero**: Para asignación de mesas y horarios
- **Admin**: Para configuración de platillos y categorías

#### **Líder de Mesero depende de:**
- **Admin**: Para gestión de usuarios y configuración del sistema
- **Admin**: Para acceso a estadísticas y reportes globales

#### **Admin tiene:**
- **Control total**: Sobre todos los aspectos del sistema
- **Independencia**: No requiere autorización de otros roles

---

## FLUJO DE TRABAJO TÍPICO

### **1. Configuración Inicial (Admin):**
```
Admin → Crear Categorías → Crear Platillos → Crear Usuarios → Configurar Mesas
```

### **2. Planificación Diaria (Líder de Mesero):**
```
Líder → Revisar Meseros Disponibles → Asignar Mesas → Aprobar Solicitudes Pendientes
```

### **3. Operación Diaria (Mesero):**
```
Mesero → Ver Asignaciones → Crear Cuenta → Tomar Orden → Enviar a Cocina → Gestionar Pago
```

### **4. Gestión de Modificaciones:**
```
Mesero → Solicita Modificación → Líder → Aprueba/Deniega → Mesero → Ejecuta si Aprobada
```

---

## CARACTERÍSTICAS TÉCNICAS DEL SISTEMA

### **Base de Datos:**
- **Oracle Database**: Sistema de gestión principal
- **Tablas principales**: USUARIOS, MESAS, CUENTAS, PLATILLOS, CATEGORIAS, ASIGNACIONES_MESAS, SOLICITUDES_MODIFICACION

### **Tecnologías:**
- **JavaFX**: Interfaz de usuario
- **JDBC**: Conectividad con base de datos
- **Maven**: Gestión de dependencias
- **FXML**: Diseño de interfaces

### **Seguridad:**
- **Autenticación por roles**: Login específico por tipo de usuario
- **Control de acceso**: Funcionalidades restringidas por rol
- **Sesiones de usuario**: Manejo de estado por usuario logueado

---

## BENEFICIOS DEL SISTEMA

### **Para el Restaurante:**
- **Control total** de operaciones
- **Trazabilidad** de todas las transacciones
- **Gestión eficiente** de personal
- **Estadísticas** para toma de decisiones

### **Para los Empleados:**
- **Claridad** en roles y responsabilidades
- **Facilidad** de uso en interfaces específicas
- **Control** de solicitudes y autorizaciones
- **Eficiencia** en procesos operativos

### **Para los Clientes:**
- **Servicio organizado** y eficiente
- **Precisión** en órdenes
- **Rapidez** en procesamiento
- **Calidad** en atención