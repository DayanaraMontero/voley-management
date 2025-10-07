package es.cheste.interfaces;

import es.cheste.clases.Opinion;
import es.cheste.excepcion.DAOException;

import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Opinion.
 *
 * <p>Permite manipular los datos de las opiniones en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface OpinionDAO {

    /**
     * Inserta una nueva opinión en la base de datos.
     *
     * @param opinion la opinión que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Opinion opinion) throws DAOException;

    /**
     * Obtiene una opinión por su identificador único.
     *
     * @param id el identificador único de la opinión.
     * @return la opinión con el ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Opinion obtenerPorId(int id) throws DAOException;

    /**
     * Obtiene una lista de todas las opiniones en la base de datos.
     *
     * @return una lista de todas las opiniones.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Opinion> obtenerTodos() throws DAOException;

    /**
     * Obtiene una lista de todas las opiniones de los usuarios sobre sus jugadoras favoritas en la base de datos.
     *
     * @return una lista de todas las opiniones de los usuarios sobre sus jugadoras favoritas.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Opinion> obtenerOpiniones() throws DAOException;

    /**
     * Actualiza la información de una opinión existente en la base de datos.
     *
     * @param opinion la opinión con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización.
     */
    void actualizar(Opinion opinion) throws DAOException;

    /**
     * Elimina una opinión de la base de datos utilizando su identificador único.
     *
     * @param id el identificador único de la opinión para eliminar.
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int id) throws DAOException;
}
