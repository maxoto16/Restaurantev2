# DOCUMENTACIÓN TÉCNICA - CONTROLADORES DEL SISTEMA

## DESCRIPCIÓN GENERAL

Este documento detalla la implementación técnica de cada controlador en el sistema de gestión del restaurante, incluyendo métodos específicos, funcionalidades y relaciones entre componentes.

---

## 1. CONTROLADORES DEL ADMINISTRADOR

### **1.1 AdminController.java**

#### **Responsabilidades:**
- Gestión CRUD de mesas del restaurante
- Navegación entre módulos administrativos
- Interfaz principal del administrador

#### **Métodos Principales:**
```java
// Gestión de Mesas
public void cargarMesas()           // Carga todas las mesas desde BD
public void agregarMesa()           // Inserta nueva mesa
public void editarMesa()            // Actualiza mesa existente
public void eliminarMesa()          // Elimina mesa con confirmación

// Navegación
public void initialize()            // Configuración inicial y eventos
private void animateIconOnHover()   // Animaciones de UI

// Utilidades
public void limpiarCampos()         // Limpia formularios
public void mostrarAlerta()         // Muestra mensajes al usuario
```

#### **Entidades Manejadas:**
- **Mesa**: `id, numeroMesa, capacidad, area, estado`

#### **Flujo de Trabajo:**
1. Cargar mesas existentes → 2. Permitir CRUD → 3. Validar datos → 4. Actualizar BD → 5. Refrescar vista

### **1.2 UsuariosController.java**

#### **Responsabilidades:**
- Gestión completa de usuarios del sistema
- Asignación de roles y permisos
- Control de estados de empleados

#### **Métodos Principales:**
```java
// Gestión de Usuarios
public void cargarUsuarios()        // Carga todos los usuarios
public void agregarUsuario()        // Crea nuevo empleado
public void editarUsuario()         // Modifica datos de empleado
public void eliminarUsuario()       // Elimina empleado con confirmación

// Validaciones
private void validarDatos()         // Valida campos obligatorios
private void validarSueldo()        // Valida formato numérico
```

#### **Entidades Manejadas:**
- **Usuario**: `idUsuario, nombre, correo, contrasena, rol, estado, sueldo, fechaRegistro`

#### **Roles Disponibles:**
- ADMIN, LIDER DE MESERO, MESERO

### **1.3 CategoriasController.java**

#### **Responsabilidades:**
- Gestión de categorías de platillos
- Organización del menú por tipos

#### **Métodos Principales:**
```java
public void cargarCategorias()      // Carga categorías desde BD
public void agregarCategoria()      // Crea nueva categoría
public void editarCategoria()       // Modifica categoría existente
public void eliminarCategoria()     // Elimina con confirmación
```

### **1.4 PlatillosController.java**

#### **Responsabilidades:**
- Gestión del menú del restaurante
- Vinculación de platillos con categorías
- Control de precios

#### **Funcionalidades:**
- CRUD completo de platillos
- Selección de categorías via ComboBox
- Autocompletado de formularios al seleccionar

### **1.5 EstadisticasController.java**

#### **Responsabilidades:**
- Generación de reportes y estadísticas
- Análisis de rendimiento por períodos
- Métricas operativas

#### **Métodos Principales:**
```java
private void filtrarEstadisticas()  // Filtra por rango de fechas
private void calcularTotales()      // Suma ingresos y transacciones
private void cargarDetalles()       // Carga detalles de cuentas
```

#### **Métricas Calculadas:**
- Total de dinero generado
- Cuentas cerradas vs abiertas
- Solicitudes de modificación
- Detalles por mesa y mesero

---

## 2. CONTROLADORES DEL LÍDER DE MESEROS

### **2.1 LiderMeserosController.java**

#### **Responsabilidades:**
- Gestión de solicitudes de modificación
- Supervisión de meseros y mesas
- Autorización de cambios en cuentas

#### **Métodos Principales:**
```java
// Gestión de Solicitudes
public void cargarSolicitudes()         // Carga solicitudes PENDIENTES
public void aceptarSolicitud()          // Aprueba modificación
public void denegarSolicitud()          // Rechaza modificación
public void mostrarDetallesSolicitud()  // Muestra info completa

// Supervisión
public void cargarMesas()               // Estado de todas las mesas
public void cargarMeseros()             // Estado de meseros
```

#### **Entidades Manejadas:**
- **Solicitud**: `idCuenta, idMesero, motivo, estado`
- **Mesa**: `numeroMesa, capacidad, ubicacion, estado`
- **Mesero**: `nombre, estado`
- **PlatilloDetalle**: Para mostrar detalles de cuentas

