# Restaurant Management System (Restaurantev2)

Restaurant Management System is a JavaFX desktop application for managing restaurant operations including order taking, table assignments, user roles (Admin, Waiter Leader, Waiter), and payment processing. The system connects to Oracle Autonomous Database for data persistence.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

- Bootstrap, build, and test the repository:
  - Ensure Java 17 is available: `java -version` should show version 17.x.x
  - Navigate to project directory: `cd Restaurantev2/`
  - Make Maven wrapper executable: `chmod +x mvnw`
  - Clean and compile: `./mvnw clean compile` -- takes ~4-5 seconds. NEVER CANCEL.
  - Run tests: `./mvnw test` -- takes ~8 seconds. NEVER CANCEL. Note: No tests exist currently.
- Run the application:
  - ALWAYS run the bootstrapping steps first.
  - `./mvnw javafx:run` -- Requires GUI environment (X11 display). Will fail in headless environments with "Unable to open DISPLAY" error.
  - Database connection: Requires Oracle wallet configuration (see Database section below).

## Database Requirements

- **Database Type**: Oracle Autonomous Database
- **Connection Method**: Wallet-based authentication with SSL
- **Wallet Location**: `src/wallet/` directory contains all required wallet files
- **Connection Issues**: 
  - Hardcoded Windows path in `MainApp.java` line 27: `C:\\ProyectoIntegradorUtezHugo\\Restaurantev2\\src\\wallet`
  - To fix for current environment, change line 27 to: `private static final String UBICACION_WALLET = "src/wallet";`
  - Database credentials are hardcoded in source code (lines 28-30 in MainApp.java)
  - Network connectivity to Oracle Cloud required
- **Expected Behavior**: Application attempts database connection on startup. Connection failure prevents application from starting.
- **Wallet Status**: Wallet expires on 2030-08-16. Valid until then.

## Validation

- **Build Validation**: Always run `./mvnw clean compile` after making changes. Build should complete in 2-5 seconds.
- **Compilation Warnings**: Module name warning is expected: "module name component restaurantev2 should avoid terminal digits"
- **GUI Testing**: Cannot test actual GUI functionality in headless environments. Application will fail with display errors.
- **Manual Testing Scenarios** (when GUI is available):
  1. **Database Connection Test**: Application startup will show "Conexión a la base de datos configurada correctamente." in console if successful
  2. **Login Flow**: Start application → Login screen appears → Test with valid database credentials
  3. **Admin Role Validation**: 
     - Login as Admin → Access Usuarios (user management)
     - Create/edit categories → Access Categorías  
     - Manage dishes → Access Platillos
     - View reports → Access Estadísticas
  4. **Waiter Leader Role Validation**:
     - Login as Líder → Access Plan del Día (daily planning)
     - Assign tables to waiters → Table assignment interface
     - Review modification requests → Approval workflows
  5. **Waiter Role Validation**:
     - Login as Mesero → View assigned tables
     - Create new order → Mesa Nueva Orden workflow
     - Process payments → Cuentas management
     - Request modifications → Modification request system
- **Database Testing**: Connection test occurs automatically on application startup
- **No Automated Tests**: Project contains no unit tests or integration tests. All validation must be manual.

## Build System Details

- **Build Tool**: Maven 3.8.5 with wrapper (`./mvnw`)
- **Java Version**: Java 17 (upgraded from original Java 16)
- **JavaFX Version**: 17.0.6 with Linux classifier (downgraded from 24.0.2 for compatibility)
- **Key Dependencies**:
  - Oracle JDBC driver (ojdbc11 23.3.0.23.09)
  - Oracle PKI security libraries
  - ControlsFX, FormsFX, BootstrapFX for UI components
- **Build Times**:
  - Clean compile: ~4-5 seconds
  - Test execution: ~8 seconds  
  - First build (downloading dependencies): ~15-20 seconds
- **Build Warnings**: JavaFX module extraction warnings are normal and can be ignored

## Project Structure

### Key Directories
```
Restaurantev2/                    # Main project directory
├── src/main/java/app/restaurantev2/  # Source code
├── src/main/resources/app/restaurantev2/ # FXML files and CSS
├── src/wallet/                   # Oracle database wallet files
├── pom.xml                       # Maven configuration
└── mvnw                         # Maven wrapper script
```

