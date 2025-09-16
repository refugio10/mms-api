package dsi.edoMex.modulomonitoreo.saechvv.repository.verificacion

import dsi.edoMex.modulomonitoreo.saechvv.entity.verificacion.DatoSegundoSegundo
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Contiene funciones relacionadas con la tabla SS_TEMPORAL
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
interface DatoSegundoSegundoRepository extends JpaRepository<DatoSegundoSegundo, Integer>{

    /**
     * Obtiene un objeto de DatoSegundoSegundo con la información de segundo a segundo de una verificación
     * @param idVerificacion Integer identificador de la verificación de la cual se quiere obtener la información
     * @return Optional<DatoSegundoSegundo> Lista de objetos con la información de segundo a segundo de una verificación
     */
    Optional<DatoSegundoSegundo> findFirstByVerificacionOrderByIdDesc(Integer idVerificacion)
}