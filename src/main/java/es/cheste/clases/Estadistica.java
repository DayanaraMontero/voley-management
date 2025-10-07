package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta clase representa la información de una estadística, como el ID de la jugadora, ID del partido, ataques,
 * saques, bloqueos, defensas, errores y un identificador único para la estadística.
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
public class Estadistica {
    /**
     * Identificador único de la estadística.
     */
    private int idEstadistica;

    /**
     * Identificador único de la jugadora.
     */
    private int idJugadora;

    /**
     * Identificador único del partido.
     */
    private int idPartido;

    /**
     * Número de ataques realizados por la jugadora.
     */
    private int ataques;

    /**
     * Número de saques realizados por la jugadora.
     */
    private int saques;

    /**
     * Número de bloqueos realizados por la jugadora.
     */
    private int bloqueos;

    /**
     * Número de defensas realizados por la jugadora.
     */
    private int defensas;

    /**
     * Número de errores realizados por la jugadora.
     */
    private int errores;

    /**
     * Constructor parcial de la clase Estadística.
     *
     * <p>Inicializa los atributos idJugadora, idPartido, ataques, saques, bloqueos, defensas y errores,
     * excluyendo el atributo idEstadistica. Este constructor es útil cuando el ID de la estadística se
     * se genera automáticamente o se asigna posteriormente.</p>
     *
     * @param idJugadora el identificador único de la jugadora.
     * @param idPartido  el identificador único del partido.
     * @param ataques    número de ataques realizados por la jugadora.
     * @param saques     número de saques realizados por la jugadora.
     * @param bloqueos   número de bloqueos realizados por la jugadora.
     * @param defensas   número de defensas realizados por la jugadora.
     * @param errores    número de errores realizados por la jugadora.
     */
    public Estadistica(int idJugadora, int idPartido, int ataques, int saques, int bloqueos, int defensas, int errores) {
        this.idJugadora = idJugadora;
        this.idPartido = idPartido;
        this.ataques = ataques;
        this.saques = saques;
        this.bloqueos = bloqueos;
        this.defensas = defensas;
        this.errores = errores;
    }
}
