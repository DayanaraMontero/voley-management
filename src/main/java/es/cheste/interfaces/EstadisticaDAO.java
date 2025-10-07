package es.cheste.interfaces;

import es.cheste.clases.Estadistica;
import es.cheste.excepcion.DAOException;

import java.util.List;

/**
 * Esta interfaz define las operaciones CRUD para la entidad Estadistica.
 *
 * <p>Permite manipular los datos de las estadísticas en la base de datos y lanza {@link DAOException}
 * en caso de errores durante el acceso a los datos.</p>
 */
public interface EstadisticaDAO {

    /**
     * Inserta una nueva estadística en la base de datos.
     *
     * @param estadistica la estadística que se desea insertar.
     * @throws DAOException si ocurre un error durante la inserción de los datos.
     */
    void insertar(Estadistica estadistica) throws DAOException;

    /**
     * Obtiene una estadística por su identificador único.
     *
     * @param id el identificador único de la estadística.
     * @return la estadística con el ID correspondiente, o devuelve el valor null si no se encuentra.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    Estadistica obtenerPorId(int id) throws DAOException;

    /**
     * Obtiene una lista de todas las estadísticas en la base de datos.
     *
     * @return una lista de todas las estadísticas.
     * @throws DAOException si ocurre un error durante la obtención de los datos.
     */
    List<Estadistica> obtenerTodos() throws DAOException;

    /**
     * Actualiza la información de una estadística existente en la base de datos.
     *
     * @param estadistica la estadistica con la información actualizada.
     * @throws DAOException sí ocurre un error durante la actualización.
     */
    void actualizar(Estadistica estadistica) throws DAOException;

    /**
     * Elimina una estadística de la base de datos utilizando su identificador único.
     *
     * @param id el identificador único de la estadística para eliminar.
     * @throws DAOException sí ocurre un error durante la eliminación.
     */
    void eliminar(int id) throws DAOException;
}
