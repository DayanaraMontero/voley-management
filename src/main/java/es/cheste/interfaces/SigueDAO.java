package es.cheste.interfaces;

import es.cheste.clases.Sigue;
import es.cheste.excepcion.DAOException;

import java.time.LocalDate;
import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Sigue.
 *
 * <p>Permite manipular los datos de los seguimientos de los usuarios a sus jugadoras favoritas en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface SigueDAO {

    /**
     * Inserta un nuevo seguimiento en la base de datos.
     *
     * @param sigue el seguimiento que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Sigue sigue) throws DAOException;

    /**
     * Obtiene un seguimiento por sus identificadores únicos.
     *
     * @param idUsuario        identificador único del usuario.
     * @param idJugadora       identificador único de la jugadora.
     * @param fechaSeguimiento la fecha de seguimiento del usuario a la jugadora.
     * @return el seguimiento con los ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Sigue obtenerPorId(int idUsuario, int idJugadora, LocalDate fechaSeguimiento) throws DAOException;

    /**
     * Obtiene una lista de todos los seguimientos en la base de datos.
     *
     * @return una lista de todos los seguimientos.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Sigue> obtenerTodos() throws DAOException;

    /**
     * Actualiza la información de un seguimiento existente en la base de datos.
     *
     * @param sigue el seguimiento con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización.
     */
    void actualizar(Sigue sigue) throws DAOException;

    /**
     * Elimina un seguimiento de la base de datos utilizando sus identificadores únicos.
     *
     * @param idUsuario        el identificador único del usuario para eliminar.
     * @param idJugadora       el identificador único de la jugadora para eliminar.
     * @param fechaSeguimiento la fecha del seguimiento del usuario a la jugadora.
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int idUsuario, int idJugadora, LocalDate fechaSeguimiento) throws DAOException;
}
