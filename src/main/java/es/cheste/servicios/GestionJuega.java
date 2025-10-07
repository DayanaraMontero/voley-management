package es.cheste.servicios;

import es.cheste.clases.Juega;
import es.cheste.conexion.ConexionBD;
import es.cheste.enums.EstadoJugadora;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.JuegaDAOImpl;
import es.cheste.interfaces.JuegaDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase proporciona un conjunto de métodos para gestionar la participación de las jugadoras en los partidos.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar participaciones desde la base de datos.
 * Además, se realiza la creación de la tabla JUEGA, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar las participaciones de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link JuegaDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionJuega {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private JuegaDAO juegaDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el objeto JuegaDAO.
     *
     * <p>Esto permite que las operaciones sobre las participaciones de las jugadoras en los partidos, se realicen con la lógica definida en el DAO.</p>
     */
    public GestionJuega() {
        this.juegaDAO = new JuegaDAOImpl();
    }

    /**
     * Crea la tabla JUEGA en la base de datos si aún no existe.
     *
     * <p>La tabla JUEGA incluye los siguientes campos: ID_JUGADORA, ID_PARTIDO, MINUTO_ENTRADA, MINUTO_SALIDA y ESTADO.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS JUEGA (" +
                "ID_JUGADORA INT, " +
                "ID_PARTIDO INT, " +
                "MINUTO_ENTRADA INT NOT NULL CHECK(MINUTO_ENTRADA >= 0), " +
                "MINUTO_SALIDA INT NOT NULL CHECK(MINUTO_SALIDA >= 0), " +
                "ESTADO VARCHAR(20) NOT NULL CHECK(UPPER(ESTADO) IN ('ACTIVA', 'SUSTITUIDA', 'LESIONADA', 'EXPULSADA', 'TITULAR', 'RESERVA', 'INACTIVA')), " +
                "PRIMARY KEY (ID_JUGADORA, ID_PARTIDO), " +
                "FOREIGN KEY (ID_JUGADORA) REFERENCES JUGADORA (ID_JUGADORA), " +
                "FOREIGN KEY (ID_PARTIDO) REFERENCES PARTIDO (ID_PARTIDO));";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla JUEGA.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla JUEGA ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla JUEGA: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            juegaDAO.insertar(new Juega(3, 1, 15, 32, "ACTIVA"));
            juegaDAO.insertar(new Juega(1, 4, 1, 145, "TITULAR"));
            juegaDAO.insertar(new Juega(2, 5, 35, 40, "SUSTITUIDA"));
            juegaDAO.insertar(new Juega(4, 2, 10, 25, "EXPULSADA"));
            juegaDAO.insertar(new Juega(5, 3, 2, 48, "RESERVA"));

            LOGGER.info("Datos insertados correctamente en la tabla JUEGA");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla JUEGA: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar las participaciones.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre las participaciones.</p>
     */
    public void mostrarMenu() {
        int opcion;

        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarJuega();
                case 2 -> obtenerJuegaPorId();
                case 3 -> listarJuega();
                case 4 -> actualizarJuega();
                case 5 -> eliminarJuega();
                case 6 -> System.out.println("Saliendo de la tabla 'JUEGA'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }

        } while (opcion != 6);
    }

    /**
     * Muestra las opciones del menú para gestionar participaciones.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de la participación de la jugadora en los partidos---");
        System.out.println("1. Agregar participación de la jugadora en el partido");
        System.out.println("2. Obtener participación por los identificadores de Jugadora y Partido");
        System.out.println("3. Mostrar lista de participaciones de las jugadoras en los partidos");
        System.out.println("4. Actualizar participación de la jugadora en el partido");
        System.out.println("5. Eliminar participación de la jugadora en el partido");
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
            System.out.print("Elige una opcion (1-6): ");
            try {
                opcion = sc.nextInt();
                sc.nextLine();

                if (opcion >= 1 && opcion <= 6) {
                    esOpcionValida = Boolean.TRUE;
                } else {
                    System.out.println("La opción elegida es incorrecta, tiene que ser un número entre 1 y 6, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una opción del menu, se tiene que introducir un número entero positivo: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido: ");
            }
        }
        return opcion;
    }

    /**
     * Solicita al usuario el ID de la jugadora o del partido para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero mayor que 0.
     *
     * @return el ID de la jugadora o del partido que sea correcto.
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
     * Este método se encarga de solicitar un número entero mayor que cero y lo valida para los campos
     * minuto de entrada y minuto de salida.
     *
     * <p>Si el valor introducido no es un número, se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero mayor o igual que cero introducido por el usuario.
     */
    private int verificarMinutos() {
        int numero = 0;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            try {
                String entrada = sc.nextLine();

                if (entrada.isEmpty()) {
                    LOGGER.error("Los minutos no pueden estar vacías.");
                    System.out.println("Los minutos es un campo obligatorio, intentalo de nuevo: ");
                    continue;
                }

                numero = Integer.parseInt(entrada);

                if (numero >= 0) {
                    esEntradaValida = Boolean.TRUE;
                } else {
                    LOGGER.info("Debes introducir un número entero mayor o igual que 0.");
                    System.out.println("Por favor, introduzca un número mayor o igual que 0: ");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un número entero mayor o igual que 0.");
                System.out.println("Por favor, introduzca un número entero mayor o igual que 0: ");
                sc.nextLine();
            }

        } while (!esEntradaValida);
        return numero;
    }

    /**
     * Este método solicita al usuario que introduzca un estado y lo verifica con los valores definidos en el enum
     * {@link es.cheste.enums.EstadoJugadora}. Si el estado no es válido se muestra un mensaje de error y se
     * solicita nuevamente hasta que el usuario introduzca un estado válido.
     *
     * @return una cadena válida que representa el estado de la jugadora introducida por el usuario, aunque también puede estar vacío.
     */
    private String verificarEstado() {
        String estado;
        boolean esValido = Boolean.FALSE;

        do {
            estado = sc.nextLine().toUpperCase();

            try {
                EstadoJugadora.valueOf(estado);
                esValido = Boolean.TRUE;

            } catch (IllegalArgumentException e) {
                LOGGER.info("El estado no es válido. Los valores permitidos son: ACTIVA, SUSTITUIDA, LESIONADA, EXPULSADA, TITULAR, RESERVA e INACTIVA");
                System.out.println("El estado no es válido. Los valores permitidos son: ACTIVA, SUSTITUIDA, LESIONADA, EXPULSADA, TITULAR, RESERVA e INACTIVA, recuerda que es un campo obligatorio, intentalo de nuevo: ");
            }

        } while (!esValido);
        return estado;
    }

    /**
     * Solicita los datos necesarios para crear una nueva participación.
     *
     * <p>Estos datos incluyen el minuto de entrada, minuto de salida y estado.</p>
     *
     * @return un objeto {@link Juega} con los datos introducidos por el usuario.
     */
    private Juega preguntarDatosJuega() {
        System.out.print("Introduzca el ID de la jugadora (este campo es obligatorio): ");
        int idJugadora = validarId("Jugadora");

        System.out.print("Introduzca el ID del partido (este campo es obligatorio): ");
        int idPartido = validarId("Partido");

        System.out.print("Introduzca el minuto de entrada de la jugadora al partido (este campo es obligatorio): ");
        int minEntrada = verificarMinutos();

        System.out.print("Introduzca el minuto de salida de la jugadora al partido (este campo es obligatorio): ");
        int minSalida = verificarMinutos();

        System.out.print("Introduzca el estado de la jugadora (este campo es obligatorio): ");
        String estado = verificarEstado();
        sc.nextLine();

        return new Juega(idJugadora, idPartido, minEntrada, minSalida, estado);
    }

    /**
     * Este método se encarga de agregar una nueva participación en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar la participación a la base de datos.</p>
     */
    public void agregarJuega() {
        Juega juega = preguntarDatosJuega();
        try {
            juegaDAO.insertar(juega);
            LOGGER.info("La participación de la jugadora con ID + {}, en el partido con el ID: {} ha sido agregado correctamente.", juega.getIdJugadora(), juega.getIdPartido());
            System.out.println("Jugadora agregado con ID: " + juega.getIdJugadora() + ", en el partido con el ID: " + juega.getIdPartido());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar la jugadora con el ID: {}, en el partido con el ID: {}, {}", juega.getIdJugadora(), juega.getIdPartido(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener una participación a partir de su ID.
     *
     * <p>Solicita al usuario el ID y obtiene los datos de la participación desde la base de datos.</p>
     *
     * @return un objeto {@link Juega} con sus correspondientes datos, o un valor null si no se encuentra una participación con los ID proporcionados.
     */
    public Juega obtenerJuegaPorId() {
        int idJugadora = validarId("Jugadora");
        int idPartido = validarId("Partido");
        try {
            Juega juega = juegaDAO.obtenerPorId(idJugadora, idPartido);
            if (juega != null) {
                LOGGER.info("Se ha encontrado la jugadora con el ID: {}, en el partido con el ID: {}", idJugadora, idPartido);
                System.out.println("Jugadora encontrada con ID: " + idJugadora + ", en el partido con el ID: " + idPartido);
                return juega;
            } else {
                LOGGER.info("No se ha encontrado ninguna jugadora con el ID proporcionado: {}, en el partido con el ID: {}", idJugadora, idPartido);
                System.out.println("No se encontro jugadora con ID: " + idJugadora + ", en el partido con ID: " + idPartido);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar la jugadora con el ID: {}, en el partido con el ID: {}, {}", idJugadora, idPartido, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todas las participaciones registradas.
     *
     * <p>Recupera y muestra todas las participaciones almacenados en la base de datos.</p>
     *
     * @return una lista con todas las participaciones, o devuelve un valor null si la lista está vacía.
     */
    public List<Juega> listarJuega() {
        try {
            List<Juega> juegaList = juegaDAO.obtenerTodos();

            if (juegaList != null && !juegaList.isEmpty()) {
                System.out.println("---Lista de las participaciones de las jugadores en los partidos---");
                for (Juega juega : juegaList) {
                    System.out.println(juega);
                }
                return juegaList;

            } else {
                System.out.println("No se encontraron las participaciones de las jugadoras en los partidos, la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de participaciones de las jugadoras en los partidos está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de una participación existente.
     *
     * <p>Permite actualizar los datos de una participación. El usuario debe introducir los IDs de la jugadora y el partido que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarJuega() {
        int idJugadora = validarId("Jugadora");
        int idPartido = validarId("Partido");
        System.out.println("Introduzca los nuevos datos de la participación de la jugadora en el partido: ");

        Juega juega = preguntarDatosJuega();
        juega.setIdJugadora(idJugadora);
        juega.setIdPartido(idPartido);
        try {
            juegaDAO.actualizar(juega);
            LOGGER.info("Se ha actualizado la participación de la jugadora correctamente con ID: {}, en el partido con ID: {}", idJugadora, idPartido);
            System.out.println("Jugadora actualizada en el partido correspondiente: " + juega);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar la jugadora con ID: {}, en el partido con ID: {}, {}", idJugadora, idPartido, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar una participación de la base de datos.
     *
     * <p>Permite eliminar una participación de la base de datos mediante sus IDs. Se solicita al
     * usuario los IDs de la jugadora y el partido y, si existen, se eliminará correctamente.</p>
     */
    public void eliminarJuega() {
        int idJugadora = validarId("Jugadora");
        int idPartido = validarId("Partido");
        try {
            juegaDAO.eliminar(idJugadora, idPartido);
            LOGGER.info("Se ha eliminado la jugadora correctamente con ID: {}, en el partido con ID: {}", idJugadora, idPartido);
            System.out.println("Jugadora eliminada correctamente con ID: " + idJugadora + ", en el partido con ID: " + idPartido);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar la jugadora con ID: {}, en el partido con ID: {}, {}", idJugadora, idPartido, e.getMessage());
        }
    }
}