#### **Estados de Solicitudes:**
- PENDIENTE → ACEPTADA/DENEGADA

### **2.2 LiderdeMeseros_PlandediaController.java**

#### **Responsabilidades:**
- Planificación diaria de asignaciones
- Asignación de mesas a meseros
- Gestión de horarios y turnos

#### **Métodos Principales:**
```java
// Carga de Datos
private void cargarMesasDisponibles()    // Mesas disponibles para asignar
private void cargarMeserosDisponibles()  // Meseros activos
private void cargarAsignacionesDeHoy()   // Asignaciones del día actual
private void cargarHistorialAsignaciones() // Historial completo

// Gestión de Asignaciones
private void agregarAsignacionMesa()     // Nueva asignación mesa-mesero
private void eliminarAsignacionMesa()    // Remover asignación
private void editarAsignacionMesa()      // Modificar asignación existente

// Validaciones
private void verificarDuplicados()      // Evita asignaciones duplicadas
private int obtenerIdMesaPorNumero()     // Conversión número → ID
private int obtenerIdMeseroPorNombre()   // Conversión nombre → ID
```

#### **Entidades Manejadas:**
- **AsignacionMesa**: `mesa, mesero` (para hoy)
- **AsignacionMesaHistorial**: `mesa, mesero, fecha` (historial)

#### **Flujo de Asignación:**
1. Seleccionar mesa disponible → 2. Seleccionar mesero activo → 3. Verificar no duplicado → 4. Crear asignación → 5. Actualizar vistas

---

## 3. CONTROLADORES DEL MESERO

### **3.1 MeseroController.java**

#### **Responsabilidades:**
- Vista principal del mesero
- Consulta de asignaciones y turnos
- Gestión de solicitudes de modificación

#### **Métodos Principales:**
```java
// Carga de Información
private void cargarMesasAsignadas()           // Mesas bajo responsabilidad
private void cargarTurnosAsignados()          // Horarios de trabajo
private void cargarSolicitudesModificacion() // Estado de solicitudes

// Navegación
private void reloadScreen()                   // Cambio entre pantallas
private void editarCuenta()                   // Acceso a modificación autorizada
```

#### **Entidades Manejadas:**
- **MesaAsignada**: `mesero, numeroMesa, estadoMesa`
- **TurnoAsignado**: `turno, horaInicio, horaFin`
- **SolicitudModificacion**: `numeroMesa, numeroCuenta, areaMesa, estadoSolicitud, idCuenta, idSolicitud`

### **3.2 MeseroNuevaOrdenController.java**

#### **Responsabilidades:**
- Creación de nuevas cuentas
- Toma de órdenes
- Gestión de platillos en cuenta

#### **Métodos Principales:**
```java
// Gestión de Cuentas
private void crearCuentaNueva()          // Inicia nueva cuenta para mesa
private void cargarDetallesCuenta()      // Carga platillos de cuenta activa
private boolean existeCuentaAbierta()    // Verifica cuenta existente

// Gestión de Platillos
private void agregarPlatilloACuenta()    // Añade platillo seleccionado
private void eliminarPlatilloDetalleCuenta() // Remueve platillo
private void enviarACocina()             // Confirma orden y cambia estado mesa

// Datos de Soporte
private void cargarMesasAsignadas()      // Mesas disponibles para el mesero
private void cargarPlatillos()           // Menú completo disponible
private int obtenerIdPlatilloPorNombre() // Conversión nombre → ID
```

#### **Entidades Manejadas:**
- **Platillo**: `nombre, precio, categoria`
- **DetalleCuenta**: `platillo, precio, mesaCuenta, idDetalleCuenta`

#### **Flujo de Orden:**
1. Seleccionar mesa asignada → 2. Crear cuenta nueva → 3. Agregar platillos → 4. Enviar a cocina → 5. Mesa cambia a OCUPADA

### **3.3 MeseroCuentasController.java**

#### **Responsabilidades:**
- Gestión de cuentas activas
- Procesamiento de pagos
- Solicitudes de modificación

#### **Métodos Principales:**
```java
// Gestión de Cuentas
private void cargarMesasConCuentaActiva()    // Mesas con cuentas abiertas
private void cargarCuentasActivasPorMesa()   // Cuentas específicas por mesa
private void actualizarDetallesCuentaYTotal() // Detalles y total de cuenta
private void calcularTotalCuenta()           // Suma total de platillos

// Solicitudes
private void enviarSolicitudModificacion()   // Crea solicitud para líder
private void pagarCuentaYLiberarMesaYCalificar() // Cierra cuenta y libera mesa

// Calificación
private void abrirVentanaCalificacionMesero() // Sistema de evaluación
private int obtenerIdMeseroPorCuenta()       // Identifica mesero responsable
```

