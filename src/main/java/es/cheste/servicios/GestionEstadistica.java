package es.cheste.servicios;

import es.cheste.clases.Estadistica;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.EstadisticaDAOImpl;
import es.cheste.interfaces.EstadisticaDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase proporciona un conjunto de métodos para gestionar estadísticas en el sistema.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar estadísticas desde la base de datos.
 * Además, se realiza la creación de la tabla ESTADISTICA, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar las estadísticas de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link EstadisticaDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionEstadistica {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private EstadisticaDAO estadisticaDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el objeto EstadisticaDAO.
     *
     * <p>Esto permite que las operaciones sobre las estadísticas se realicen con la lógica definida en el DAO.</p>
     */
    public GestionEstadistica() {
        this.estadisticaDAO = new EstadisticaDAOImpl();
    }

    /**
     * Crea la tabla ESTADISTICA en la base de datos si aún no existe.
     *
     * <p>La tabla ESTADISTICA incluye los siguientes campos: ID_ESTADISTICA, ID_JUGADORA, ID_PARTIDO, ATAQUES, SAQUES, BLOQUEOS, DEFENSAS y ERRORES.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS ESTADISTICA (" +
                "ID_ESTADISTICA SERIAL PRIMARY KEY, " +
                "ID_JUGADORA INT, " +
                "FOREIGN KEY (ID_JUGADORA) REFERENCES JUGADORA (ID_JUGADORA), " +
                "ID_PARTIDO INT, " +
                "FOREIGN KEY (ID_PARTIDO) REFERENCES PARTIDO (ID_PARTIDO), " +
                "ATAQUES INT NOT NULL CHECK(ATAQUES >= 0), " +
                "SAQUES INT NOT NULL CHECK(SAQUES >= 0), " +
                "BLOQUEOS INT NOT NULL CHECK(BLOQUEOS >= 0), " +
                "DEFENSAS INT NOT NULL CHECK(DEFENSAS >= 0), " +
                "ERRORES INT NOT NULL CHECK(ERRORES >= 0));";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla ESTADISTICA.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla ESTADISTICA ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla ESTADISTICA: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            estadisticaDAO.insertar(new Estadistica(2, 3, 7, 2, 5, 4, 2));
            estadisticaDAO.insertar(new Estadistica(4, 2, 14, 5, 3, 2, 3));
            estadisticaDAO.insertar(new Estadistica(1, 4, 9, 8, 12, 1, 1));
            estadisticaDAO.insertar(new Estadistica(3, 5, 4, 10, 4, 9, 4));
            estadisticaDAO.insertar(new Estadistica(5, 1, 6, 4, 3, 9, 2));

            LOGGER.info("Datos insertados correctamente en la tabla ESTADISTICA");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla ESTADISTICA: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar las estadísticas.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre las estadísticas.</p>
     */
    public void mostrarMenu() {
        int opcion;

        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarEstadistica();
                case 2 -> obtenerEstadisticaPorId();
                case 3 -> listarEstadistica();
                case 4 -> actualizarEstadistica();
                case 5 -> eliminarEstadistica();
                case 6 -> calcularRankingJugadoras();
                case 7 -> System.out.println("Saliendo de la tabla 'ESTADISTICA'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }

        } while (opcion != 7);
    }

    /**
     * Muestra las opciones del menú para gestionar estadísticas.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de las Estadísticas---");
        System.out.println("1. Agregar estadística");
        System.out.println("2. Obtener estadística por ID");
        System.out.println("3. Mostrar lista de estadísticas");
        System.out.println("4. Actualizar estadística");
        System.out.println("5. Eliminar estadística");
        System.out.println("6. Calcular el Ranking de las Jugadoras");
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
                LOGGER.error("Para seleccionar una opción del menu, se tiene que introducir un número entero: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido: ");
            }
        }
        return opcion;
    }

    /**
     * Solicita al usuario el ID de la estadística para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero mayor que 0.
     *
     * @return el ID de la estadística que sea correcto.
     */
    private int validarIdEstadistica() {
        int id = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {
            System.out.println("Introduzca el ID de la estadística: ");

            try {
                id = sc.nextInt();
                sc.nextLine();

                if (id > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El número de ID de la estadística no puede ser menor que 0: {}", id);
                    System.out.println("El número del ID tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una estadística por su ID, se tiene que introducir un valor numérico entero positivo: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el ID de la estadística: ");
            }
        }
        return id;
    }

    /**
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para los campos ID de jugadora e ID de partido.
     *
     * <p>Si el valor introducido no es un número o es menor a cero, se le pedirá al usuario que introduzca el
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
     * Este método se encarga de solicitar un número entero mayor o igual que cero y lo valida para las estadísticas
     * relacionadas con el rendimiento de una jugadora en un partido, como ataques, saques, bloqueos,
     * defensas, errores.
     *
     * <p>Si el valor introducido no es un número, se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero mayor o igual que cero introducido por el usuario.
     */
    private int verificarRendimiento() {
        int numero = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("Las métricas de rendimiento no pueden estar vacías.");
                    System.out.println("Las métricas de rendimiento es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                numero = Integer.parseInt(entrada);

                if (numero >= 0) {
                    esEntradaValida = Boolean.TRUE;
                } else {
                    LOGGER.info("Debes introducir un número entero mayor o igual que 0.");
                    System.out.println("Por favor, introduzca número mayor o igual que 0: ");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un valor numérico entero mayor o igual que 0.");
                System.out.println("Por favor, introduzca un número entero mayor o igual que 0: ");
            }

        } while (!esEntradaValida);
        return numero;
    }

    /**
     * Solicita los datos necesarios para crear una nueva estadística.
     *
     * <p>Estos datos incluyen el ID de la jugadora, ID del partido, y las estadísticas de rendimiento de la
     * jugadora como ataques, saques, bloqueos, defensas y errores.</p>
     *
     * @return un objeto {@link Estadistica} con los datos introducidos por el usuario.
     */
    private Estadistica preguntarDatosEstadistica() {
        System.out.print("Introduzca el ID de la jugadora: ");
        int idJugadora = verificarIds();

        System.out.print("Introduzca el ID del partido: ");
        int idPartido = verificarIds();

        System.out.print("Introduzca el número de ataques que realizó la jugadora (este campo es obligatorio): ");
        int ataques = verificarRendimiento();

        System.out.print("Introduzca el número de saques que realizó la jugadora (este campo es obligatorio): ");
        int saques = verificarRendimiento();

        System.out.print("Introduzca el número de bloqueos que realizó la jugadora (este campo es obligatorio): ");
        int bloqueos = verificarRendimiento();

        System.out.print("Introduzca el número de defensas que realizó la jugadora (este campo es obligatorio): ");
        int defensas = verificarRendimiento();

        System.out.print("Introduzca el número de errores que realizó la jugadora (este campo es obligatorio): ");
        int errores = verificarRendimiento();

        return new Estadistica(idJugadora, idPartido, ataques, saques, bloqueos, defensas, errores);
    }

    /**
     * Este método se encarga de agregar una nueva estadística en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar la estadística a la base de datos.</p>
     */
    public void agregarEstadistica() {
        Estadistica estadistica = preguntarDatosEstadistica();
        try {
            estadisticaDAO.insertar(estadistica);
            LOGGER.info("Estadistica agregada correctamente con ID: {}", estadistica.getIdEstadistica());
            System.out.println("Estadistica agregada con ID: " + estadistica.getIdEstadistica());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar la estadistica con el ID: {}, {}", estadistica.getIdEstadistica(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener una estadística a partir de su ID.
     *
     * <p>Solicita al usuario el ID y obtiene los datos de la estadística desde la base de datos.</p>
     *
     * @return un objeto {@link Estadistica} con sus correspondientes datos, o un valor null si no se encuentra una estadística con el ID proporcionado.
     */
    public Estadistica obtenerEstadisticaPorId() {
        int id = validarIdEstadistica();
        try {
            Estadistica estadistica = estadisticaDAO.obtenerPorId(id);
            if (estadistica != null) {
                LOGGER.info("Se ha encontrado la estadistica correctamente con ID: {}", id);
                System.out.println("Estadistica encontrada por ID: " + estadistica);
                return estadistica;
            } else {
                LOGGER.info("No se ha encontrado ninguna estadistica con el ID proporcionado: {}", id);
                System.out.println("No se encontro ninguna estadistica con el ID: " + id);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar la estadistica con el ID: {}, {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todas las estadísticas registradas.
     *
     * <p>Recupera y muestra todas las estadísticas almacenadas en la base de datos.</p>
     *
     * @return una lista con todas las estadísticas, o devuelve un valor null si la lista está vacía.
     */
    public List<Estadistica> listarEstadistica() {
        try {
            List<Estadistica> estadisticaList = estadisticaDAO.obtenerTodos();

            if (estadisticaList != null && !estadisticaList.isEmpty()) {
                System.out.println("---Lista de estadísticas---");
                for (Estadistica estadistica : estadisticaList) {
                    System.out.println(estadistica);
                }
                return estadisticaList;

            } else {
                System.out.println("No se encontraron estadísticas, la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de estadísticas está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de una estadística existente.
     *
     * <p>Permite actualizar los datos de una estadística. El usuario debe introducir el ID de la estadística que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarEstadistica() {
        int id = validarIdEstadistica();
        System.out.println("Introduzca los nuevos datos de la estadística: ");

        Estadistica estadistica = preguntarDatosEstadistica();
        estadistica.setIdEstadistica(id);
        try {
            estadisticaDAO.actualizar(estadistica);
            LOGGER.info("Se ha actualizado la estadística correctamente con ID: {}", id);
            System.out.println("Estadística actualizada: " + estadistica);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar la estadística con ID: {}, {}", id, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar una estadística de la base de datos.
     *
     * <p>Permite eliminar una estadística de la base de datos mediante su ID. Se solicita al
     * usuario el ID de la estadística y, si existe, se eliminará correctamente.</p>
     */
    public void eliminarEstadistica() {
        int id = validarIdEstadistica();
        try {
            estadisticaDAO.eliminar(id);
            LOGGER.info("Se ha eliminado la estadística correctamente con ID: {}", id);
            System.out.println("Estadística eliminada correctamente con ID: " + id);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar la estadística con ID: {}, {}", id, e.getMessage());
        }
    }

    /**
     * Este método se encarga de ejecutar el procedimiento almacenado en la base de datos para calcular y mostrar el ranking de jugadoras.
     *
     * <p>Si el procedimiento encuentra resultados, se imprimen en consola, mostrando la información de cada jugadora incluyendo su
     * ID, nombre, apellidos, puntos y su posición en el ranking. Si no se encuentran resultados, se muestra un mensaje informando de que no hay
     * datos disponibles en el ranking.</p>
     */
    public void calcularRankingJugadoras() {
        ConexionBD conexionBD = new ConexionBD();
        String procedimiento = "SELECT * FROM calcularRankingJugadoras()";

        try (Connection connection = conexionBD.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(procedimiento)) {

            if (!rs.isBeforeFirst()) {
                LOGGER.info("El procedimiento almacenado no tiene ningún resultado.");
                System.out.println("No hay datos disponibles en el Ranking.");
                return;
            }

            System.out.println("---Ranking de Jugadoras---");
            System.out.printf("%-10s %-20s %-20s %-10s %-10s%n", "ID", "Nombre", "Apellidos", "Puntos", "Rank");

            while (rs.next()) {
                int id = rs.getInt("ID_JUGADORA");
                String nombre = rs.getString("NOMBRE");
                String apellidos = rs.getString("APELLIDOS");
                int puntos = rs.getInt("PUNTOS");
                int rank = rs.getInt("RANK");

                System.out.printf("%-10d %-20s %-20s %-10d %-10d%n", id, nombre, apellidos, puntos, rank);
            }

        } catch (SQLException e) {
            LOGGER.error("Error ejecutando el procedimiento almacenado: {}", e.getMessage());
        }
    }
}
