package es.cheste.interfaces;

import es.cheste.clases.Usuario;
import es.cheste.excepcion.DAOException;

import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Usuario.
 *
 * <p>Permite manipular los datos de los usuarios en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface UsuarioDAO {

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param usuario el usuario que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Usuario usuario) throws DAOException;

    /**
     * Obtiene un usuario por su identificador único.
     *
     * @param id el identificador único del usuario.
     * @return el usuario con el ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Usuario obtenerPorId(int id) throws DAOException;

    /**
     * Obtiene una lista de todos los usuarios en la base de datos.
     *
     * @return una lista de todos los usuarios.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Usuario> obtenerTodos() throws DAOException;

    /**
     * Actualiza la información de un usuario existente en la base de datos.
     *
     * @param usuario el usuario con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización.
     */
    void actualizar(Usuario usuario) throws DAOException;

    /**
     * Elimina un usuario de la base de datos utilizando su identificador único.
     *
     * @param id el identificador único del usuario para eliminar.
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int id) throws DAOException;
}