#### **Entidades Manejadas:**
- **DetalleCuenta**: `mesa, platillo, precio`

#### **Flujo de Pago:**
1. Seleccionar mesa con cuenta → 2. Seleccionar cuenta específica → 3. Calcular total → 4. Procesar pago → 5. Cerrar cuenta → 6. Liberar mesa → 7. Calificar servicio

---

## 4. ARQUITECTURA Y PATRONES

### **4.1 Patrón MVC (Model-View-Controller)**
- **Model**: Clases internas estáticas para entidades de datos
- **View**: Archivos FXML para interfaces de usuario
- **Controller**: Controladores Java con lógica de negocio

### **4.2 Patrón DAO (Data Access Object)**
- **ConexionBaseDatos**: Clase centralizada para acceso a BD
- **PreparedStatement**: Prevención de inyección SQL
- **Try-with-resources**: Gestión automática de recursos

### **4.3 Gestión de Navegación**
- **PantallaController**: Clase estática para cambio de pantallas
- **Stage management**: Control de ventanas JavaFX
- **Scene switching**: Cambio dinámico de interfaces

### **4.4 Manejo de Errores**
- **Try-catch**: Captura de excepciones SQL y JavaFX
- **mostrarAlerta()**: Método estandarizado para mensajes
- **Validaciones**: Verificación de datos antes de operaciones

---

## 5. BASE DE DATOS

### **5.1 Tablas Principales:**
```sql
USUARIOS (ID_USUARIO, NOMBRE, CORREO, CONTRASENA, ROL, ESTADO, SUELDO, FECHA_REGISTRO)
MESAS (ID_MESA, NUMERO_MESA, CAPACIDAD, UBICACION, ESTADO)
CUENTAS (ID_CUENTA, ID_MESA, ID_MESERO, ESTADO, FECHA_APERTURA)
PLATILLOS (ID_PLATILLO, NOMBRE, PRECIO, ID_CATEGORIA, DESCRIPCION)
CATEGORIAS (ID_CATEGORIA, NOMBRE, DESCRIPCION)
DETALLES_CUENTA (ID_DETALLE, ID_CUENTA, ID_PLATILLO, CANTIDAD, NOTAS)
ASIGNACIONES_MESAS (ID_MESA, ID_USUARIO, ID_TURNO, FECHA)
SOLICITUDES_MODIFICACION (ID_SOLICITUD, ID_CUENTA, ID_MESERO, MOTIVO, ESTADO, FECHA_SOLICITUD)
```

### **5.2 Relaciones:**
- USUARIOS 1:N ASIGNACIONES_MESAS
- MESAS 1:N ASIGNACIONES_MESAS  
- MESAS 1:N CUENTAS
- USUARIOS 1:N CUENTAS
- CUENTAS 1:N DETALLES_CUENTA
- PLATILLOS 1:N DETALLES_CUENTA
- CATEGORIAS 1:N PLATILLOS
- CUENTAS 1:N SOLICITUDES_MODIFICACION

---

## 6. CONSIDERACIONES DE SEGURIDAD

### **6.1 Autenticación:**
- Login por rol específico
- Sesión de usuario mantenida en `LoginController.currentUserId`
- Verificación de permisos por pantalla

### **6.2 Autorización:**
- Funcionalidades restringidas por rol
- Validación de operaciones permitidas
- Control de acceso a datos sensibles

### **6.3 Integridad de Datos:**
- Validaciones en frontend y backend
- Transacciones para operaciones críticas
- Prevención de duplicados y inconsistencias

---

## 7. TECNOLOGÍAS Y DEPENDENCIAS

### **7.1 JavaFX:**
- **Versión**: 24.0.2
- **Componentes**: Controls, FXML
- **Características**: TableView, ComboBox, DatePicker, TextArea

### **7.2 Oracle Database:**
- **Driver**: ojdbc11 versión 23.3.0.23.09
- **Características**: Prepared Statements, Connection Pooling

### **7.3 Maven:**
- **Gestión de dependencias**
- **Compilación**: Java 16
- **Plugin JavaFX**: Para ejecución de aplicación

Este sistema proporciona una solución completa y robusta para la gestión operativa de un restaurante, con roles claramente definidos y funcionalidades específicas para cada tipo de usuario.