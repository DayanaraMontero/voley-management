package es.cheste.servicios;

import es.cheste.clases.Entrenador;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.EntrenadorDAOImpl;
import es.cheste.interfaces.EntrenadorDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase proporciona un conjunto de métodos para gestionar entrenadores en el sistema.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar entrenadores desde la base de datos.
 * Además, se realiza la creación de la tabla ENTRENADOR, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar los entrenadores de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link EntrenadorDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionEntrenador {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private EntrenadorDAO entrenadorDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el objeto EntrenadorDAO.
     *
     * <p>Esto permite que las operaciones sobre los entrenadores se realicen con la lógica definida en el DAO.</p>
     */
    public GestionEntrenador() {
        this.entrenadorDAO = new EntrenadorDAOImpl();
    }

    /**
     * Crea la tabla ENTRENADOR en la base de datos si aún no existe.
     *
     * <p>La tabla ENTRENADOR incluye los siguientes campos: ID_ENTRENADOR, NOMBRE, APELLIDOS, NACIONALIDAD y EXPERIENCIA.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS ENTRENADOR (" +
                "ID_ENTRENADOR SERIAL PRIMARY KEY, " +
                "NOMBRE VARCHAR(30) NOT NULL, " +
                "APELLIDOS VARCHAR(50) NOT NULL, " +
                "NACIONALIDAD VARCHAR(25), " +
                "EXPERIENCIA INT NOT NULL CHECK (EXPERIENCIA > 0));";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla ENTRENADOR.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla ENTRENADOR ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla ENTRENADOR: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            entrenadorDAO.insertar(new Entrenador("Mario", "Martinez", "Cubana", 2));
            entrenadorDAO.insertar(new Entrenador("Juan", "Lopez", "Español", 5));
            entrenadorDAO.insertar(new Entrenador("Rosa", "Rodriguez Ruiz", null, 8));
            entrenadorDAO.insertar(new Entrenador("Carmen", "Moreno", "Mexicana", 3));
            entrenadorDAO.insertar(new Entrenador("Roberto", "Fernandez", null, 6));

            LOGGER.info("Datos insertados correctamente en la tabla ENTRENADOR");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla ENTRENADOR: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar los entrenadores.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre los entrenadores.</p>
     */
    public void mostrarMenu() {
        int opcion;
        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarEntrenador();
                case 2 -> obtenerEntrenadorPorId();
                case 3 -> listarEntrenador();
                case 4 -> actualizarEntrenador();
                case 5 -> eliminarEntrenador();
                case 6 -> System.out.println("Saliendo de la tabla 'ENTRENADOR'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }
        } while (opcion != 6);
    }

    /**
     * Muestra las opciones del menú para gestionar entrenadores.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de los Entrenadores---");
        System.out.println("1. Agregar entrenador");
        System.out.println("2. Obtener entrenador por ID");
        System.out.println("3. Mostrar lista de entrenadores");
        System.out.println("4. Actualizar entrenador");
        System.out.println("5. Eliminar entrenador");
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
                LOGGER.error("Para seleccionar una opción del menu, se tiene que introducir un número entero mayor que 0: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido: ");
            }
        }
        return opcion;
    }

    /**
     * Solicita al usuario el ID del entrenador para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero mayor que 0.
     *
     * @return el ID del entrenador que sea correcto.
     */
    private int validarId() {
        int id = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {
            System.out.println("Introduzca el ID del entrenador: ");

            try {
                id = sc.nextInt();
                sc.nextLine();

                if (id > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El número de ID del entrenador tiene que ser mayor que 0: {}", id);
                    System.out.println("El número del ID tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar un entrenador por su ID, se tiene que introducir un número entero mayor que 0: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el ID del entrenador: ");
            }
        }
        return id;
    }

    /**
     * Este método se encarga de verificar los atributos, nombre o apellido.
     *
     * <p>Solicita al usuario que introduzca un nombre o apellido y verifica que cumpla las siguientes condiciones: </p>
     * <ul>
     *     <li>Cada palabra debe empezar con una letra mayúscula.</li>
     *     <li>Tiene que tener al menos tres caracteres.</li>
     *     <li>Solo se permiten letras y espacios, en caso de ser necesario.</li>
     * </ul>
     * <p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa el nombre o apellido introducido por el usuario.
     */
    private String verificarNombreApellido() {
        String campo;
        String patron = "([A-Z][a-zA-Z]*)+( [A-Z][a-zA-Z]*)*";

        do {
            campo = sc.nextLine();

            if (campo.length() < 3 || !campo.matches(patron)) {
                LOGGER.info("Cada palabra debe empezar por mayúscula, tener al menos tres caracteres y solo contener letras y en caso de ser necesario espacios.");
                System.out.println("Cada palabra debe empezar por mayúscula, tener al menos tres caracteres y solo contener letras y en caso de ser necesario espacios, recuerda que es un campo obligatorio, intentalo de nuevo: ");
            }

        } while (campo.length() < 3 || !campo.matches(patron));

        return campo;
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
                System.out.println("El campo 'nacionalidad' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios), recuerda que es un campo obligatorio, intentalo de nuevo: ");
            }

        } while (!nacionalidad.matches(patron));
        return nacionalidad;
    }

    /**
     * Este método se encarga de verificar que los años de experiencia sean válidos.
     *
     * <p>La experiencia debe ser un valor numérico y el número debe ser mayor que 0.
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.</p>
     *
     * @return un número válido que representa los años de experiencia introducidos por el usuario.
     */
    private int verificarExperiencia() {
        int experiencia = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("La experiencia no puede estar vacía.");
                    System.out.println("La experiencia del entrenador es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                experiencia = Integer.parseInt(entrada);

                if (experiencia > 0) {
                    esEntradaValida = Boolean.TRUE;
                } else {
                    LOGGER.info("La experiencia debe ser un número mayor que 0.");
                    System.out.println("La experiencia debe ser un número mayor que 0, intentalo de nuevo:");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("La entrada para el campo experiencia es incorrecto: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el atributo 'experiencia': ");
            }

        } while (!esEntradaValida);
        return experiencia;
    }

    /**
     * Solicita los datos necesarios para crear un nuevo entrenador.
     *
     * <p>Estos datos incluyen el nombre, apellidos, nacionalidad y los años de experiencia.</p>
     *
     * @return un objeto {@link Entrenador} con los datos introducidos por el usuario.
     */
    private Entrenador preguntarDatosEntrenador() {
        System.out.print("Introduzca el nombre (este campo es obligatorio): ");
        String nombre = verificarNombreApellido();

        System.out.print("Introduzca los apellidos (este campo es obligatorio): ");
        String apellidos = verificarNombreApellido();

        System.out.print("Introduzca la nacionalidad: ");
        String nacionalidad = verificarNacionalidad();

        System.out.print("Introduzca los años de experiencia (este campo es obligatorio): ");
        int experiencia = verificarExperiencia();

        return new Entrenador(nombre, apellidos, nacionalidad, experiencia);
    }

    /**
     * Este método se encarga de agregar un nuevo entrenador en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar el entrenador a la base de datos.</p>
     */
    public void agregarEntrenador() {
        Entrenador entrenador = preguntarDatosEntrenador();
        try {
            entrenadorDAO.insertar(entrenador);
            LOGGER.info("Entrenador agregado correctamente con ID: {}", entrenador.getIdEntrenador());
            System.out.println("Entrenador agregado con ID: " + entrenador.getIdEntrenador());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar el entrenador con el ID: {}, {}", entrenador.getIdEntrenador(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener un entrenador a partir de su ID.
     *
     * <p>Solicita al usuario el ID y obtiene los datos del entrenador desde la base de datos.</p>
     *
     * @return un objeto {@link Entrenador} con sus correspondientes datos, o un valor null si no se encuentra un entrenador con el ID proporcionado.
     */
    public Entrenador obtenerEntrenadorPorId() {
        int id = validarId();
        try {
            Entrenador entrenador = entrenadorDAO.obtenerPorId(id);
            if (entrenador != null) {
                LOGGER.info("Se ha encontrado el entrenador correctamente con ID: {}", id);
                System.out.println("Entrenador encontrado por ID: " + entrenador);
                return entrenador;
            } else {
                LOGGER.info("No se ha encontrado ningun entrenador con el ID proporcionado: {}", id);
                System.out.println("No se encontro entrenador con ID: " + id);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar el entrenador con el ID: {}, {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todos los entrenadores registrados.
     *
     * <p>Recupera y muestra todos los entrenadores almacenados en la base de datos.</p>
     *
     * @return una lista con todos los entrenadores, o devuelve un valor null si la lista está vacía.
     */
    public List<Entrenador> listarEntrenador() {
        try {
            List<Entrenador> entrenadorList = entrenadorDAO.obtenerTodos();

            if (entrenadorList != null && !entrenadorList.isEmpty()) {
                System.out.println("---Lista de entrenadores---");
                for (Entrenador entrenador : entrenadorList) {
                    System.out.println(entrenador);
                }
                return entrenadorList;

            } else {
                System.out.println("No se encontraron entrenadores, la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de entrenadores está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de un entrenador existente.
     *
     * <p>Permite actualizar los datos de un entrenador. El usuario debe introducir el ID del entrenador que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarEntrenador() {
        int id = validarId();
        System.out.println("Introduzca los nuevos datos del entrenador: ");
        Entrenador entrenador = preguntarDatosEntrenador();
        entrenador.setIdEntrenador(id);

        try {
            entrenadorDAO.actualizar(entrenador);
            LOGGER.info("Se ha actualizado el entrenador correctamente con ID: {}", id);
            System.out.println("Entrenador actualizado: " + entrenador);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar el entrenador con ID: {}, {}", id, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar un entrenador de la base de datos.
     *
     * <p>Permite eliminar un entrenador de la base de datos mediante su ID. Se solicita al
     * usuario el ID del entrenador y, si existe, se eliminará correctamente.</p>
     */
    public void eliminarEntrenador() {
        int id = validarId();
        try {
            entrenadorDAO.eliminar(id);
            LOGGER.info("Se ha eliminado el entrenador correctamente con ID: {}", id);
            System.out.println("Entrenador eliminado correctamente con ID: " + id);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar el entrenador con ID: {}, {}", id, e.getMessage());
        }
    }
}




