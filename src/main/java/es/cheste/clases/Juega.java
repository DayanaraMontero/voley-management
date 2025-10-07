package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta clase representa la información de la participación de una jugadora en su partido correspondiente,
 * como el minuto de entrada, minuto de salida, estado y los identificadores únicos de la jugadora y el partido.
 *
 * <p>Utiliza anotaciones de la librería Lombok para generar automáticamente los métodos getters, setters,
 * toString, hashCode, equals, y los distintos métodos constructores.</p>
 *
 * <p>Tenemos dos constructores en esta clase: </p>
 * <ul>
 *     <li>Un constructor completo, que incluye todos los atributos, incluyendo los identificadores.</li>
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
public class Juega {
    /**
     * Identificador único de la jugadora.
     */
    private int idJugadora;

    /**
     * Identificador único del partido.
     */
    private int idPartido;

    /**
     * Minuto de entrada de la jugadora al partido.
     */
    private int minutoEntrada;

    /**
     * Minuto salida de la jugadora al partido.
     */
    private int minutoSalida;

    /**
     * Estado de la jugadora durante el partido.
     */
    private String estado;
}
