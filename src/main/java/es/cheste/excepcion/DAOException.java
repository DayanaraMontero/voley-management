package es.cheste.excepcion;

/**
 * Esta clase representa una excepción específica para errores de acceso a datos (DAO).
 *
 * <p>Se utiliza para manejar errores, permitiendo que los detalles de la causa
 * y el mensaje de error se propaguen en el programa.</p>
 */
public class DAOException extends Exception {

    /**
     * Construye una nueva excepción con un mensaje descriptivo y una causa específica.
     *
     * @param mensaje el mensaje detallado de la excepción.
     * @param causa   la causa de la excepción.
     */
    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
