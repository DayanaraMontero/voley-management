package es.cheste;

import es.cheste.conexion.ConexionBD;
import es.cheste.servicios.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Es la clase principal que gestiona la ejecución de la aplicación de la Volleyball Nations League (VNL).
 * Esta clase se encarga de conectar con la base de datos, crear las tablas necesarias, y mostrar un menú interactivo
 * para que el usuario pueda seleccionar la tabla con la que desea trabajar.
 *
 * <p>La aplicación cuenta con varias tablas que pueden ser gestionadas desde el menú: Entrenador, Jugadora, Partido,
 * Estadistica, Usuario, Opinion, Juega y Sigue.</p>
 *
 * <p>Los métodos de esta clase utilizan los servicios correspondientes para gestionar las tablas y ejecutar consultas a la
 * base de datos.</p>
 *
 * @see es.cheste.conexion.ConexionBD
 * @see es.cheste.servicios.GestionEntrenador
 * @see es.cheste.servicios.GestionJugadora
 * @see es.cheste.servicios.GestionPartido
 * @see es.cheste.servicios.GestionEstadistica
 * @see es.cheste.servicios.GestionUsuario
 * @see es.cheste.servicios.GestionOpinion
 * @see es.cheste.servicios.GestionJuega
 * @see es.cheste.servicios.GestionSigue
 */
