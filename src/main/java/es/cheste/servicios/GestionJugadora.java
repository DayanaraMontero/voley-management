package es.cheste.servicios;

import es.cheste.clases.Jugadora;
import es.cheste.conexion.ConexionBD;
import es.cheste.enums.PosicionJugadora;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.JugadoraDAOImpl;
import es.cheste.interfaces.JugadoraDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase proporciona un conjunto de métodos para gestionar jugadoras en el sistema.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar jugadoras desde la base de datos.
 * Además, se realiza la creación de la tabla JUGADORA, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar las jugadoras de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link JugadoraDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionJugadora {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private JugadoraDAO jugadoraDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el objeto JugadoraDAO.
     *
     * <p>Esto permite que las operaciones sobre las jugadoras se realicen con la lógica definida en el DAO.</p>
     */
    public GestionJugadora() {
        this.jugadoraDAO = new JugadoraDAOImpl();
    }

    /**
     * Crea la tabla JUGADORA en la base de datos si aún no existe.
     *
     * <p>La tabla JUGADORA incluye los siguientes campos: ID_JUGADORA, NOMBRE, APELLIDOS, POSICION, NACIONALIDAD, EDAD, DORSAL, NOM_EQUIPO e ID_ENTRENADOR.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS JUGADORA (" +
                "ID_JUGADORA SERIAL PRIMARY KEY, " +
                "NOMBRE VARCHAR(30) NOT NULL, " +
                "APELLIDOS VARCHAR(50) NOT NULL, " +
                "POSICION VARCHAR(20) NOT NULL CHECK(UPPER(POSICION) IN ('COLOCADORA', 'OPUESTO', 'CENTRAL', 'LIBERO', 'ATACANTE')), " +
                "NACIONALIDAD VARCHAR(25), " +
                "EDAD INT NOT NULL CHECK(EDAD >= 16 AND EDAD <= 50), " +
                "DORSAL INT NOT NULL CHECK(DORSAL >= 1 AND DORSAL <= 99), " +
                "NOM_EQUIPO VARCHAR(30) NOT NULL, " +
                "ID_ENTRENADOR INT, " +
                "FOREIGN KEY (ID_ENTRENADOR) REFERENCES ENTRENADOR (ID_ENTRENADOR));";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla JUGADORA.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla JUGADORA ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla JUGADORA: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            jugadoraDAO.insertar(new Jugadora("Brenda", "Castillo", "LIBERO", "Dominicana", 38, 5, "Equipo A", 2));
            jugadoraDAO.insertar(new Jugadora("Brayelin", "Martinez", "ATACANTE", "Cubana", 25, 14, "Equipo B", 5));
            jugadoraDAO.insertar(new Jugadora("Melisa", "Vargas", "OPUESTO", "Italiana", 29, 45, "Equipo C", 1));
            jugadoraDAO.insertar(new Jugadora("Lara", "Morgan", "COLOCADORA", "Estadounidense", 24, 58, "Equipo D", 3));
            jugadoraDAO.insertar(new Jugadora("Daiana", "Leyba", "CENTRAL", "Mexicana", 21, 2, "Equipo E", 4));

            LOGGER.info("Datos insertados correctamente en la tabla JUGADORA");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla JUGADORA: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar las jugadoras.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre las jugadoras.</p>
     */
    public void mostrarMenu() {
        int opcion;

        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarJugadora();
                case 2 -> obtenerJugadoraPorId();
                case 3 -> listarJugadora();
                case 4 -> listarJugadorasConEntrenadores();
                case 5 -> actualizarJugadora();
                case 6 -> eliminarJugadora();
                case 7 -> System.out.println("Saliendo de la tabla 'JUGADORA'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }

        } while (opcion != 7);
    }

    /**
     * Muestra las opciones del menú para gestionar jugadoras.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de las Jugadoras---");
        System.out.println("1. Agregar jugadora");
        System.out.println("2. Obtener jugadora por ID");
        System.out.println("3. Mostrar lista de jugadoras");
        System.out.println("4. Mostrar la lista de jugadoras con sus entrenadores");
        System.out.println("5. Actualizar jugadora");
        System.out.println("6. Eliminar jugadora");
        System.out.println("7. Salir");
    }

    /**
     * Lee y valida la opción elegida por el usuario en el menú. Asegura que el valor
     * introducido sea un número entre 1 y 7.
     *
     * @return la opción seleccionada por el usuario, un número entre 1 y 7.
     */
    private int leerOpcion() {
        int opcion = 0;
        boolean esOpcionValida = Boolean.FALSE;

        while (!esOpcionValida) {
            System.out.print("Elige una opción (1-7): ");
            try {
                opcion = sc.nextInt();
                sc.nextLine();

                if (opcion >= 1 && opcion <= 7) {
                    esOpcionValida = Boolean.TRUE;
                } else {
                    System.out.println("La opción elegida es incorrecta, tiene que ser un número entre 1 y 7, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una opción del menú, se tiene que introducir un valor numérico entero positivo: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido: ");
            }
        }
        return opcion;
    }

    /**
     * Solicita al usuario el ID de la jugadora para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero positivo.
     *
     * @return el ID de la jugadora que sea correcto.
     */
    private int validarIdJugadora() {
        int id = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {
            System.out.println("Introduzca el ID de la jugadora: ");

            try {
                id = sc.nextInt();
                sc.nextLine();

                if (id > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El número de ID de la jugadora no puede ser menor que 0: {}", id);
                    System.out.println("El número del ID tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una jugadora por su ID, se tiene que introducir un valor numérico entero positivo: {}", e.getMessage());
            }
        }
        return id;
    }

    /**
     * Este método se encarga de verificar el campo nombre, apellido o nombre del equipo.
     *
     * <p>Solicita al usuario que introduzca un nombre, apellido o nombre del equipo y verifica que cumpla las siguientes condiciones: </p>
     * <ul>
     *     <li>Cada palabra debe empezar con una letra mayúscula.</li>
     *     <li>Tiene que tener al menos tres caracteres.</li>
     *     <li>Solo se permiten letras y espacios, en caso de ser necesario.</li>
     * </ul>
     * <p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa el nombre, apellido o nombre del equipo introducido por el usuario.
     */
    private String verificarNombreApellidoNomEquipo() {
        String campo;
        String patron = "([A-Z][a-zA-Z]*)+( [A-Z][a-zA-Z]*)*";

        do {
            campo = sc.nextLine();

            if (campo.length() < 3 || !campo.matches(patron)) {
                LOGGER.info("Cada palabra debe empezar por mayúscula, tener al menos tres caracteres y solo contener letras y en caso de ser necesario espacios.");
                System.out.println("Cada palabra debe empezar por mayúscula, tener al menos tres caracteres y solo contener letras y en caso de ser necesario espacios, intentalo de nuevo: ");
            }

        } while (campo.length() < 3 || !campo.matches(patron));

        return campo;
    }

    /**
     * Este método solicita al usuario que introduzca una posición y lo verifica con los valores definidos en el enum
     * {@link es.cheste.enums.PosicionJugadora}. Si la posición no es válida se muestra un mensaje de error y se solicita
     * nuevamente hasta que el usuario introduzca una posición válida.
     *
     * @return una cadena válida que representa la posición de la jugadora introducida por el usuario.
     */
    private String verificarPosicion() {
        String posicion;
        boolean esValido = Boolean.FALSE;

        do {
            posicion = sc.nextLine().toUpperCase();

            try {
                PosicionJugadora.valueOf(posicion);
                esValido = Boolean.TRUE;

            } catch (IllegalArgumentException e) {
                LOGGER.info("La posición no es válida. Los valores permitidos son: COLOCADORA, OPUESTO, CENTRAL, LIBERO, ATACANTE");
                System.out.println("La posición no es válida. Los valores permitidos son: COLOCADORA, OPUESTO, CENTRAL, LIBERO, ATACANTE, recuerda que es un campo obligatorio, intentalo de nuevo: ");
            }

        } while (!esValido);
        return posicion;
    }

    /**
     * Este método se encarga de verificar que la nacionalidad sea válida.
     *
     * <p>La nacionalidad debe ser una cadena de texto que contenga solo letras, tanto en mayúsculas como en minúsculas,
     * puede incluir espacios en el caso de que haya más de una palabra. El campo también puede estar vacío, porque no es
     * un campo obligatorio en la base de datos.</p>
     * <p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa la nacionalidad introducida por el usuario.
     */
    private String verificarNacionalidad() {
        String nacionalidad;
        String patron = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$";

        do {
            nacionalidad = sc.nextLine();

            if (nacionalidad.isEmpty()) {
                return nacionalidad;
            }

            if (!nacionalidad.matches(patron)) {
                LOGGER.info("El campo 'nacionalidad' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios).");
                System.out.println("El campo 'nacionalidad' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios), intentalo de nuevo: ");
            }

        } while (!nacionalidad.matches(patron));
        return nacionalidad;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para edad de la jugadora.
     *
     * <p>Si el valor introducido no es un número o no está dentro del rango de edades (16-50), se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero entre 16 y 50 introducidos por el usuario.
     */
    private int verificarEdad() {
        int edad = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("La edad no puede estar vacía.");
                    System.out.println("La edad de la jugadora es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                edad = Integer.parseInt(entrada);

                if (edad < 16 || edad > 50) {
                    LOGGER.info("La edad debe ser un número entre 16 y 50.");
                    System.out.println("Por favor, introduzca un número entre 16 y 50, intentalo de nuevo: ");
                } else {
                    esEntradaValida = Boolean.TRUE;
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un número entero para la edad.");
                System.out.println("Por favor, introduzca un número válido para la edad entre 16 y 50, intentalo de nuevo: ");
            }

        } while (!esEntradaValida);
        return edad;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para el número del dorsal de la jugadora.
     *
     * <p>Si el valor introducido no es un número o no está dentro del rango (1-99), se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero entre 1 y 99 introducidos por el usuario.
     */
    private int verificarDorsal() {
        int dorsal = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("El dorsal no puede estar vacío.");
                    System.out.println("El número del dorsal de la jugadora es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                dorsal = Integer.parseInt(entrada);

                if (dorsal < 1 || dorsal > 99) {
                    LOGGER.info("El número del dorsal debe ser un número entre 1 y 99.");
                    System.out.println("Por favor, introduzca un número entre 1 y 99: ");
                } else {
                    esEntradaValida = Boolean.TRUE;
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un número entero para el número del dorsal.");
                System.out.println("Por favor, introduzca un número válido para el número del dorsal entre 1 y 99: ");
            }

        } while (!esEntradaValida);
        return dorsal;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para el campo ID del entrenador.
     * Si el usuario decide dejar el campo vacío también se considera válido.
     *
     * <p>Devuelve un 0 si el usuario deja el campo vacío, lo que indica que el identificador del entrenador no se han proporcionado.</p>
     *
     * <p>Si el valor introducido no es un número o es menor o igual a cero, se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero mayor que cero introducido por el usuario.
     */
    private int verificarIdEntrenador() {
        int idEntrenador = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {

            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("El ID no puede estar vacío.");
                    System.out.println("El ID es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                idEntrenador = Integer.parseInt(entrada);

                if (idEntrenador > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El ID tiene que ser mayor que 0: {}", idEntrenador);
                    System.out.println("El número del ID tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar un entrenador por su ID, se tiene que introducir un número entero mayor que 0: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el ID: ");
            }
        }
        return idEntrenador;
    }

    /**
     * Solicita los datos necesarios para crear una nueva jugadora.
     *
     * <p>Estos datos incluyen el nombre, apellidos, posicion, nacionalidad, edad, dorsal, nombre del equipo y el identificador único del entrenador.</p>
     *
     * @return un objeto {@link Jugadora} con los datos introducidos por el usuario.
     */
    private Jugadora preguntarDatosJugadora() {
        System.out.print("Introduzca el nombre (este campo es obligatorio): ");
        String nombre = verificarNombreApellidoNomEquipo();

        System.out.print("Introduzca los apellidos (este campo es obligatorio): ");
        String apellidos = verificarNombreApellidoNomEquipo();

        System.out.print("Introduzca la posicion (este campo es obligatorio): ");
        String posicion = verificarPosicion();

        System.out.print("Introduzca la nacionalidad: ");
        String nacionalidad = verificarNacionalidad();

        System.out.print("Introduzca la edad (este campo es obligatorio): ");
        int edad = verificarEdad();

        System.out.print("Introduzca el numero del dorsal (este campo es obligatorio): ");
        int dorsal = verificarDorsal();

        System.out.print("Introduzca el nombre de equipo (este campo es obligatorio): ");
        String nomEquipo = verificarNombreApellidoNomEquipo();

        System.out.print("Introduzca el ID del entrenador (este campo es obligatorio): ");
        int idEntrenador = verificarIdEntrenador();
        sc.nextLine();

        return new Jugadora(nombre, apellidos, posicion, nacionalidad, edad, dorsal, nomEquipo, idEntrenador);
    }

    /**
     * Este método se encarga de agregar una nueva jugadora en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar la jugadora a la base de datos.</p>
     */
    public void agregarJugadora() {
        Jugadora jugadora = preguntarDatosJugadora();
        try {
            jugadoraDAO.insertar(jugadora);
            LOGGER.info("Jugadora agregada correctamente con ID: {}", jugadora.getIdJugadora());
            System.out.println("Jugadora agregada con ID: " + jugadora.getIdJugadora());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar la jugadora con el ID: {}, {}", jugadora.getIdJugadora(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener una jugadora a partir de su ID.
     *
     * <p>Solicita al usuario el ID y obtiene los datos de la jugadora desde la base de datos.</p>
     *
     * @return un objeto {@link Jugadora} con sus correspondientes datos, o un valor null si no se encuentra una jugadora con el ID proporcionado.
     */
    public Jugadora obtenerJugadoraPorId() {
        int id = validarIdJugadora();
        try {
            Jugadora jugadora = jugadoraDAO.obtenerPorId(id);
            if (jugadora != null) {
                LOGGER.info("Se ha encontrado la jugadora correctamente con el ID: {}", id);
                System.out.println("Jugadora encontrada por ID: " + jugadora);
                return jugadora;
            } else {
                LOGGER.info("No se ha encontrado ninguna jugadora con el ID proporcionado: {}", id);
                System.out.println("No se encontro ninguna jugadora con ID: " + id);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar la jugadora con el ID: {}, {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todas las jugadoras registradas.
     *
     * <p>Recupera y muestra todas las jugadoras almacenadas en la base de datos.</p>
     *
     * @return una lista con todas las jugadoras, o devuelve un valor null si la lista está vacía.
     */
    public List<Jugadora> listarJugadora() {
        try {
            List<Jugadora> jugadoraList = jugadoraDAO.obtenerTodos();

            if (jugadoraList != null && !jugadoraList.isEmpty()) {
                System.out.println("---Lista de jugadoras---");
                for (Jugadora jugadora : jugadoraList) {
                    System.out.println(jugadora);
                }
                return jugadoraList;

            } else {
                System.out.println("No se encontraron jugadoras, la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de jugadoras está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todas las jugadoras con sus correspondientes entrenadores.
     *
     * <p>Recupera y muestra todas las jugadoras con sus entrenadores almacenadas en la base de datos.</p>
     *
     * @return una lista con todas las jugadoras con sus respectivos entrenadores, o devuelve un valor null si la lista está vacía.
     */
    public List<Jugadora> listarJugadorasConEntrenadores() {
        try {
            List<Jugadora> jugadoraList = jugadoraDAO.obtenerJugadoraConEntrenador();

            if (jugadoraList != null && !jugadoraList.isEmpty()) {
                System.out.println("---Lista de jugadoras con sus entrenadores---");
                for (Jugadora jugadora : jugadoraList) {
                    System.out.printf("ID: %d, Nombre: %s, ID Entrenador: %d, Nombre del Equipo: %s%n",
                            jugadora.getIdJugadora(),
                            jugadora.getNombre(),
                            jugadora.getIdEntrenador(),
                            jugadora.getNombreEquipo());
                }
                return jugadoraList;

            } else {
                System.out.println("No se encontraron jugadoras con entrenadores, la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de jugadoras con sus respectivos entrenadores está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de una jugadora existente.
     *
     * <p>Permite actualizar los datos de una jugadora. El usuario debe introducir el ID de la jugadora que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarJugadora() {
        int id = validarIdJugadora();
        System.out.println("Introduzca los nuevos datos de la jugadora: ");

        Jugadora jugadora = preguntarDatosJugadora();
        jugadora.setIdJugadora(id);
        try {
            jugadoraDAO.actualizar(jugadora);
            LOGGER.info("Se ha actualizado la jugadora correctamente con ID: {}", id);
            System.out.println("Jugadora actualizada: " + jugadora);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar la jugadora con ID: {}, {}", id, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar una jugadora de la base de datos.
     *
     * <p>Permite eliminar una jugadora de la base de datos mediante su ID. Se solicita al
     * usuario el ID de la jugadora y, si existe, se eliminará correctamente.</p>
     */
    public void eliminarJugadora() {
        int id = validarIdJugadora();
        try {
            jugadoraDAO.eliminar(id);
            LOGGER.info("Se ha eliminado la jugadora correctamente con ID: {}", id);
            System.out.println("Jugadora eliminada correctamente con ID: " + id);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar la jugadora con ID: {}, {}", id, e.getMessage());
        }
    }
}
