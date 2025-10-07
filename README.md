# 🏐 Gestión de Equipo de Voleibol 

## Descripción
Aplicación Java realizada para la gestión de equipos, jugadoras y partidos de voleibol. Se conecta a **PostgreSQL** mediante **JDBC** utilizando el patrón **DAO** y permite registrar estadísticas, opiniones de fans y generar rankings de jugadoras más destacadas.

---

## Objetivos
- Aprender y aplicar el patrón **DAO** para acceso a datos.
- Practicar operaciones **CRUD** en Java conectadas a PostgreSQL.
- Implementar un **procedimiento almacenado** para ranking de jugadoras.
- Gestionar datos de jugadoras, entrenadores, usuarios en una estructura relacional.

---

## Funcionalidades
- Registro de **estadísticas** y asignación a jugadoras y partidos.
- Registro de **opiniones de fans** sobre jugadoras favoritas.
- Consulta de **ranking de jugadoras** según puntos.
- Gestión de seguimiento de jugadoras por usuarios.

## Modelo relacional
![Modelo relacional Voleibol](src/main/resources/img/modelo-relacional-voleibol.png "Modelo relacional Voleibol")

---

## Stack tecnológico

**Backend:** Java 17, JDBC

**Base de Datos:** PostgreSQL

**Librerías:** Lombok, Log4j

**Gestión:** Maven

**Control de versiones:** Git y GitHub

---

## Instalación

### Requisitos
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Configuración

1. Crear base de datos:
```sql
CREATE DATABASE voleibol;
```

2. Configurar **application.properties:**
```properties
db.url=jdbc:postgresql://localhost:5432/voleibol
db.username=tu_usuario
db.password=tu_contraseña
```

3. Ejecutar:
```bash
git clone https://github.com/DayanaraMontero/voley-management.git
cd voley-management
mvn clean install
mvn exec:java -Dexec.mainClass="Main"
```

---

## 📁 Estructura del proyecto

```
voley-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── es/
│   │   │       └── cheste/
│   │   │           ├── clases/            # Clases: Jugadora, Entrenador, Usuario, Estadistica, Opinion, Juega, Sigue
│   │   │           ├── conexion/          # Conexión a PostgreSQL
│   │   │           ├── enums/             # Enumeraciones: EstadoJugadora, PosicionJugadora, FrecuenciaInteraccion
│   │   │           ├── excepcion/         # Excepciones personalizadas, ExcepcionDAO
│   │   │           ├── interfaces/        # Interfaces DAO
│   │   │           ├── impl/              # Implementación de interfaces DAO
│   │   │           ├── servicios/         # Lógica de negocio o servicios
│   │   │           └── Main.java          # Clase principal
│   │   └── resources/
│   │       ├── img/                       # Imágenes, en este caso modelo relacional
│   │       ├── sql/                       # Scripts SQL: procedimiento almacenado, crear/eliminar tablas
│   │       ├── application.properties     # Configuración de conexión a BD
│   │       └── log4j2.xml                 # Configuración del logging
├── .gitignore
├── app.log                                # Fichero de logs generado
├── pom.xml                                # Dependencias y configuración Maven
└── README.md
```

---

## Licencia
Proyecto desarrollado con fines educativos como parte del ciclo **DAM** en el **CIPFP Cheste.**


