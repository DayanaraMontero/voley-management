package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Esta clase representa la información de un partido, como la fecha, el nombre del equipo competidor 1, entrenador del equipo competidor 1,
 * el nombre del equipo competidor 2, entrenador del equipo competidor 2, resultado, duración y un identificador único para el partido.
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
public class Partido {
    /**
     * Identificador único del partido.
     */
    private int idPartido;

    /**
     * Fecha del partido.
     */
    private LocalDate fecha;

    /**
     * Nombre del primer equipo competidor.
     */
    private String equipoCompetidor1;

    /**
     * Entrenador del primer equipo competidor.
     */
    private int entrenadorCompetidor1;

    /**
     * Nombre del segundo equipo competidor.
     */
    private String equipoCompetidor2;

    /**
     * Entrenador del segundo equipo competidor.
     */
    private int entrenadorCompetidor2;

    /**
     * Resultado del partido.
     */
    private String resultado;

    /**
     * Duración del partido.
     */
    private int duracion;

    /**
     * Constructor parcial de la clase Partido.
     *
     * <p>Inicializa los atributos fecha, equipoCompetidor1, entrenadorCompetidor1, equipoCompetidor2,  entrenadorCompetidor2,
     * resultado, y duracion, excluyendo el atributo idPartido. Este constructor es útil cuando el ID del partido se genera automáticamente
     * o se asigna posteriormente.</p>
     *
     * @param fecha                 la fecha del partido.
     * @param equipoCompetidor1     el nombre del primer equipo competidor.
     * @param entrenadorCompetidor1 el entrenador del primer equipo competidor.
     * @param equipoCompetidor2     el nombre del segundo equipo competidor.
     * @param entrenadorCompetidor2 el entrenador del segundo equipo competidor.
     * @param resultado             el resultado del partido.
     * @param duracion              la duración del partido.
     */
    public Partido(LocalDate fecha, String equipoCompetidor1, int entrenadorCompetidor1, String equipoCompetidor2, int entrenadorCompetidor2, String resultado, int duracion) {
        this.fecha = fecha;
        this.equipoCompetidor1 = equipoCompetidor1;
        this.entrenadorCompetidor1 = entrenadorCompetidor1;
        this.equipoCompetidor2 = equipoCompetidor2;
        this.entrenadorCompetidor2 = entrenadorCompetidor2;
        this.resultado = resultado;
        this.duracion = duracion;
    }
}
