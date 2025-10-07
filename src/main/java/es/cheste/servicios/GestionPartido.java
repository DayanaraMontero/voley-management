package es.cheste.servicios;

import es.cheste.clases.Partido;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.PartidoDAOImpl;
import es.cheste.interfaces.PartidoDAO;
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
 * Esta clase proporciona un conjunto de métodos para gestionar partidos en el sistema.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar partidos desde la base de datos.
 * Además, se realiza la creación de la tabla PARTIDO, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar los partidos de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link PartidoDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionPartido {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private final PartidoDAO partidoDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el objeto PartidoDAO.
     *
     * <p>Esto permite que las operaciones sobre los partidos se realicen con la lógica definida en el DAO.</p>
     */
    public GestionPartido() {
        this.partidoDAO = new PartidoDAOImpl();
    }

    /**
     * Crea la tabla PARTIDO en la base de datos si aún no existe.
     *
     * <p>La tabla PARTIDO incluye los siguientes campos: ID_PARTIDO, FECHA, EQUIPO_COMPETIDOR1, ENTRENADOR_COMPETIDOR1, EQUIPO_COMPETIDOR2, ENTRENADOR_COMPETIDOR2, RESULTADO y DURACION.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS PARTIDO (" +
                "ID_PARTIDO SERIAL PRIMARY KEY, " +
                "FECHA DATE NOT NULL, " +
                "EQUIPO_COMPETIDOR1 VARCHAR(50) NOT NULL, " +
                "ENTRENADOR_COMPETIDOR1 INT, " +
                "FOREIGN KEY (ENTRENADOR_COMPETIDOR1) REFERENCES ENTRENADOR (ID_ENTRENADOR), " +
                "EQUIPO_COMPETIDOR2 VARCHAR(50) NOT NULL, " +
                "ENTRENADOR_COMPETIDOR2 INT, " +
                "FOREIGN KEY (ENTRENADOR_COMPETIDOR2) REFERENCES ENTRENADOR (ID_ENTRENADOR), " +
                "RESULTADO VARCHAR(80) NOT NULL, " +
                "DURACION INT NOT NULL CHECK(DURACION > 0))";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla PARTIDO.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla PARTIDO ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla PARTIDO: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            LocalDate fecha1 = LocalDate.of(2021, 5, 18);
            LocalDate fecha2 = LocalDate.of(2020, 8, 24);
            LocalDate fecha3 = LocalDate.of(2023, 2, 4);
            LocalDate fecha4 = LocalDate.of(2024, 3, 15);
            LocalDate fecha5 = LocalDate.of(2022, 7, 21);

            partidoDAO.insertar(new Partido(fecha1, "Equipo E", 1, "Equipo B", 3, "El Equipo B ha ganado el partido", 156));
            partidoDAO.insertar(new Partido(fecha2, "Equipo A", 4, "Equipo D", 2, "El Equipo D ha ganado el partido", 172));
            partidoDAO.insertar(new Partido(fecha3, "Equipo C", 5, "Equipo B", 3, "El Equipo B ha ganado el partido", 142));
            partidoDAO.insertar(new Partido(fecha4, "Equipo A", 4, "Equipo C", 5, "El Equipo A ha ganado el partido", 158));
            partidoDAO.insertar(new Partido(fecha5, "Equipo D", 2, "Equipo E", 1, "El Equipo E ha ganado el partido", 184));

            LOGGER.info("Datos insertados correctamente en la tabla PARTIDO");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla PARTIDO: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar los partidos.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre los partidos.</p>
     */
    public void mostrarMenu() {
        int opcion;

        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarPartido();
                case 2 -> obtenerPartidoPorId();
                case 3 -> listarPartido();
                case 4 -> actualizarPartido();
                case 5 -> eliminarPartido();
                case 6 -> System.out.println("Saliendo de la tabla 'PARTIDO'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }

        } while (opcion != 6);
    }

    /**
     * Muestra las opciones del menú para gestionar partidos.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de los Partidos---");
        System.out.println("1. Agregar partido");
        System.out.println("2. Obtener partido por ID");
        System.out.println("3. Mostrar lista de partidos");
        System.out.println("4. Actualizar partido");
        System.out.println("5. Eliminar partido");
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
     * Solicita al usuario el ID del partido para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero positivo.
     *
     * @return el ID del partido que sea correcto.
     */
    private int validarId() {
        int id = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {
            System.out.println("Introduzca el ID del partido: ");

            try {
                id = sc.nextInt();
                sc.nextLine();

                if (id > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El número de ID del partido tiene que ser mayor que 0: {}", id);
                    System.out.println("El número del ID tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar un partido por su ID, se tiene que introducir un número entero mayor que 0: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el ID del partido: ");
            }
        }
        return id;
    }

    /**
     * Este método se encarga de verificar que la fecha del partido sea válida.
     *
     * <p>La fecha debe tener un formato específico dd-mm-yyyy, y no puede estar vacío.</p>
     * <p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa la fecha del partido introducido por el usuario.
     */
    private LocalDate verificarFecha() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fecha = null;
        boolean esFechaValida = Boolean.FALSE;

        do {
            String fechaStr = sc.nextLine();

            if (fechaStr.isEmpty()) {
                LOGGER.error("La fecha no puede estar vacía.");
                System.out.println("La fecha del partido es un campo obligatorio, intentalo de nuevo: ");
                continue;
            }

            try {
                fecha = LocalDate.parse(fechaStr, formatter);
                esFechaValida = Boolean.TRUE;
            } catch (DateTimeParseException e) {
                LOGGER.error("El formato de la fecha es incorrecto, tiene que ser dd-MM-yyyy: {}", e.getMessage());
                System.out.println("El formato de la fecha es incorrecto, tiene que ser dd-MM-yyyy, intentalo de nuevo: ");
            }
        } while (!esFechaValida);
        return fecha;
    }

    /**
     * Este método se encarga de verificar el atributo, nombre del equipo.
     *
     * <p>Solicita al usuario que introduzca un nombre de equipo y verifica que cumpla las siguientes condiciones: </p>
     * <ul>
     *     <li>Cada palabra debe empezar con una letra mayúscula.</li>
     *     <li>Tiene que tener al menos tres caracteres.</li>
     *     <li>Solo se permiten letras y espacios, en caso de ser necesario.</li>
     * </ul>
     * <p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa el nombre del equipo introducido por el usuario.
     */
    private String verificarNombreEquipo() {
        String nombreEquipo;
        String patron = "([A-Z][a-zA-Z]*)+( [A-Z][a-zA-Z]*)*";

        do {
            nombreEquipo = sc.nextLine();

            if (nombreEquipo.length() < 3 || !nombreEquipo.matches(patron)) {
                LOGGER.info("Cada palabra debe empezar por mayúscula, tener al menos tres caracteres y solo contener letras y en caso de ser necesario espacios.");
                System.out.println("Cada palabra debe empezar por mayúscula, tener al menos tres caracteres y solo contener letras y en caso de ser necesario espacios, intentalo de nuevo: ");
            }

        } while (nombreEquipo.length() < 3 || !nombreEquipo.matches(patron));

        return nombreEquipo;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para el campo ID del entrenadpr.
     * Si el usuario decide dejar el campo vacío también se considera válido.
     *
     * <p>Si el valor introducido no es un número o es menor o igual a cero, se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero mayor que cero introducido por el usuario.
     */
    private int verificarIdEntrenador() {
        int idEntrenador = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            String entrada = sc.nextLine();

            if (entrada.isEmpty()) {
                return idEntrenador;
            }

            try {
                idEntrenador = Integer.parseInt(entrada);

                if (idEntrenador > 0) {
                    esEntradaValida = Boolean.TRUE;
                } else {
                    LOGGER.info("Debes introducir un número mayor que 0 para el ID del entrenador.");
                    System.out.println("Por favor, introduzca un número mayor que 0 para el ID del entrenador: ");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un número entero mayor que 0 para el ID del entrenador.");
                System.out.println("Por favor, introduzca un número entero mayor que 0 para el ID del entrenador: ");
                sc.nextLine();
            }

        } while (!esEntradaValida);
        return idEntrenador;
    }

    /**
     * Este método se encarga de verificar el campo resultado para los partidos.
     *
     * <p>Solicita al usuario que introduzca el resultado y verifica que cumpla las siguientes condiciones para el nombre del equipo: </p>
     * <ul>
     *     <li>Cada palabra debe empezar con una letra mayúscula.</li>
     *     <li>Tiene que tener al menos tres caracteres.</li>
     *     <li>Solo se permiten letras y espacios, en caso de ser necesario.</li>
     * </ul>
     *
     * <p>Además, el resultado debe seguir un formato específico, por ejemplo: Equipo A 3 - 1 Equipo B (Nombre del equipo 1 resultado - resultado Nombre del equipo 2)</p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa el resultado introducido por el usuario.
     */
    private String verificarResultado() {
        String resultado;
        String patron = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$";

        do {
            resultado = sc.nextLine();

            if (!resultado.matches(patron)) {
                LOGGER.info("El campo 'resultado' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios).");
                System.out.println("El campo 'resultado' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios), intentalo de nuevo: ");
            }

        } while (!resultado.matches(patron));
        return resultado;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para la duración del partido.
     *
     * <p>Si el valor introducido no es un número o es menor o igual que 0, se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero mayor que 0 introducidos por el usuario.
     */
    private int verificarDuracion() {
        int duracion = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("La duración no puede estar vacía.");
                    System.out.println("La duración es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                duracion = Integer.parseInt(entrada);

                if (duracion > 0) {
                    esEntradaValida = Boolean.TRUE;
                } else {
                    LOGGER.info("El tiempo de la duración debe ser un número entero mayor que 0.");
                    System.out.println("Por favor, introduzca un número entero mayor que 0: ");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un número entero mayor que 0 para la duración del partido.");
                System.out.println("Por favor, introduzca un número entero mayor que 0 para la duración del partido: ");
                sc.nextLine();
            }

        } while (!esEntradaValida);
        return duracion;
    }

    /**
     * Solicita los datos necesarios para crear un nuevo partido.
     *
     * <p>Estos datos incluyen la fecha, nombre del primer equipo competidor, entrenador del primer equipo competidor, nombre del segundo equipo competidor,
     * entrenador del segundo equipo competidor, resultado y duración.</p>
     *
     * @return un objeto {@link Partido} con los datos introducidos por el usuario.
     */
    private Partido preguntarDatosPartido() {
        System.out.print("Introduzca la fecha (dd-mm-yyyy) (este campo es obligatorio): ");
        LocalDate fecha = verificarFecha();

        System.out.print("Introduzca el equipo competidor 1 (este campo es obligatorio): ");
        String equipoCompetidor1 = verificarNombreEquipo();

        System.out.print("Introduzca el ID del entrenador del equipo competidor 1: ");
        int entrenador1 = verificarIdEntrenador();

        System.out.print("Introduzca el equipo competidor 2 (este campo es obligatorio): ");
        String equipoCompetidor2 = verificarNombreEquipo();

        System.out.print("Introduzca el ID del entrenador del equipo competidor 2: ");
        int entrenador2 = verificarIdEntrenador();

        System.out.print("Introduzca el resultado (este campo es obligatorio): ");
        String resultado = verificarResultado();

        System.out.print("Introduzca la duración en minutos (este campo es obligatorio): ");
        int duracion = verificarDuracion();
        sc.nextLine();

        return new Partido(fecha, equipoCompetidor1, entrenador1, equipoCompetidor2, entrenador2, resultado, duracion);
    }

    /**
     * Este método se encarga de agregar un nuevo partido en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar el partido a la base de datos.</p>
     */
    public void agregarPartido() {
        Partido partido = preguntarDatosPartido();
        try {
            partidoDAO.insertar(partido);
            LOGGER.info("Partido agregado correctamente con ID: {}", partido.getIdPartido());
            System.out.println("Partido agregado con ID: " + partido.getIdPartido());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar el partido con el ID: {}, {}", partido.getIdPartido(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener un partido a partir de su ID.
     *
     * <p>Solicita al usuario el ID y obtiene los datos del partido desde la base de datos.</p>
     *
     * @return un objeto {@link Partido} con sus correspondientes datos, o un valor null si no se encuentra un partido con el ID proporcionado.
     */
    public Partido obtenerPartidoPorId() {
        int id = validarId();
        try {
            Partido partido = partidoDAO.obtenerPorId(id);
            if (partido != null) {
                LOGGER.info("Se ha encontrado el partido correctamente por ID: {}", id);
                System.out.println("Partido encontrado por ID: " + partido);
                return partido;
            } else {
                LOGGER.info("No se ha encontrado ningun partido con el ID proporcionado: {}", id);
                System.out.println("No se encontro partido con ID: " + id);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar el partido con el ID: {}, {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todos los partidos registrados.
     *
     * <p>Recupera y muestra todos los partidos almacenados en la base de datos.</p>
     *
     * @return una lista con todos los partidos, o devuelve un valor null si la lista está vacía.
     */
    public List<Partido> listarPartido() {
        try {
            List<Partido> partidoList = partidoDAO.obtenerTodos();

            if (partidoList != null && !partidoList.isEmpty()) {
                System.out.println("---Lista de partidos---");
                for (Partido partido : partidoList) {
                    System.out.println(partido);
                }
                return partidoList;

            } else {
                System.out.println("No se encontraron partidos, la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de partidos está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de un partido existente.
     *
     * <p>Permite actualizar los datos de un partido. El usuario debe introducir el ID del partido que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarPartido() {
        int id = validarId();
        System.out.println("Introduzca los nuevos datos del partido: ");

        Partido partido = preguntarDatosPartido();
        partido.setIdPartido(id);
        try {
            partidoDAO.actualizar(partido);
            LOGGER.info("Se ha actualizado el partido correctamente con ID: {}", id);
            System.out.println("Partido actualizado: " + partido);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar el partido con ID: {}, {}", id, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar un partido de la base de datos.
     *
     * <p>Permite eliminar un partido de la base de datos mediante su ID. Se solicita al
     * usuario el ID del partido y, si existe, se eliminará correctamente.</p>
     */
    public void eliminarPartido() {
        int id = validarId();
        try {
            partidoDAO.eliminar(id);
            LOGGER.info("Se ha eliminado el partido correctamente con ID: {}", id);
            System.out.println("Partido eliminado correctamente con ID: " + id);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar el partido con ID: {}, {}", id, e.getMessage());
        }
    }
}
