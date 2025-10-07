package es.cheste.servicios;

import es.cheste.clases.Sigue;
import es.cheste.conexion.ConexionBD;
import es.cheste.enums.FrecuenciaInteraccion;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.SigueDAOImpl;
import es.cheste.interfaces.SigueDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase proporciona un conjunto de métodos para gestionar los seguimientos de los usuarios a sus jugadoras favoritas.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar seguimientos desde la base de datos.
 * Además, se realiza la creación de la tabla SIGUE, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar los entrenadores de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link SigueDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionSigue {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private SigueDAO sigueDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el objeto SigueDAO.
     *
     * <p>Esto permite que las operaciones sobre los seguimientos se realicen con la lógica definida en el DAO.</p>
     */
    public GestionSigue() {
        this.sigueDAO = new SigueDAOImpl();
    }

    /**
     * Crea la tabla SIGUE en la base de datos si aún no existe.
     *
     * <p>La tabla SIGUE incluye los siguientes campos: ID_USUARIO, ID_JUGADORA, FECHA_SEGUIMIENTO, FRECUENCIA_INTERACCION y OBSERVACIONES.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS SIGUE (" +
                "ID_USUARIO INT, " +
                "ID_JUGADORA INT, " +
                "FECHA_SEGUIMIENTO DATE, " +
                "FRECUENCIA_INTERACCION VARCHAR(20) NOT NULL CHECK(FRECUENCIA_INTERACCION IN ('DIARIO', 'SEMANAL', 'MENSUAL', 'ANUAL')), " +
                "OBSERVACIONES VARCHAR(100) NOT NULL, " +
                "PRIMARY KEY (ID_USUARIO, ID_JUGADORA, FECHA_SEGUIMIENTO), " +
                "FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO (ID_USUARIO), " +
                "FOREIGN KEY (ID_JUGADORA) REFERENCES JUGADORA (ID_JUGADORA));";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla SIGUE.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla SIGUE ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla SIGUE: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            LocalDate fecha1 = LocalDate.of(2022, 8, 25);
            LocalDate fecha2 = LocalDate.of(2021, 4, 10);
            LocalDate fecha3 = LocalDate.of(2022, 10, 10);
            LocalDate fecha4 = LocalDate.of(2024, 2, 11);
            LocalDate fecha5 = LocalDate.of(2023, 12, 1);

            sigueDAO.insertar(new Sigue(3, 4, fecha1, "SEMANAL", "Mi Jugadora favorita desde los juegos olimpicos"));
            sigueDAO.insertar(new Sigue(1, 5, fecha2, "MENSUAL", "Completamente merecido el primer puesto"));
            sigueDAO.insertar(new Sigue(5, 1, fecha3, "DIARIO", "Soy fan de ella desde que empezo en el mundo del voleibol"));
            sigueDAO.insertar(new Sigue(4, 2, fecha4, "ANUAL", "Hace mucho que deje de ver los partido de la VNL pero sigue siendo mi jugadora favorita"));
            sigueDAO.insertar(new Sigue(2, 3, fecha5, "SEMANAL", "En definitiva una de las mejores jugadoras del mundo"));

            LOGGER.info("Datos insertados correctamente en la tabla SIGUE");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla SIGUE: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar los seguimientos.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre los seguimientos.</p>
     */
    public void mostrarMenu() {
        int opcion;

        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarSigue();
                case 2 -> obtenerSiguePorId();
                case 3 -> listarSigue();
                case 4 -> actualizarSigue();
                case 5 -> eliminarSigue();
                case 6 -> System.out.println("Saliendo de la tabla 'SIGUE'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }

        } while (opcion != 6);
    }

    /**
     * Muestra las opciones del menú para gestionar seguimientos.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de seguimiento de los usuarios con sus jugadoras favoritas---");
        System.out.println("1. Agregar seguimiento del usuario");
        System.out.println("2. Obtener seguimiento por identificadores de Usuario y Jugadora");
        System.out.println("3. Mostrar lista de seguimientos de los usuarios");
        System.out.println("4. Actualizar seguimiento del usuario");
        System.out.println("5. Eliminar seguimiento del usuario");
        System.out.println("6. Salir");
    }

    /**
     * Lee y valida la opción elegida por el usuario en el menú. Asegura que el valor
     * introducido sea un número entre 1 y 6.
     *
     * @return la opción seleccionada por el usuario, un número entre 1 y 6.
     */
    private int leerOpcion() {
        int opcion = 0;
        boolean esOpcionValida = Boolean.FALSE;

        while (!esOpcionValida) {
            System.out.print("Elige una opción (1-6): ");
            try {
                opcion = sc.nextInt();
                sc.nextLine();

                if (opcion >= 1 && opcion <= 6) {
                    esOpcionValida = Boolean.TRUE;
                } else {
                    System.out.println("La opción elegida es incorrecta, tiene que ser un número entre 1 y 6, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una opción del menú, se tiene que introducir un número entero positivo: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido: ");
            }
        }
        return opcion;
    }

    /**
     * Solicita al usuario el ID de la jugadora o del usuario para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero mayor que 0.
     *
     * @return el ID de la jugadora o del usuario que sea correcto.
     */
    private int validarId(String tipoId) {
        int id = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {

            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("El ID no puede estar vacío.");
                    System.out.println("El ID es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                id = Integer.parseInt(entrada);

                if (id > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El número de ID {}tiene que ser mayor que 0: {}", tipoId, id);
                    System.out.println("El número del ID '" + tipoId + "' tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar un/una {} por su ID, se tiene que introducir un número entero mayor que 0: {}", tipoId, e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el ID: ");
            }
        }
        return id;
    }

    /**
     * Solicita al usuario la fecha de seguimiento para verificar que sea correcto. Además, se asegura
     * que la fecha este en formato dd-mm-yyyy.
     *
     * @return la fecha de seguimiento que tenga un formato correcto.
     */
    private LocalDate validarFecha() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fechaSeguimiento = null;
        boolean esFechaValida = Boolean.FALSE;

        do {
            String fechaStr = sc.nextLine();

            if (fechaStr.isEmpty()) {
                LOGGER.error("La fecha no puede estar vacía.");
                System.out.println("La fecha de seguimiento es un campo obligatorio, intentalo de nuevo: ");
                continue;
            }

            try {
                fechaSeguimiento = LocalDate.parse(fechaStr, formatter);
                esFechaValida = Boolean.TRUE;
            } catch (DateTimeParseException e) {
                LOGGER.error("El formato de la fecha es incorrecto, tiene que ser dd-MM-yyyy: {}", e.getMessage());
                System.out.println("El formato de la fecha es incorrecto, tiene que ser dd-MM-yyyy, intentalo de nuevo: ");
            }
        } while (!esFechaValida);
        return fechaSeguimiento;
    }

    /**
     * Este método solicita al usuario que introduzca la frecuencia de interacción y lo verifica con los valores definidos en el enum
     * {@link es.cheste.enums.FrecuenciaInteraccion}. Si la frecuencia de interacción no es válida se muestra un mensaje de error y se solicita
     * nuevamente hasta que el usuario introduzca una frecuencia válida.
     *
     * @return una cadena válida que representa la frecuencia de interacción introducida por el usuario.
     */
    private String verificarFrecuenciaInteraccion() {
        String frecuencia;
        boolean esValido = Boolean.FALSE;

        do {
            frecuencia = sc.nextLine().toUpperCase();

            if (frecuencia.isEmpty()) {
                LOGGER.error("La frecuencia de interacción no puede estar vacía.");
                System.out.println("La frecuencia de interacción del usuario es un campo obligatorio, intentalo de nuevo: ");
                continue;
            }

            try {
                FrecuenciaInteraccion.valueOf(frecuencia);
                esValido = Boolean.TRUE;

            } catch (IllegalArgumentException e) {
                LOGGER.info("La frecuencia de interacción no es válida. Los valores permitidos son: DIARIO, SEMANAL, MENSUAL, ANUAL");
                System.out.println("La frecuencia de interacción no es válida. Los valores permitidos son: DIARIO, SEMANAL, MENSUAL, ANUAL, intentalo de nuevo: ");
            }

        } while (!esValido);
        return frecuencia;
    }

    /**
     * Este método se encarga de verificar que la observación sea válida.
     *
     * <p>La observación debe ser una cadena de texto que contenga solo letras, tanto en mayúsculas como en minúsculas,
     * puede incluir espacios en el caso de que haya más de una palabra.</p>
     *
     * <p>Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.</p>
     *
     * @return una cadena válida que representa la observación introducida por el usuario.
     */
    private String verificarObservacion() {
        String observacion;
        String patron = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$";

        do {
            observacion = sc.nextLine();

            if (!observacion.matches(patron)) {
                LOGGER.info("El campo 'observación' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios).");
                System.out.println("El campo 'observación' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios), recuerda que es un campo obligatorio, intentalo de nuevo: ");
            }

        } while (!observacion.matches(patron));
        return observacion;
    }

    /**
     * Solicita los datos necesarios para crear un nuevo seguimiento.
     *
     * <p>Estos datos incluyen el nivelInteraccion, frecuenciaInteraccion y las observaciones.</p>
     *
     * @return un objeto {@link Sigue} con los datos introducidos por el usuario.
     */
    private Sigue preguntarDatosSigue() {
        System.out.println("Introduzca el ID del usuario (este campo es obligatorio): ");
        int idUsuario = validarId("Usuario");

        System.out.println("Introduzca el ID de la jugadora (este campo es obligatorio): ");
        int idJugadora = validarId("Jugadora");

        System.out.print("Introduzca la fecha (dd-mm-yyyy) (este campo es obligatorio): ");
        LocalDate fecha = validarFecha();

        System.out.print("Introduzca la frecuencia de interaccion del usuario (este campo es obligatorio): ");
        String frecuenciaInteraccion = verificarFrecuenciaInteraccion();

        System.out.println("Introduzca las observaciones que desee hacer de la jugadora (este campo es obligatorio): ");
        String observaciones = verificarObservacion();
        sc.nextLine();

        return new Sigue(idUsuario, idJugadora, fecha, frecuenciaInteraccion, observaciones);
    }

    /**
     * Este método se encarga de agregar un nuevo seguimiento en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar el seguimiento a la base de datos.</p>
     */
    public void agregarSigue() {
        Sigue sigue = preguntarDatosSigue();
        try {
            sigueDAO.insertar(sigue);
            LOGGER.info("El seguimiento del usuario con ID: {}, sobre su jugadora favorita con ID: {}, con la fecha: {}, ha sido agregada correctamente.", sigue.getIdUsuario(), sigue.getIdJugadora(), sigue.getFechaSeguimiento());
            System.out.println("Usuario agregado con ID: " + sigue.getIdUsuario() + ", sobre su jugadora favorita con el ID: " + sigue.getIdJugadora() + ", con la fecha: " + sigue.getFechaSeguimiento());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar el usuario con el ID: {}, sobre su jugadora favorita con el ID: {}, con la fecha: {}, {}", sigue.getIdUsuario(), sigue.getIdJugadora(), sigue.getFechaSeguimiento(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener un seguimiento a partir de los ID del usuario y la jugadora.
     *
     * <p>Solicita al usuario los IDs y obtiene los datos del seguimiento desde la base de datos.</p>
     *
     * @return un objeto {@link Sigue} con sus correspondientes datos, o un valor null si no se encuentra un seguimiento con los IDs proporcionados.
     */
    public Sigue obtenerSiguePorId() {
        int idUsuario = validarId("Usuario");
        int idJugadora = validarId("Jugadora");
        LocalDate fechaSeguimiento = validarFecha();

        try {
            Sigue sigue = sigueDAO.obtenerPorId(idUsuario, idJugadora, fechaSeguimiento);
            if (sigue != null) {
                LOGGER.info("Se ha encontrado el usuario con el ID: {}, sobre su jugadora favorita con el ID: {}, con la fecha: {}", idUsuario, idJugadora, fechaSeguimiento);
                System.out.println("Usuario encontrado por ID: " + idUsuario + ", sobre la jugadora con el ID: " + idJugadora);
                return sigue;
            } else {
                LOGGER.info("No se ha encontrado ningun usuario con el ID proporcionado: {}, sobre su jugadora favorita con el ID: {}, con la fecha: {}", idUsuario, idJugadora, fechaSeguimiento);
                System.out.println("No se encontro usuario con ID: " + idUsuario + ", sobre su jugadora favorita con ID: " + idJugadora + ", con fecha: " + fechaSeguimiento);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar el usuario con el ID: {}, sobre su jugadora favorita con el ID: {}, con fecha: {}, {}", idUsuario, idJugadora, fechaSeguimiento, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todos los seguimientos registrados.
     *
     * <p>Recupera y muestra todos los seguimientos almacenados en la base de datos.</p>
     *
     * @return una lista con todos los seguimientos, o devuelve un valor null si la lista está vacía.
     */
    public List<Sigue> listarSigue() {
        try {
            List<Sigue> sigueList = sigueDAO.obtenerTodos();

            if (sigueList != null && !sigueList.isEmpty()) {
                System.out.println("---Lista de los seguimientos de los usuarios con sus jugadoras favoritas---");
                for (Sigue sigue : sigueList) {
                    System.out.println(sigue);
                }
                return sigueList;

            } else {
                System.out.println("La lista de interacciones de los usuarios con sus jugadoras favoritas en las fecha correspondiente está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de interacciones de los usuarios con sus jugadoras favoritas en las fecha correspondiente está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de un seguimiento existente.
     *
     * <p>Permite actualizar los datos de un seguimiento. El usuario debe introducir los IDs del usuario y la jugadora que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarSigue() {
        int idUsuario = validarId("Usuario");
        int idJugadora = validarId("Jugadora");
        LocalDate fechaSeguimiento = validarFecha();
        System.out.println("Introduzca los nuevos datos de las interacciones del usuario con la jugadora: ");

        Sigue sigue = preguntarDatosSigue();
        sigue.setIdUsuario(idUsuario);
        sigue.setIdJugadora(idJugadora);
        sigue.setFechaSeguimiento(fechaSeguimiento);

        try {
            sigueDAO.actualizar(sigue);
            LOGGER.info("Se ha actualizado el seguimiento del usuario con ID: {}, sobre su jugadora favorita con ID: {}, con fecha: {}", idUsuario, idJugadora, fechaSeguimiento);
            System.out.println("Usuario actualizado con la jugadora correspondiente: " + sigue);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar el usuario con ID: {}, sobre su jugadora favorita con ID: {}, con fecha: {}, {}", idUsuario, idJugadora, fechaSeguimiento, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar un seguimiento de la base de datos.
     *
     * <p>Permite eliminar un seguimiento de la base de datos mediante sus IDs. Se solicita al
     * usuario los IDs del usuario y la jugadora y, si existe, se eliminará correctamente.</p>
     */
    public void eliminarSigue() {
        int idUsuario = validarId("Usuario");
        int idJugadora = validarId("Jugadora");
        LocalDate fechaSeguimiento = validarFecha();

        try {
            sigueDAO.eliminar(idUsuario, idJugadora, fechaSeguimiento);
            LOGGER.info("Se ha eliminado el usuario correctamente con ID: {}, sobre su jugadora favorita con ID: {}, con fecha: {}", idUsuario, idJugadora, fechaSeguimiento);
            System.out.println("Usuario eliminado correctamente con ID: " + idUsuario + ", sobre su jugadora favorita con ID: " + idJugadora + ", con fecha: " + fechaSeguimiento);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar el usuario con ID: {}, sobre su jugadora favorita con ID: {}, con fecha: {}, {}", idUsuario, idJugadora, fechaSeguimiento, e.getMessage());
        }
    }
}
