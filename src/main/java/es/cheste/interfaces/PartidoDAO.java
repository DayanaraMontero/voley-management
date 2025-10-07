package es.cheste.interfaces;

import es.cheste.clases.Partido;
import es.cheste.excepcion.DAOException;

import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Partido.
 *
 * <p>Permite manipular los datos de los partidos en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface PartidoDAO {

    /**
     * Inserta un nuevo partido en la base de datos.
     *
     * @param partido el partido que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Partido partido) throws DAOException;

    /**
     * Obtiene un partido por su identificador único.
     *
     * @param id el identificador único del partido.
     * @return el partido con el ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Partido obtenerPorId(int id) throws DAOException;

    /**
     * Obtiene una lista de todos los partidos en la base de datos.
     *
     * @return una lista de todos los partidos.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Partido> obtenerTodos() throws DAOException;

    /**
     * Actualiza la información de un partido existente en la base de datos.
     *
     * @param partido el partido con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización.
     */
    void actualizar(Partido partido) throws DAOException;

    /**
     * Elimina un partido de la base de datos utilizando su identificador único.
     *
     * @param id el identificador único del partido para eliminar.
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int id) throws DAOException;
}