public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConexionBD conexionBD = new ConexionBD();

    private static final GestionEntrenador gestionEntrenador = new GestionEntrenador();
    private static final GestionJugadora gestionJugadora = new GestionJugadora();
    private static final GestionPartido gestionPartido = new GestionPartido();
    private static final GestionEstadistica gestionEstadistica = new GestionEstadistica();
    private static final GestionUsuario gestionUsuario = new GestionUsuario();
    private static final GestionOpinion gestionOpinion = new GestionOpinion();
    private static final GestionJuega gestionJuega = new GestionJuega();
    private static final GestionSigue gestionSigue = new GestionSigue();

    /**
     * Es el método principal que inicia la ejecución de la aplicación, conecta con la base de datos y
     * crea las tablas necesarias, luego muestra el menú de opciones para que el usuario seleccione con
     * qué tabla desea trabajar.
     *
     * @param args argumentos de línea de comandos, no se utilizan en esta implementación.
     */
    public static void main(String[] args) {
        //eliminarTodasLasTablas();
        crearTablas();
        insertarDatosIniciales();
        mostrarOpcionTablas();
    }

    /**
     * Este método se encarga de crear las tablas necesarias en la base de datos.
     * Llama a los métodos correspondientes de cada clase de gestión para crear las tablas relacionadas con:
     * Entrenador, Jugadora, Partido, Estadistica, Usuario, Opinion, Juega y Sigue.
     */
    private static void crearTablas() {
        gestionEntrenador.crearTabla(Main.conexionBD);
        gestionJugadora.crearTabla(Main.conexionBD);
        gestionPartido.crearTabla(Main.conexionBD);
        gestionEstadistica.crearTabla(Main.conexionBD);
        gestionUsuario.crearTabla(Main.conexionBD);
        gestionOpinion.crearTabla(Main.conexionBD);
        gestionJuega.crearTabla(Main.conexionBD);
        gestionSigue.crearTabla(Main.conexionBD);
    }

    private static void eliminarTodasLasTablas() {
        String[] tablas = {"JUEGA", "SIGUE", "OPINION", "USUARIO", "ESTADISTICA", "PARTIDO", "JUGADORA", "ENTRENADOR"};

        for(String tabla : tablas) {
            String sql = "DROP TABLE IF EXISTS " + tabla;

            try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(sql)) {
                ps.executeUpdate();
                LOGGER.info("Tabla {} eliminada correctamente", tabla);

            } catch (SQLException e) {
                LOGGER.error("Error al eliminar la tabla {}: {}", tabla, e.getMessage());
            }
        }
    }

    private static void insertarDatosIniciales() {
        if(tablaEstaVacia("ENTRENADOR")) {
            gestionEntrenador.insertarDatosIniciales();
        }

        if(tablaEstaVacia("JUGADORA")) {
            gestionJugadora.insertarDatosIniciales();
        }

        if(tablaEstaVacia("PARTIDO")) {
            gestionPartido.insertarDatosIniciales();
        }

        if(tablaEstaVacia("ESTADISTICA")) {
            gestionEstadistica.insertarDatosIniciales();
        }

        if(tablaEstaVacia("USUARIO")) {
            gestionUsuario.insertarDatosIniciales();
        }

        if(tablaEstaVacia("OPINION")) {
            gestionOpinion.insertarDatosIniciales();
        }

        if(tablaEstaVacia("JUEGA")) {
            gestionJuega.insertarDatosIniciales();
        }

        if(tablaEstaVacia("SIGUE")) {
            gestionSigue.insertarDatosIniciales();
        }
    }

    private static boolean tablaEstaVacia(String nombreTabla) {
        String sql = "SELECT COUNT(*) FROM " + nombreTabla;

        try (PreparedStatement ps = conexionBD.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if(rs.next()) {
                return rs.getInt(1) == 0;
            }

        }   catch (SQLException e) {
            LOGGER.error("Error al verificar que la tabla esta vacia: {}: {}", nombreTabla, e.getMessage());
        }
        return false;
    }

    /**
     * Este método se encarga de mostrar el menú de opciones para que el usuario seleccione con qué tabla desea trabajar.
     * El programa continúa hasta que el usuario seleccione la opción de salir (opción 9).
     *
     * <p>Después de que el usuario la seleccione, se llama al método correspondiente para gestionar la tabla seleccionada por el usuario.</p>
     *
     * <p>Al final del método, se asegura de que los recursos, como la conexión y el Scanner se cierren independientemente de si
     * ocurre una excepción.</p>
     */
    private static void mostrarOpcionTablas() {
        int opcion;
        System.out.println("¡Bienvenido a la aplicación oficial de la Volleyball Nations League!");

        try (Scanner sc = new Scanner(System.in)) {
            do {
                mostrarOpcionesMenu();
                opcion = leerOpcion(sc);

                switch (opcion) {
                    case 1 -> gestionEntrenador.mostrarMenu();
                    case 2 -> gestionJugadora.mostrarMenu();
                    case 3 -> gestionPartido.mostrarMenu();
                    case 4 -> gestionEstadistica.mostrarMenu();
                    case 5 -> gestionUsuario.mostrarMenu();
                    case 6 -> gestionOpinion.mostrarMenu();
                    case 7 -> gestionJuega.mostrarMenu();
                    case 8 -> gestionSigue.mostrarMenu();
                    case 9 -> System.out.println("¡Hasta luego!");
                    default -> System.out.println("La opción elegida es incorrecta.");
                }
            } while (opcion != 9);

        } catch (InputMismatchException e) {
            LOGGER.error("Error, se tiene que introducir un valor numérico: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error inesperado: {}", e.getMessage());
        } finally {
            conexionBD.desconectar();
            LOGGER.info("Se han cerrado los recursos correctamente.");
        }
    }

    /**
     * Este método se encarga de mostrar las opciones del menú para que el usuario decida
     * con qué tabla trabajar.
     */
    private static void mostrarOpcionesMenu() {
        System.out.println("¿Con cuál tabla desea trabajar?");
        System.out.println("1. Tabla ENTRENADOR");
        System.out.println("2. Tabla JUGADORA");
        System.out.println("3. Tabla PARTIDO");
        System.out.println("4. Tabla ESTADISTICA");
        System.out.println("5. Tabla USUARIO");
        System.out.println("6. Tabla OPINION");
        System.out.println("7. Tabla JUEGA");
        System.out.println("8. Tabla SIGUE");
        System.out.println("9. Salir de la aplicacion");
    }

    /**
     * Este método se encarga de leer la opción seleccionada por el usuario.
     * Después, se valida que la opción esté dentro del rango permitido (1-9):
     *
     * @return la opción seleccionada por el usuario.
     */
    private static int leerOpcion(Scanner sc) {
        int opcion = 0;
        boolean esOpcionValida = Boolean.FALSE;

        while (!esOpcionValida) {
            System.out.print("Elige una opción (1-9): ");
            try {
                opcion = sc.nextInt();
                sc.nextLine();

                if (opcion >= 1 && opcion <= 9) {
                    esOpcionValida = Boolean.TRUE;
                } else {
                    System.out.println("La opción elegida es incorrecta, tiene que ser un número entre 1 y 9.");
                }

            } catch (InputMismatchException e) {
                LOGGER.error("Para seleccionar una opción del menú, se tiene que introducir un valor numérico entero positivo: {}", e.getMessage());
                sc.nextLine();
            }
        }
        return opcion;
    }
}
