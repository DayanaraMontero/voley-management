package es.cheste.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta clase representa la información de un usuario como el nombre, apellidos, email, contraseña,
 * identificador único de la jugadora y un identificador único para el usuario.
 *
 * <p>Utiliza anotaciones de la librería Lombok para generar automáticamente los métodos getters, setters,
 * toString, hashCode, equals, y los distintos métodos constructores.</p>
 *
 * <p>Tenemos tres constructores en esta clase: </p>
 * <ul>
 *     <li>Un constructor completo, que incluye todos los atributos, incluyendo el ID.</li>
 *     <li>Un constructor vacío, no incluye ningún atributo</li>
 *     <li>Por último, un constructor parcial, que excluye el ID, ya que se asume que se genera automáticamente o se asignará posteriormente</li>
 * </ul>
 *
 * @see lombok.Data
 * @see lombok.NoArgsConstructor
 * @see lombok.AllArgsConstructor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    /**
     * Identificador único del usuario.
     */
    private int idUsuario;

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Apellidos del usuario.
     */
    private String apellidos;

    /**
     * Email del usuario.
     */
    private String email;

    /**
     * Contraseña del usuario.
     */
    private String contrasenya;

    /**
     * Identificador único de la jugadora.
     */
    private int idJugadora;

    /**
     * Constructor parcial de la clase Usuario.
     *
     * <p>Inicializa los atributos nombre, email, contraseña e identificador único de la jugadora, excluyendo el atributo
     * idUsuario. Este constructor es útil cuando el ID del usuario se genera automáticamente o
     * se asigna posteriormente.</p>
     *
     * @param nombre      el nombre del usuario.
     * @param apellidos   los apellidos del usuario.
     * @param email       el email del usuario.
     * @param contrasenya la contraseña del usuario.
     * @param idJugadora  el identificador único de la jugadora.
     */
    public Usuario(String nombre, String apellidos, String email, String contrasenya, int idJugadora) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contrasenya = contrasenya;
        this.idJugadora = idJugadora;
    }
}
