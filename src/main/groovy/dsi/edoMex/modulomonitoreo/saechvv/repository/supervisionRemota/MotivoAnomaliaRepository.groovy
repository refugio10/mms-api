package dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.MotivoAnomalia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Contiene funciones relacionadas con la tabla CAT_MOTIVO_ANOMALIA
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface MotivoAnomaliaRepository extends JpaRepository<MotivoAnomalia,Integer>{

    /**
     * Obtiene todos los motivos de anomalía ordenados por el id ascendente
     * @return List<MotivoAnomalia> Lista de motivos de anomalía
     */
    List<MotivoAnomalia> findAllByOrderByIdAsc()

    /**
     * Obtiene la descripción de un motivo de anomalía por un identificador en específico
     * @param idMotivoAnomalia Integer identificador del motivo de anomalía
     * @return String descripción del motivo de anomalía
     */
    @Query(value = "SELECT DESCRIPCION FROM CAT_MOTIVO_ANOMALIA where ID_CAT_MOTIVO_ANOMALIA=:idMotivoAnomalia", nativeQuery = true)
    String getDescripcionAnomalia(@Param("idMotivoAnomalia") Integer idMotivoAnomalia)
}