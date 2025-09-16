package dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.AnomaliaSupervisionRemota
import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.SupervisionRemota
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Contiene funciones relacionadas con la tabla ANOMALIASSR
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface AnomaliaSupervisionRemotaRepository extends JpaRepository<AnomaliaSupervisionRemota, Integer>{


    /**
     * Obtiene el siguiente Id de la secuencia SEQ_ANOMALIASSR
     *
     * @return Integer con el valor de la secuencia SEQ_ANOMALIASSR
     */
    @Query(value = "SELECT NEXT VALUE FOR SEQ_ANOMALIASSR as id", nativeQuery = true)
    Integer getIdAnomaliaSupervision()


    @Query(value = '''SELECT ASR.IDANOMALIASSR AS idAnomalia, ASR.FECHA_REGISTRO AS fechaRegistro, 
       ASR.FOLIO AS folio, ASR.TIPO_ANOMALIA AS tipoAnomalia, ASR.OBSERVACION AS descripcionAnomalia
            FROM ANOMALIASSR ASR WHERE ASR.IDSUPERREMOTA = :idSupervisionRemota''', nativeQuery = true)
    List<Map> findAnomaliasBySupervisionRemota(@Param("idSupervisionRemota") Integer idSupervisionRemota)
}