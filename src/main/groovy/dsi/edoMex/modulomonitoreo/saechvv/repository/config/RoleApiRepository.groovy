package dsi.edoMex.modulomonitoreo.saechvv.repository.config

import dsi.edoMex.modulomonitoreo.saechvv.entity.config.RoleApi
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Contiene funciones para la administración de role api
 *
 * @author lorenav
 * @version 1.0 14/11/2024
 */
@Repository
interface RoleApiRepository extends JpaRepository<RoleApi, Long> {

    /**
     * Obtiene un objeto de RolApi a partir del nombre del rol
     * @param nombre Nombre del rol
     * @return Objeto de clase Optional de tipo clase RoleApi
     */
    Optional<RoleApi> findByNombre(String nombre)

    /**
     * Obtiene los Roles con respecto al estado en la que se encuentran
     *
     * @param activo [1: activo, 0: inactivo]
     * @return Lista de objetos de clase RoleApi
     */
    List<RoleApi> findAllByActivo(Integer activo)
}