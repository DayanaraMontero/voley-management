package es.cheste.servicios;

import es.cheste.clases.Usuario;
import es.cheste.conexion.ConexionBD;
import es.cheste.excepcion.DAOException;
import es.cheste.impl.UsuarioDAOImpl;
import es.cheste.interfaces.UsuarioDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase proporciona un conjunto de métodos para gestionar usuarios en el sistema.
 * Permite realizar operaciones como agregar, obtener, listar, actualizar y eliminar usuarios desde la base de datos.
 * Además, se realiza la creación de la tabla USUARIO, en caso de que no exista en la base de datos.
 *
 * <p>También incluye un menú interactivo para que el usuario pueda gestionar los usuarios de manera fácil y práctica.</p>
 *
 * <p>Utiliza la interfaz {@link UsuarioDAO} que permite realizar las operaciones CRUD (crear, leer, actualizar y eliminar) en la base de datos.</p>
 */
public class GestionUsuario {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Scanner sc = new Scanner(System.in);
    private List<String> correosList;
    private UsuarioDAO usuarioDAO;

    /**
     * Constructor de la clase. Se encarga de inicializar el ArrayList y el objeto UsuarioDAO.
     *
     * <p>Esto permite que las operaciones sobre los usuarios se realicen con la lógica definida en el DAO.</p>
     */
    public GestionUsuario() {
        correosList = new ArrayList<>();
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Crea la tabla USUARIO en la base de datos si aún no existe.
     *
     * <p>La tabla USUARIO incluye los siguientes campos: ID_USUARIO, NOMBRE, EMAIL, CONTRASENYA y ID_JUGADORA.</p>
     *
     * @param conexion la conexión activa a la base de datos.
     * @return devuelve un true si la tabla se crea correctamente, en caso contrario, devuelve un false si ocurre un error durante la creación.
     */
    public boolean crearTabla(ConexionBD conexion) {
        String consulta = "CREATE TABLE IF NOT EXISTS USUARIO (" +
                "ID_USUARIO SERIAL PRIMARY KEY, " +
                "NOMBRE VARCHAR(30) NOT NULL, " +
                "APELLIDOS VARCHAR(50) NOT NULL, " +
                "EMAIL VARCHAR(50) UNIQUE, " +
                "CONTRASENYA VARCHAR(20) NOT NULL, " +
                "ID_JUGADORA INT, " +
                "FOREIGN KEY (ID_JUGADORA) REFERENCES JUGADORA (ID_JUGADORA));";

        if (conexion == null) {
            LOGGER.error("No hay conexión con base de datos, no se puede ejecutar la consulta para crear la tabla USUARIO.");
            return false;
        }

        try (Statement stmt = conexion.getConnection().createStatement()) {
            stmt.execute(consulta);
            LOGGER.info("La tabla USUARIO ha sido creada correctamente.");
            return true;

        } catch (SQLException e) {
            LOGGER.error("No se ha podido crear la tabla USUARIO: {}", e.getMessage());
            return false;
        }
    }

    public void insertarDatosIniciales() {
        try {
            usuarioDAO.insertar(new Usuario("Diana", "Rodriguez", "diana@gmail.com", "hgtrRed4@", 2));
            usuarioDAO.insertar(new Usuario("Jose", "Sanchez", "josesanch@gmail.com", "hotrReu4@", 5));
            usuarioDAO.insertar(new Usuario("Paula", "Perez", "paulap@gmail.com", "eytrRed4@", 3));
            usuarioDAO.insertar(new Usuario("Valentina", "Garcia", "valentinag@gmail.com", "wgteRed6@", 1));
            usuarioDAO.insertar(new Usuario("Juana", "Hernandez", "juanah@gmail.com", "tgtrReu4@", 4));

            LOGGER.info("Datos insertados correctamente en la tabla USUARIO");

        } catch (DAOException e) {
            LOGGER.error("Error al insertar ls datos iniciales en la tabla USUARIO: {}", e.getMessage());
        }
    }

    /**
     * Muestra el menú interactivo con las opciones disponibes para gestionar los usuarios.
     *
     * <p>Este menú permite al usuario seleccionar las operaciones que desea realizar sobre los usuarios.</p>
     */
    public void mostrarMenu() {
        int opcion;

        do {
            mostrarOpcionesMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1 -> agregarUsuario();
                case 2 -> obtenerUsuarioPorId();
                case 3 -> listarUsuario();
                case 4 -> actualizarUsuario();
                case 5 -> eliminarUsuario();
                case 6 -> System.out.println("Saliendo de la tabla 'USUARIO'...");
                default -> System.out.println("La opción elegida es incorrecta.");
            }

        } while (opcion != 6);
    }

