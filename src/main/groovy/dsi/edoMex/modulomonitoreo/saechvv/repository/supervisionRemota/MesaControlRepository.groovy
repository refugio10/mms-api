package dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.MesaControl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Contiene funciones relacionadas con la tabla MESA_CONTROL
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface MesaControlRepository extends JpaRepository<MesaControl, Integer>{

    /**
     * Obtiene el siguiente Id de la secuencia SEQ_MESA_CONTROL
     *
     * @return Integer con el valor de la secuencia SEQ_MESA_CONTROL
     */
    @Query(value = "SELECT NEXT VALUE FOR SEQ_MESA_CONTROL as maximo ", nativeQuery = true)
    Integer getSiguienteIdMesaControl()

    /**
     * Obtiene una lista de mesas de control por medio del id único
     * @param idUnique String id único de la mesa de control a consultar
     * @return List<MesaControl> Lista de mesas de control encontradas
     */
    List<MesaControl> findAllByIdUnique(String idUnique)
}