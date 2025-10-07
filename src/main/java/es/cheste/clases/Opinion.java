package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta clase representa la información de una opinión, como el identificador del partido, identificador de la jugadora,
 * identificador del usuario, puntuación, comentario y un identificador único para la opinión.
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
public class Opinion {
    /**
     * Identificador único de la opinión.
     */
    private int idOpinion;

    /**
     * Identificador único del partido.
     */
    private int idPartido;

    /**
     * Identificador único de la jugadora.
     */
    private int idJugadora;

    /**
     * Identificador único del usuario.
     */
    private int idUsuario;

    /**
     * Puntuación del usuario respecto al desarrollo del partido y la jugadora.
     */
    private int puntuacion;

    /**
     * Comentario del usuario respecto al desarrollo del partido y la jugadora.
     */
    private String comentario;

    /**
     * Constructor parcial de la clase Opinion.
     *
     * <p>Inicializa los atributos idPartido, idJugadora, idUsuario, puntuacion y comentario, excluyendo el atributo
     * idOpinion. Este constructor es útil cuando el ID de la opinión se genera automáticamente
     * o se asigna posteriormente.</p>
     *
     * @param idPartido  el identificador único del partido.
     * @param idJugadora el identificador único de la jugadora.
     * @param idUsuario  el identificador único del usuario.
     * @param puntuacion la puntuación del usuario respecto al partido y la jugadora.
     * @param comentario el comentario del usuario respecto al partido y la jugadora.
     */
    public Opinion(int idPartido, int idJugadora, int idUsuario, int puntuacion, String comentario) {
        this.idPartido = idPartido;
        this.idJugadora = idJugadora;
        this.idUsuario = idUsuario;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }
}
