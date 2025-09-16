package dsi.edoMex.modulomonitoreo.saechvv.service.config

import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.Criteria
import dsi.edoMex.modulomonitoreo.saechvv.entity.config.RoleApi
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.ModuloRequest
import dsi.edoMex.modulomonitoreo.saechvv.repository.config.RoleApiRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Contiene las funciones para la administración de role_api de la base de SAECHVV
 *
 * @author lorenav
 * @version 1.0 26/11/2024
 */
@Service
class RoleApiService {

    @Autowired
    private Criteria criteria

    @Autowired
    private RoleApiRepository roleApiRepository

    /**
     * Obtiene un rolApi específico
     *
     * @param id Identificador del rol
     * @return Objeto de clase RoleApi
     */
    def getRoleApi(Long id) {
        return roleApiRepository.findById(id).orElse(null)
    }

    /**
     * Obtiene lista de usuarios con respecto a los parámetros enviados del request
     *
     * @param parametros Proporciona la información contenida en los parámetros de las solicitudes HTTP
     * @return Lista de objetos de usuario
     */
    def consulta(Map<String, Object> parametros) {
        return criteria.list(RoleApi.class, parametros, { filters, roleApi, builder, query ->
            roleApi.join('permisosApi').with { requestMap ->
                filters.push(builder.equal(requestMap.get('activo'), 1 as Integer))

                if (parametros.moduloRequest && (parametros.moduloRequest as Integer) > -1)
                    filters.push(builder.equal(requestMap.get('moduloRequest'), ModuloRequest.get(parametros.moduloRequest as Integer)))
            }

            if (parametros.usuario) {
                roleApi.join('usuariosRoleApi').with { usuarioRoleApi ->
                    usuarioRoleApi.join('usuario').with { usuario ->
                        filters.push(builder.equal(usuario.get('id'), parametros.usuario as Integer))
                    }
                }
            }

            if (parametros.activo && (parametros.activo as Integer) > -1)
                filters.push(builder.equal(roleApi.get('activo'), parametros.activo as Integer))

            if (parametros.nombre)
                filters.push(builder.like(roleApi.get('nombre'), "%${parametros.nombre}%"))

            query.distinct(true)
        }, { roleApi, builder, query ->
            query.orderBy(builder.asc(roleApi.get('nombre')))
        })
    }
}