### Important Files
- `MainApp.java` - Application entry point with database connection logic
- `LoginController.java` - Handles user authentication
- `AdminController.java` - Administrator functionality
- `MeseroController.java` - Waiter functionality  
- `LiderMeserosController.java` - Waiter leader functionality
- `*.fxml` files - JavaFX UI definitions
- `module-info.java` - Java module configuration

### User Roles and Controllers
- **Admin**: Complete system access (users, categories, dishes, statistics)
- **Líder de Meseros** (Waiter Leader): Table assignments, approval workflows
- **Mesero** (Waiter): Order taking, table management, payment processing

## Common Issues and Solutions

### Compilation Errors
- **JavaFX version mismatch**: Use JavaFX 17.0.6 with Java 17
- **Module not found errors**: Ensure JavaFX dependencies have correct classifiers
- **Class version errors**: Verify Java 17 is being used for compilation

### Runtime Errors  
- **Display errors**: Normal in headless environments - GUI apps require display server
- **Database connection failures**: Check wallet path configuration and network connectivity
- **Module warnings**: Can be safely ignored

### Path Issues
- **Wallet path**: Hardcoded Windows path needs modification for Linux/Mac deployment
- **Resource loading**: Uses relative paths that work within Maven structure

## Development Workflow

1. **Before making changes**: Always run `./mvnw clean compile` to verify current state
2. **After making changes**: 
   - Compile: `./mvnw clean compile` 
   - Test database connection by attempting to run app (if GUI available)
   - Test specific user role workflows
3. **Code modifications**: Focus on controller classes for business logic, FXML for UI changes
4. **Database changes**: Requires Oracle database access and wallet reconfiguration

## Limitations

- **No automated testing**: All validation must be done manually
- **Database dependency**: Cannot run without Oracle database connection
- **GUI requirement**: Cannot demonstrate functionality in headless environments
- **Hardcoded configuration**: Database paths and credentials embedded in source code
- **Platform-specific**: Wallet paths and some configurations assume Windows environment

## Time Expectations

- **NEVER CANCEL**: Clean compile takes 4-5 seconds maximum. Set timeout to 60+ seconds.
- **NEVER CANCEL**: Test execution takes 8 seconds maximum. Set timeout to 30+ seconds. 
- **NEVER CANCEL**: First build with dependency download takes 15-20 seconds. Set timeout to 60+ seconds.
- **Application startup**: 2-3 seconds for GUI initialization (when display available)
- **Database connection**: 3-5 seconds for initial connection (when network accessible)

## Common tasks
The following are outputs from frequently run commands. Reference them instead of viewing, searching, or running bash commands to save time.

### Repository root structure
```
ls -la [repo-root]
.
..
.git
.github/
.idea
DOCUMENTACION_TECNICA_CONTROLADORES.md
FUNCIONALIDAD_USUARIOS.md  
README.md
RESUMEN_EJECUTIVO_PRESENTACION.md
Restaurantev2/
```

### Project directory structure  
```
ls -la Restaurantev2/
.
..
.gitignore
.idea
.mvn/
mvnw
mvnw.cmd
pom.xml
src/
```

### Maven commands reference
- `./mvnw clean` - Clean build artifacts
- `./mvnw compile` - Compile source code only
- `./mvnw clean compile` - Clean and compile (recommended)
- `./mvnw test` - Run tests (currently none exist)
- `./mvnw javafx:run` - Run the JavaFX application
- `./mvnw --version` - Check Maven version

### Java source structure
```
src/main/java/app/restaurantev2/
├── AdminController.java
├── CalificacionMeseroController.java  
├── CategoriasController.java
├── EstadisticasController.java
├── LiderMeserosController.java
├── LiderdeMeseros_PlandediaController.java
├── LoginController.java
├── MainApp.java
├── MeseroController.java
├── MeseroCuentasController.java
├── MeseroNuevaOrdenController.java
├── ModificacionController.java
├── PlatillosController.java
├── SolicitudesController.java
├── UsuariosController.java
└── module-info.java
```

## Technical Architecture

- **Pattern**: MVC (Model-View-Controller) with JavaFX
- **Database Access**: Direct JDBC with prepared statements  
- **UI Framework**: JavaFX with FXML and CSS styling
- **Module System**: Java Platform Module System (JPMS)
- **Security**: Oracle wallet-based authentication for database