package dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.AnomaliaMesaControl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


/**
 * Contiene funciones relacionadas con la tabla ANOMALIAS_MESA_CONTROL
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface AnomaliaMesaControlRepository extends JpaRepository<AnomaliaMesaControl, Integer> {

    /**
     * Obtiene el siguiente Id de la secuencia SEQ_ANOMALIAS_MESA_CONTROL
     *
     * @return Integer con el valor de la secuencia SEQ_ANOMALIAS_MESA_CONTROL
     */
    @Query(value = "SELECT NEXT VALUE FOR SEQ_ANOMALIAS_MESA_CONTROL as id ", nativeQuery = true)
    Integer getIdAnomalia()

    /**
     * Obtiene la última anomalía de mesa de constrol por una verificación es específico
     * @param idVerificacion Integer identificador de la verificación
     * @return Optional<AnomaliaMesaControl> objeto con la información encontrada
     */
    Optional<AnomaliaMesaControl> findFirstByVerificacionIdOrderByIdDesc(Integer idVerificacion)
}