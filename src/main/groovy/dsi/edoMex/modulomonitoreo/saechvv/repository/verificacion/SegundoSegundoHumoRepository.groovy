package dsi.edoMex.modulomonitoreo.saechvv.repository.verificacion

import dsi.edoMex.modulomonitoreo.saechvv.entity.verificacion.SegundoSegundoHumo
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Contiene funciones relacionadas con la tabla SS_HUMO
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Repository
interface SegundoSegundoHumoRepository extends JpaRepository<SegundoSegundoHumo, Integer> {

    /**
     * Obtiene una lista de objetos de SegundoSegundoHumo con la información de segundo a segundo de humo
     * @param verificacion objeto verificación de la cual se quiere obtener la información
     * @return Optional<SegundoSegundoHumo> objeto con la información de segundo a segundo de humo en una verificación
     */
    Optional<SegundoSegundoHumo> findFirstByVerificacionOrderByIdDesc(Verificacion verificacion)
}