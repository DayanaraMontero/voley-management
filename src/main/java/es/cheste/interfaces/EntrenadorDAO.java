package es.cheste.interfaces;

import es.cheste.clases.Entrenador;
import es.cheste.excepcion.DAOException;

import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Entrenador.
 *
 * <p>Permite manipular los datos de los entrenadores en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface EntrenadorDAO {

    /**
     * Inserta un nuevo entrenador en la base de datos.
     *
     * @param entrenador el entrenador que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Entrenador entrenador) throws DAOException;

    /**
     * Obtiene un entrenador por su identificador único.
     *
     * @param id el identificador único del entrenador.
     * @return el entrenador con el ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Entrenador obtenerPorId(int id) throws DAOException;

    /**
     * Obtiene una lista de todos los entrenadores en la base de datos.
     *
     * @return una lista de todos los entrenadores.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Entrenador> obtenerTodos() throws DAOException;

    /**
     * Actualiza la información de un entrenador existente en la base de datos.
     *
     * @param entrenador el entrenador con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización.
     */
    void actualizar(Entrenador entrenador) throws DAOException;

    /**
     * Elimina un entrenador de la base de datos utilizando su identificador único.
     *
     * @param id el identificador único del entrenador para eliminar.
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int id) throws DAOException;
}
