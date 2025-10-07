package es.cheste.interfaces;

import es.cheste.clases.Juega;
import es.cheste.excepcion.DAOException;

import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Juega.
 *
 * <p>Permite manipular los datos de las participaciones de las jugadores en los partidos en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface JuegaDAO {

    /**
     * Inserta una nueva participación en la base de datos.
     *
     * @param juega la participación que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Juega juega) throws DAOException;

    /**
     * Obtiene una participación por su identificador único.
     *
     * @param idJugadora el identificador único de la jugadora.
     * @param idPartido  el identificador único del partido.
     * @return la participación con los ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Juega obtenerPorId(int idJugadora, int idPartido) throws DAOException;

    /**
     * Obtiene una lista de todas las participaciones en la base de datos.
     *
     * @return una lista de todas las participaciones.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Juega> obtenerTodos() throws DAOException;

    /**
     * Actualiza la información de una participación existente en la base de datos.
     *
     * @param juega la participación con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización
     */
    void actualizar(Juega juega) throws DAOException;

    /**
     * Elimina una participación de la base de datos utilizando sus identificadores únicos.
     *
     * @param idJugadora el identificador único de la jugadora para eliminar
     * @param idPartido  el identificador único del partido para eliminar
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int idJugadora, int idPartido) throws DAOException;
}
