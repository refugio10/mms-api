package dsi.edoMex.modulomonitoreo.saechvv.repository.config

import dsi.edoMex.modulomonitoreo.saechvv.entity.config.RequestMap
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.ModuloRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Contiene funciones para la administración de request map
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Repository
interface RequestMapRepository extends JpaRepository<RequestMap, Long> {

    /**
     * Obtiene una RequestMap a partir de las propiedades principales
     * @param url Url del request
     * @param controller Nombre del controller que contiene el requestMap
     * @param accion Nombre del método o acción del requestMap
     * @param metodo Nombre del tipo de método del requestMap
     * @param activo [1: activo, 0: inactivo]
     * @return Objeto de clase Optional de tipo clase RequestMap
     */
    Optional<RequestMap> findByUrlAndControllerAndAccionAndMetodoAndModuloRequestAndActivo(String url, String controller, String accion, String metodo, ModuloRequest moduloRequest, Integer activo)


    /**
     * Obtiene un RequestMap dependiendo de los roles y características de la petición
     *
     * @param roles Lista de nombres de roles
     * @param url Cadena de la petición
     * @param metodo Cadena del método que realizó la petición
     * @param modulo Identificador del módulo en el requestMap
     * @return Objeto de clase Optional de tipo clase RequestMap
     */
    @Query(value = '''SELECT rm.*
    FROM ROLE_API ra
    INNER JOIN REQUEST_MAP_ROLE_API rmra ON ra.ID = rmra.ROLE_ID
    INNER JOIN REQUEST_MAP rm ON rm.ID = rmra.REQUEST_ID
    WHERE rm.ACTIVO = 1 AND ra.NOMBRE IN (:roles)
    AND rm.URL = :url AND rm.METODO = :metodo AND rm.MODULO = :modulo ''', nativeQuery = true)
    Optional<RequestMap> findByRoleAndUrlAndMetodoAndModuloRequest(@Param("roles") List<String> roles, @Param("url") String url, @Param("metodo") String metodo, @Param("modulo") Integer modulo)

    /**
     * Obtiene los RequestMap asociados a un módulo específico
     * @param moduloRequest Objeto de enum ModuloRequest
     * @param activo [1: activo, 0: inactivo]
     * @return Lista de RequestMap
     */
    List<RequestMap> findAllByModuloRequestAndActivo(ModuloRequest moduloRequest, Integer activo)
}