    /**
     * Muestra las opciones del menú para gestionar usuarios.
     */
    private void mostrarOpcionesMenu() {
        System.out.println("---Menú de Gestión de los Usuarios---");
        System.out.println("1. Agregar usuario");
        System.out.println("2. Obtener usuario por ID");
        System.out.println("3. Mostrar lista de usuarios");
        System.out.println("4. Actualizar usuario");
        System.out.println("5. Eliminar usuario");
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
                    System.out.println("La opción elegida es incorrecta, tiene que ser un número entre 1 y 6, intentalo de nuevo:");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una opción del menu, se tiene que introducir un número entero positivo: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido: ");
            }
        }
        return opcion;
    }

    /**
     * Solicita al usuario el ID del usuario para verificar que sea correcto. Además, se asegura
     * que el ID sea un número entero mayor que 0.
     *
     * @return el ID del usuario que sea correcto.
     */
    private int validarId() {
        int id = 0;
        boolean esIdValido = Boolean.FALSE;

        while (!esIdValido) {
            System.out.println("Introduzca el ID del usuario: ");

            try {
                id = sc.nextInt();
                sc.nextLine();

                if (id > 0) {
                    esIdValido = Boolean.TRUE;
                } else {
                    LOGGER.info("El número de ID del usuario tiene que ser mayor que 0: {}", id);
                    System.out.println("El número del ID tiene que ser mayor que 0, intentalo de nuevo: ");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar un usuario por su ID, se tiene que introducir un número entero mayor que 0: {}", e.getMessage());
                System.out.println("Por favor, introduzca un número válido para el ID del usuario: ");
            }
        }
        return id;
    }

    /**
     * Este método se encarga de verificar el atributo, nombre o apellido.
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
                System.out.println("Cada palabra debe empezar por mayúscula, tener al menos tres caracteres y solo contener letras y en caso de ser necesario espacios, intentalo de nuevo: ");
            }

        } while (campo.length() < 3 || !campo.matches(patron));

        return campo;
    }

    /**
     * Este método se encarga de verificar el campo correo electrónico.
     *
     * <p>Solicita al usuario que introduzca un correo electrónico y verifica que cumpla las siguientes condiciones: </p>
     * <ul>
     *     <li>Debe incluir el símbolo '@'.</li>
     *     <li>Tiene que tener al menos tres caracteres.</li>
     *     <li>Debe incluir al menos un punto despues del '@'.</li>
     * </ul>
     *
     * <p>Además, el correo debe ser único para cada usuario, sin embargo, como no es un campo obligatorio, puede estar vacío</p>
     * <p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa el correo electrónico introducido por el usuario.
     */
    private String verificarCorreo() {
        String correo;
        String patron = ".{3,}@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

        do {
            correo = sc.nextLine();

            if (correo.isEmpty()) {
                return correo;
            }

            if (correosList.contains(correo)) {
                LOGGER.info("El correo ya está registrado, tiene que introducir otro.");
                System.out.println("El correo ya está registrado, tiene que introducir otro: ");
            }

            if (correo.length() < 3 || !correo.matches(patron)) {
                LOGGER.info("El correo debe tener al menos tres caracteres, incluir el '@' y un punto después del '@'.");
                System.out.println("El correo debe tener al menos tres caracteres, incluir el '@' y un punto después del '@', intentalo de nuevo: ");
            }

        } while (correo.length() < 3 || !correo.matches(patron) || correosList.contains(correo));

        correosList.add(correo);

        return correo;
    }

    /**
     * Este método se encarga de verificar el campo contraseña.
     *
     * <p>Solicita al usuario que introduzca una contraseña y verifica que cumpla las siguientes condiciones: </p>
     * <ul>
     *     <li>Tiene que tener al menos 8 caracteres.</li>
     *     <li>Debe contener al menos una letra mayúscula.</li>
     *     <li>Tiene que incluir al menos un carácter especial, por ejemplo: '@', '-', '?', '*', etc.</li>
     * </ul>
     * <p>
     * Si el campo no cumple estas condiciones, se le notifica al usuario y se solicita el campo nuevamente.
     *
     * @return una cadena válida que representa la contraseña introducida por el usuario.
     */
    private String verificarContrasenya() {
        String contrasenya;
        String patron = "(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}";

        do {
            contrasenya = sc.nextLine();

            if (contrasenya.length() < 8 || !contrasenya.matches(patron)) {
                LOGGER.info("La contraseña debe tener al menos 8 caracteres, incluir al menos una mayúscula y un carácter especial.");
                System.out.println("La contraseña debe tener al menos 8 caracteres, incluir al menos una mayúscula y un carácter especial, intentalo de nuevo: ");
            }

        } while (contrasenya.length() < 8 || !contrasenya.matches(patron));

        return contrasenya;
    }

    /**
     * Este método se encarga de solicitar un número mayor que cero y lo valida para el campo ID de la jugadora.
     * Si el usuario decide dejar el campo vacío también se considera válido.
     *
     * <p>Si el valor introducido no es un número o es menor o igual a cero, se le pedirá al usuario que introduzca el
     * número nuevamente.</p>
     *
     * @return un número entero mayor que cero introducido por el usuario.
     */
    private Integer verificarIdJugadora() {
        Integer idJugadora = null;
        boolean esEntradaValida = Boolean.FALSE;

        do {
            String entrada = sc.nextLine();

            if (entrada.isEmpty()) {
                return idJugadora;
            }

            try {
                idJugadora = Integer.parseInt(entrada);

                if (idJugadora > 0) {
                    esEntradaValida = Boolean.TRUE;
                } else {
                    LOGGER.info("Debes introducir un número mayor que 0 para el ID de la jugadora.");
                    System.out.println("Por favor, introduzca un número mayor que 0 para el ID de la jugadora: ");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("Se debe introducir un número mayor que 0 para el ID de la jugadora.");
                System.out.println("Por favor, introduzca un número mayor que 0 para el ID de la jugadora: ");
                sc.nextLine();
            }

        } while (!esEntradaValida);
        return idJugadora;
    }

    /**
     * Solicita los datos necesarios para crear un nuevo usuario.
     *
     * <p>Estos datos incluyen el nombre, email, contrasenya y el identificador único de la jugadora.</p>
     *
     * @return un objeto {@link Usuario} con los datos introducidos por el usuario.
     */
    private Usuario preguntarDatosUsuario() {
        System.out.print("Introduzca el nombre (este campo es obligatorio): ");
        String nombre = verificarNombreApellido();

        System.out.print("Introduzca los apellidos (este campo es obligatorio): ");
        String apellidos = verificarNombreApellido();

        System.out.print("Introduzca el email: ");
        String email = verificarCorreo();

        System.out.print("Introduzca la contraseña (este campo es obligatorio): ");
        String contrasenya = verificarContrasenya();

        System.out.print("Introduzca el ID de la jugadora: ");
        int idJugadora = verificarIdJugadora();
        sc.nextLine();

        return new Usuario(nombre, apellidos, email, contrasenya, idJugadora);
    }

    /**
     * Este método se encarga de agregar un nuevo usuario en la base de datos.
     *
     * <p>Los datos se solicitan al usuario, y se validan antes de agregar el usuario a la base de datos.</p>
     */
    public void agregarUsuario() {
        Usuario usuario = preguntarDatosUsuario();
        try {
            usuarioDAO.insertar(usuario);
            LOGGER.info("Usuario agregado correctamente con ID: {}", usuario.getIdUsuario());
            System.out.println("Usuario agregado con ID: " + usuario.getIdUsuario());
        } catch (DAOException e) {
            LOGGER.error("No se ha podido agregar el usuario con el ID: {}, {}", usuario.getIdUsuario(), e.getMessage());
        }
    }

    /**
     * Este método se encarga de obtener un usuario a partir de su ID.
     *
     * <p>Solicita al usuario el ID y obtiene los datos del usuario desde la base de datos.</p>
     *
     * @return un objeto {@link Usuario} con sus correspondientes datos, o un valor null si no se encuentra un usuario con el ID proporcionado.
     */
    public Usuario obtenerUsuarioPorId() {
        int id = validarId();
        try {
            Usuario usuario = usuarioDAO.obtenerPorId(id);
            if (usuario != null) {
                LOGGER.info("Se ha encontrado el usuario correctamente con ID: {}", id);
                System.out.println("Usuario encontrado por ID: " + usuario);
                return usuario;
            } else {
                LOGGER.info("No se ha encontrado ningun usuario con el ID proporcionado: {}", id);
                System.out.println("No se encontro usuario con ID: " + id);
                return null;
            }
        } catch (DAOException e) {
            LOGGER.error("No se ha podido encontrar el usuario con el ID: {}, {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de listar todos los usuarios registrados.
     *
     * <p>Recupera y muestra todos los usuarios almacenados en la base de datos.</p>
     *
     * @return una lista con todos los usuarios, o devuelve un valor null si la lista está vacía.
     */
    public List<Usuario> listarUsuario() {
        try {
            List<Usuario> usuarioList = usuarioDAO.obtenerTodos();

            if (usuarioList != null && !usuarioList.isEmpty()) {
                System.out.println("---Lista de usuarios---");
                for (Usuario usuario : usuarioList) {
                    System.out.println(usuario);
                }
                return usuarioList;

            } else {
                System.out.println("No se encontraron usuarios la lista está vacía.");
                return null;
            }

        } catch (DAOException e) {
            LOGGER.error("La lista de usuarios está vacía: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Este método se encarga de actualizar los datos de un usuario existente.
     *
     * <p>Permite actualizar los datos de un usuario. El usuario debe introducir el ID del usuario que
     * desea actualizar, y luego podrá modificar sus datos.</p>
     */
    public void actualizarUsuario() {
        int id = validarId();
        System.out.println("Introduzca los nuevos datos del usuario: ");

        Usuario usuario = preguntarDatosUsuario();
        usuario.setIdUsuario(id);
        try {
            usuarioDAO.actualizar(usuario);
            LOGGER.info("Se ha actualizado el usuario correctamente con ID: {}", id);
            System.out.println("Usuario actualizado: " + usuario);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido actualizar el usuario con ID: {}, {}", id, e.getMessage());
        }
    }

    /**
     * Este método se encarga de eliminar un usuario de la base de datos.
     *
     * <p>Permite eliminar un usuario de la base de datos mediante su ID. Se solicita al
     * usuario el ID del usuario y, si existe, se eliminará correctamente.</p>
     */
    public void eliminarUsuario() {
        int id = validarId();
        try {
            usuarioDAO.eliminar(id);
            LOGGER.info("Se ha eliminado el usuario correctamente con ID: {}", id);
            System.out.println("Usuario eliminado correctamente con ID: " + id);
        } catch (DAOException e) {
            LOGGER.error("No se ha podido eliminar el usuario con ID: {}, {}", id, e.getMessage());
        }
    }
}
