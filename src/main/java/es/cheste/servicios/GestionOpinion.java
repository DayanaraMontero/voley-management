package es.cheste.servicios;

import es.cheste.clases.Opinion;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.OpinionDAOImpl;
import es.cheste.interfaces.OpinionDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase proporciona un conjunto de métodos para gestionar opiniones en el sistema.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar opiniones desde la base de datos.
 * Además, se realiza la creación de la tabla OPINION, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar las opiniones de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link OpinionDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionOpinion {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private OpinionDAO opinionDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el objeto OpinionDAO.
     *
     * <p>Esto permite que las operaciones sobre las opiniones se realicen con la lógica definida en el DAO.</p>
     */
    public GestionOpinion() {
        this.opinionDAO = new OpinionDAOImpl();
    }

    /**
     * Crea la tabla OPINION en la base de datos si aún no existe.
     *
     * <p>La tabla OPINION incluye los siguientes campos: ID_OPINION, ID_PARTIDO, ID_JUGADORA, ID_USUARIO, PUNTUACION y COMENTARIO.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS OPINION (" +
                "ID_OPINION SERIAL PRIMARY KEY, " +
                "ID_PARTIDO INT, " +
                "FOREIGN KEY (ID_PARTIDO) REFERENCES PARTIDO (ID_PARTIDO), " +
                "ID_JUGADORA INT, " +
                "FOREIGN KEY (ID_JUGADORA) REFERENCES JUGADORA (ID_JUGADORA), " +
                "ID_USUARIO INT, " +
                "FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO (ID_USUARIO), " +
                "PUNTUACION INT NOT NULL CHECK(PUNTUACION >= 1 AND PUNTUACION <= 10), " +
                "COMENTARIO VARCHAR(100) NOT NULL);";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla OPINION.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla OPINION ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla OPINION: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            opinionDAO.insertar(new Opinion(3, 4, 2, 8, "Una de las mejores jugadoras que he visto"));
            opinionDAO.insertar(new Opinion(1, 3, 3, 7, "Su desarrollo durante el partido no ha estado mal, pero podria haber sido mejor"));
            opinionDAO.insertar(new Opinion(5, 1, 4, 3, "Uno de los peores partidos que la he visto jugar en mi vida"));
            opinionDAO.insertar(new Opinion(2, 2, 5, 10, "Bien merecido el primer lugar estuvieron impresionantes"));
            opinionDAO.insertar(new Opinion(4, 5, 1, 7, "Me alegro de que mi jugadora favorita ha ganado"));

            LOGGER.info("Datos insertados correctamente en la tabla OPINION");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla OPINION: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar las opiniones.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre las opiniones.</p>
     */
    public void mostrarMenu() {
        int opcion;

        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarOpinion();
                case 2 -> obtenerOpinionPorId();
                case 3 -> listarOpinion();
                case 4 -> listarOpinionUsuario();
                case 5 -> actualizarOpinion();
                case 6 -> eliminarOpinion();
                case 7 -> System.out.println("Saliendo de la tabla 'OPINION'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }

        } while (opcion != 7);
    }

    /**
     * Muestra las opciones del menú para gestionar opiniones.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de las Opiniones---");
        System.out.println("1. Agregar opinión");
        System.out.println("2. Obtener opinión por ID");
        System.out.println("3. Mostrar lista de opiniones");
        System.out.println("4. Mostrar lista de las opiniones de los usuarios respecto a sus jugadoras favoritas");
        System.out.println("5. Actualizar opinión");
        System.out.println("6. Eliminar opinión");
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
            System.out.print("Elige una opinión (1-7): ");
            try {
                opcion = sc.nextInt();
                sc.nextLine();

                if (opcion >= 1 && opcion <= 7) {
                    esOpcionValida = Boolean.TRUE;
                } else {
                    System.out.println("La opinión elegida es incorrecta, tiene que ser un número entre 1 y 7, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una opinión del menú, se tiene que introducir un valor numérico entero positivo: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido: ");
            }
        }
        return opcion;
    }

    /**
     * Solicita al usuario el ID de la opinión para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero positivo.
     *
     * @return el ID de la opinión que sea correcto.
     */
    private int validarIdOpinion() {
        int id = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {
            System.out.println("Introduzca el ID de la opinion: ");

            try {
                id = sc.nextInt();
                sc.nextLine();

                if (id > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El número de ID de la opinión tiene que ser mayor que 0: {}", id);
                    System.out.println("El número del ID tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una opinión por su ID, se tiene que introducir un número entero mayor que 0: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el ID de la opinión: ");
            }
        }
        return id;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para los campos ID de partido, ID de jugadora,
     * e ID de usuario. Si el usuario decide dejar alguno de los campos vacíos también se considera válido.
     *
     * <p>Devuelve un 0 si el usuario deja el campo vacío, lo que indica que los identificadores no se han proporcionado.</p>
     *
     * <p>Si el valor introducido no es un número o es menor que cero, se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero mayor que cero introducido por el usuario.
     */
    private int verificarIds() {
        int id = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            String entrada = sc.nextLine();

            if (entrada.isEmpty()) {
                return id;
            }

            try {
                id = Integer.parseInt(entrada);

                if (id > 0) {
                    esEntradaValida = Boolean.TRUE;
                } else {
                    LOGGER.info("Debes introducir un número mayor que 0 para el ID.");
                    System.out.println("Por favor, introduzca un número mayor que 0 para el ID: ");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un número entero mayor que 0 para el ID .");
                System.out.println("Por favor, introduzca un número entero mayor que 0 para el ID: ");
                sc.nextLine();
            }

        } while (!esEntradaValida);
        return id;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para la puntuación que asigne el
     * usuario sobre la jugadora en su partido correspondiente.
     *
     * <p>Si el valor introducido no es un número o no está dentro del rango para las puntuaciones (1-10), se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero entre 1 y 10 introducidos por el usuario.
     */
    private int verificarPuntuacion() {
        int puntuacion = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("La puntuación no puede estar vacía.");
                    System.out.println("La puntuación es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                puntuacion = Integer.parseInt(entrada);

                if (puntuacion < 1 || puntuacion > 10) {
                    LOGGER.info("La puntuación debe ser un número entre 1 y 10.");
                    System.out.println("Por favor, introduzca un número entre 1 y 10: ");
                } else {
                    esEntradaValida = Boolean.TRUE;
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un valor numérico entero para la puntuación.");
                System.out.println("Por favor, introduzca un número entero para la puntuación entre 1 y 10: ");
                sc.nextLine();
            }

        } while (!esEntradaValida);
        return puntuacion;
    }

    /**
     * Este método se encarga de verificar que el comentario sea válido.
     *
     * <p>El comentario debe ser una cadena de texto que contenga solo letras, tanto en mayúsculas como en minúsculas,
     * puede incluir espacios en el caso de que haya más de una palabra.</p>
     *
     * <p>Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.</p>
     *
     * @return una cadena válida que representa el comentario introducido por el usuario.
     */
    private String verificarComentario() {
        String comentario;
        String patron = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$";

        do {
            comentario = sc.nextLine();

            if (comentario.isEmpty()) {
                return comentario;
            }

            if (!comentario.matches(patron)) {
                LOGGER.info("El campo 'comentario' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios).");
                System.out.println("El campo 'comentario' debe ser una cadena de texto válida (solo letras y en caso de ser necesario espacios), recuerda que es obligatorio, intentalo de nuevo: ");
            }

        } while (!comentario.matches(patron));
        return comentario;
    }

    /**
     * Solicita los datos necesarios para crear una nueva opinión.
     *
     * <p>Estos datos incluyen el identificador del partido, identificador de la jugadora, identificador del usuario, puntuación y el comentario.</p>
     *
     * @return un objeto {@link Opinion} con los datos introducidos por el usuario.
     */
    private Opinion preguntarDatosOpinion() {
        System.out.print("Introduzca el ID del partido: ");
        int idPartido = verificarIds();

        System.out.print("Introduzca el ID de la jugadora: ");
        int idJugadora = verificarIds();

        System.out.print("Introduzca el ID del usuario: ");
        int idUsuario = verificarIds();

        System.out.print("Introduzca la puntuacion (este campo es obligatorio): ");
        int puntuacion = verificarPuntuacion();

        System.out.print("Introduzca el comentario (este campo es obligatorio): ");
        String comentario = verificarComentario();
        sc.nextLine();

        return new Opinion(idPartido, idJugadora, idUsuario, puntuacion, comentario);
    }

    /**
     * Este método se encarga de agregar una nueva opinión en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar la opinión a la base de datos.</p>
     */
    public void agregarOpinion() {
        Opinion opinion = preguntarDatosOpinion();
        try {
            opinionDAO.insertar(opinion);
            LOGGER.info("Opinión agregada correctamente con ID: {}", opinion.getIdOpinion());
            System.out.println("Opinión agregada con ID: " + opinion.getIdOpinion());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar la opinión con el ID: {}, {}", opinion.getIdOpinion(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener una opinión a partir de su ID.
     *
     * <p>Solicita al usuario el ID y obtiene los datos de la opinión desde la base de datos.</p>
     *
     * @return un objeto {@link Opinion} con sus correspondientes datos, o un valor null si no se encuentra una opiniómn con el ID proporcionado.
     */
    public Opinion obtenerOpinionPorId() {
        int id = validarIdOpinion();
        try {
            Opinion opinion = opinionDAO.obtenerPorId(id);
            if (opinion != null) {
                LOGGER.info("Se ha encontrado la opinión correctamente con ID: {}", id);
                System.out.println("Opinión encontrada por ID: " + opinion);
                return opinion;
            } else {
                LOGGER.info("No se ha encontrado ninguna opinión con el ID proporcionado: {}", id);
                System.out.println("No se encontro ninguna opinión con ID: " + id);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar la opinión con el ID: {}, {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todas las opiniones registradas.
     *
     * <p>Recupera y muestra todas las opiniones almacenadas en la base de datos.</p>
     *
     * @return una lista con todas las opiniones, o devuelve un valor null si la lista está vacía.
     */
    public List<Opinion> listarOpinion() {
        try {
            List<Opinion> opinionList = opinionDAO.obtenerTodos();

            if (opinionList != null && !opinionList.isEmpty()) {
                System.out.println("---Lista de opiniones---");
                for (Opinion opinion : opinionList) {
                    System.out.println(opinion);
                }
                return opinionList;

            } else {
                System.out.println("No se encontraron opiniones, la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de opiniones está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todas las opiniones de los usuarios con respecto a sus jugadoras favoritas.
     *
     * <p>Recupera y muestra todas las opiniones de los usuarios almacenados en la base de datos.</p>
     *
     * @return una lista con todas las opiniones de los usuarios, o devuelve un valor null si la lista está vacía.
     */
    public List<Opinion> listarOpinionUsuario() {
        try {
            List<Opinion> opinionList = opinionDAO.obtenerOpiniones();

            if (opinionList != null && !opinionList.isEmpty()) {
                System.out.println("---Lista de las opiniones de los usuarios respecto a sus jugadoras favoritas---");
                for (Opinion opinion : opinionList) {
                    System.out.printf("ID Jugadora: %d, ID Usuario: %d, Puntuación: %d, Comentario: %s%n",
                            opinion.getIdJugadora(),
                            opinion.getIdUsuario(),
                            opinion.getPuntuacion(),
                            opinion.getComentario());
                }
                return opinionList;

            } else {
                System.out.println("No se encontraron las opiniones de los usuarios sobre sus jugadoras favoritas.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de opiniones de los usuarios sobre sus jugadoras favoritas está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de una opinión existente.
     *
     * <p>Permite actualizar los datos de una opinión. El usuario debe introducir el ID de la opinión que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarOpinion() {
        int id = validarIdOpinion();
        System.out.println("Introduzca los nuevos datos de la opinión: ");

        Opinion opinion = preguntarDatosOpinion();
        opinion.setIdOpinion(id);
        try {
            opinionDAO.actualizar(opinion);
            LOGGER.info("Se ha actualizado la opinión correctamente con ID: {}", id);
            System.out.println("Opinión actualizada: " + opinion);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar la opinión con ID: {}, {}", id, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar una opinión de la base de datos.
     *
     * <p>Permite eliminar una opinión de la base de datos mediante su ID. Se solicita al
     * usuario el ID de la opinión y, si existe, se eliminará correctamente.</p>
     */
    public void eliminarOpinion() {
        int id = validarIdOpinion();
        try {
            opinionDAO.eliminar(id);
            LOGGER.info("Se ha eliminado la opinión correctamente con ID: {}", id);
            System.out.println("Opinión eliminada correctamente con ID: " + id);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar la opinión con ID: {}, {}", id, e.getMessage());
        }
    }
}
