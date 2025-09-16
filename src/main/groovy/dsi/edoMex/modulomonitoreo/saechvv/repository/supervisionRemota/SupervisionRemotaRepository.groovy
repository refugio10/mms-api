package dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.SupervisionRemota
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Contiene funciones para la administración de supervisiones remotas
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface SupervisionRemotaRepository extends JpaRepository<SupervisionRemota, Integer>{

    /**
     * Obtiene el siguiente Id de la secuencia SEQ_SUPER_REMOTA
     *
     * @return Integer con el valor de la secuencia SEQ_SUPER_REMOTA
     */
    @Query(value = "SELECT NEXT VALUE FOR SEQ_SUPER_REMOTA as maximo ", nativeQuery = true)
    Integer getSiguienteId()

    /**
     * Busca una supervisión remota por su folio
     * @param folioSupervision String con el folio de la supervisión remota
     * @return Optional<SupervisionRemota> con la supervisión remota encontrada
     */
    Optional<SupervisionRemota> findByFolio(String folioSupervision)

}