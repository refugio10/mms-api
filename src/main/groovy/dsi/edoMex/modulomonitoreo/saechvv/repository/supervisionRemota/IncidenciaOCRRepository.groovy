package dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.IncidenciaOCR

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Contiene funciones relacionadas con la tabla INCIDENCIAS_OCR
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Repository
interface IncidenciaOCRRepository extends JpaRepository<IncidenciaOCR, Integer> {

    /**
     * Actualiza el estatus de las incidencias OCR por su verificación
     * @param verificacion Integer identificador de la verificación
     * @param nuevoEstatus Integer estatus a establecer para las incidencias
     * @return Integer total de registros actualizados
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE INCIDENCIAS_OCR SET ESTATUS = :nuevoEstatus WHERE IDVERIFICACION = :verificacion", nativeQuery = true)
    Integer updateEstatusIncidenciaByVerificacion(@Param("verificacion") Integer verificacion, @Param("nuevoEstatus") Integer nuevoEstatus)
}