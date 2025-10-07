package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Esta clase representa la información del seguimiento de un usuario a una jugadora determinada, como la frecuencia de interacción,
 * observaciones, fecha de seguimiento e identificadores únicos para el usuario y la jugadora.
 *
 * <p>Utiliza anotaciones de la librería Lombok para generar automáticamente los métodos getters, setters,
 * toString, hashCode, equals, y los distintos métodos constructores.</p>
 *
 * <p>Tenemos dos constructores en esta clase: </p>
 * <ul>
 *     <li>Un constructor completo, que incluye todos los atributos, incluyendo los identificadores y la fecha de seguimiento.</li>
 *     <li>Un constructor vacío, no incluye ningún atributo.</li>
 * </ul>
 *
 * @see lombok.Data
 * @see lombok.NoArgsConstructor
 * @see lombok.AllArgsConstructor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sigue {
    /**
     * Identificador único del usuario.
     */
    private int idUsuario;

    /**
     * Identificador único de la jugadora.
     */
    private int idJugadora;

    /**
     * Fecha de seguimiento del usuario a la jugadora.
     */
    private LocalDate fechaSeguimiento;

    /**
     * Frecuencia de interacción del usuario.
     */
    private String frecuenciaInteraccion;

    /**
     * Observaciones del usuario.
     */
    private String observaciones;
}
