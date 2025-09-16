package dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.BitacoraAPR
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


/**
 * Contiene funciones relacionadas con la tabla BITACORA_APR
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface BitacoraAprRepository extends JpaRepository<BitacoraAPR, Integer>{


    /**
     * Actualiza el estatus de la bitácora APR por su verificación
     * @param verificacion Integer identificador de la verificación
     * @param nuevoEstatus Integer estatus a establecer para las bitácoras
     * @return Integer total de registros actualizados
     */
    @Modifying
    @Query(value = "UPDATE BITACORA_APR SET STATUS = :nuevoEstatus WHERE ID_VERIFICACION = :verificacion", nativeQuery = true)
    @Transactional
    Integer updateEstatusBitacoraByVerificacion(@Param("verificacion") Integer verificacion,
                                                @Param("nuevoEstatus") Integer nuevoEstatus)

}