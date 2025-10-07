package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta clase representa la información de un entrenador, como el nombre, apellidos, nacionalidad,
 * años de experiencia y un identificador único para el entrenador.
 *
 * <p>Utiliza anotaciones de la librería Lombok para generar automáticamente los métodos getters, setters,
 * toString, hashCode, equals y los distintos métodos constructores.</p>
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
public class Entrenador {
    /** Identificador único del entrenador.*/
    private int idEntrenador;

    /** Nombre del entrenador.*/
    private String nombre;

    /** Apellidos del entrenador.*/
    private String apellidos;

    /** Nacionalidad del entrenador.*/
    private String nacionalidad;

    /** Años de experiencia del entrenador.*/
    private int experiencia;

    /**
     * Constructor parcial de la clase Entrenador.
     *
     * <p>Inicializa los atributos nombre, apellidos, nacionalidad y experiencia del entrenador, excluyendo el atributo
     * idEntrenador. Este constructor es útil cuando el ID del entrenador se genera automáticamente o se asigna
     * posteriormente.</p>
     *
     * @param nombre el nombre del entrenador.
     * @param apellidos los apellidos del entrenador.
     * @param nacionalidad la nacionalidad del entrenador.
     * @param experiencia los años de experiencia del entrenador.
     */
    public Entrenador(String nombre, String apellidos, String nacionalidad, int experiencia) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacionalidad = nacionalidad;
        this.experiencia = experiencia;
    }
}
