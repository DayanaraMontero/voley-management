package es.cheste.interfaces;

import es.cheste.clases.Jugadora;
import es.cheste.excepcion.DAOException;

import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Jugadora.
 *
 * <p>Permite manipular los datos de las jugadoras en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface JugadoraDAO {

    /**
     * Inserta una nueva jugadora en la base de datos.
     *
     * @param jugadora la jugadora que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Jugadora jugadora) throws DAOException;

    /**
     * Obtiene una jugadora por su identificador único.
     *
     * @param id el identificador único de la jugadora.
     * @return la jugadora con el ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Jugadora obtenerPorId(int id) throws DAOException;

    /**
     * Obtiene una lista de todas las jugadoras en la base de datos.
     *
     * @return una lista de todas las jugadoras.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Jugadora> obtenerTodos() throws DAOException;

    /**
     * Obtiene una lista de todas las jugadoras con sus respectivos entrenadores en la base de datos.
     *
     * @return una lista de todas las jugadoras con sus entrenadores.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Jugadora> obtenerJugadoraConEntrenador() throws DAOException;

    /**
     * Actualiza la información de una jugadora existente en la base de datos.
     *
     * @param jugadora la jugadora con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización.
     */
    void actualizar(Jugadora jugadora) throws DAOException;

    /**
     * Elimina una jugadora de la base de datos utilizando su identificador único.
     *
     * @param id el identificador único de la jugadora para eliminar.
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int id) throws DAOException;
}
