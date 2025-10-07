package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta clase representa la información de una jugadora, como el nombre, apellidos, posicion, nacionalidad,
 * edad, dorsal, nombre del equipo, identificador único del entrenador y un identificador único para la jugadora.
 *
 * <p>Utiliza anotaciones de la librería Lombok para generar automáticamente los métodos getters, setters,
 * toString, hashCode, equals, y los distintos métodos constructores.</p>
 *
 * <p>Tenemos tres constructores en esta clase: </p>
 * <ul>
 *     <li>Un constructor completo, que incluye todos los atributos, incluyendo el ID.</li>
 *     <li>Un constructor vacío, no incluye ningún atributo.</li>
 *     <li>Por último, un constructor parcial que excluye el ID, ya que se asume que se genera automáticamente o se asignará posteriormente.</li>
 * </ul>
 *
 * @see lombok.Data
 * @see lombok.NoArgsConstructor
 * @see lombok.AllArgsConstructor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jugadora {
    /**
     * Identificador único de la jugadora.
     */
    private int idJugadora;

    /**
     * Nombre de la jugadora.
     */
    private String nombre;

    /**
     * Apellidos de la jugadora.
     */
    private String apellidos;

    /**
     * Posición de la jugadora.
     */
    private String posicion;

    /**
     * Nacionalidad de la jugadora.
     */
    private String nacionalidad;

    /**
     * Edad de la jugadora.
     */
    private int edad;

    /**
     * Número de dorsal de la jugadora.
     */
    private int dorsal;

    /**
     * Nombre del equipo de la jugadora.
     */
    private String nombreEquipo;

    /**
     * Identificador único del entrenador.
     */
    private int idEntrenador;

    /**
     * Constructor parcial de la clase Jugadora.
     *
     * <p>Inicializa los atributos nombre, apellidos, posición, nacionalidad, edad, dorsal, nombreEquipo e idEntrenador,
     * excluyendo el atributo idJugadora. Este constructor es útil cuando el ID de la jugadora se genera automáticamente
     * o se asigna posteriormente.</p>
     *
     * @param nombre       el nombre de la jugadora.
     * @param apellidos    los apellidos de la jugadora.
     * @param posicion     la posición de la jugadora.
     * @param nacionalidad la nacionalidad de la jugadora.
     * @param edad         la edad de la jugadora.
     * @param dorsal       el número de dorsal de la jugadora.
     * @param nombreEquipo el nombre del equipo de la jugadora.
     * @param idEntrenador el identificador único del entrenador.
     */
    public Jugadora(String nombre, String apellidos, String posicion, String nacionalidad, int edad, int dorsal, String nombreEquipo, int idEntrenador) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.posicion = posicion;
        this.nacionalidad = nacionalidad;
        this.edad = edad;
        this.dorsal = dorsal;
        this.nombreEquipo = nombreEquipo;
        this.idEntrenador = idEntrenador;
    }
}
