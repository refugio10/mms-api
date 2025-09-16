package dsi.edoMex.modulomonitoreo.modulomonitoreo.config.jwt

import dsi.edoMex.modulomonitoreo.modulomonitoreo.config.RequestSecured
import dsi.edoMex.modulomonitoreo.saechvv.entity.config.RequestMap
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.ModuloRequest
import dsi.edoMex.modulomonitoreo.saechvv.repository.config.RequestMapRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component

import java.util.function.Supplier

/**
 * Determina si la solicitud es autenticado para permitir el acceso a algún recurso
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Component
class PocClosureVoter implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    private RequestMapRepository requestMapRepository

    private RequestSecured requestSecured

    /**
     * Constructor de PocClosureVoter, inicializa las variables globales
     * @param requestSecured Objeto de clase RequestSecured que contiene las urls
     * a los que se tiene acceso por token autenticado
     */
    PocClosureVoter(RequestSecured requestSecured) {
        this.requestSecured = requestSecured
    }

    /**
     * Verifica que la autorización encontrada de una solicitud contenga acceso al recurso solicitado
     *
     * @param authentication Autenticación del usuario
     * @param object Obtiene el contexto de la autorización de una conexión específicada
     * @return Concede o deniega el acceso a la solicitud enviada
     */
    @Override
    AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        String requestMethod = object.request.method.toString()
        String urlRequested = object.request.requestURI.split("/mms-api").last()
        Collection<GrantedAuthority> userRoles = (Collection<GrantedAuthority>) authentication.get().authorities
        List<String> rolesToken = userRoles.stream().map(it -> it.getAuthority()).toList()
        rolesToken = rolesToken.get(0).split(",").toList()

        if (rolesToken.contains("ROLE_ADMIN_MONITOREO")) return new AuthorizationDecision(true)

        def urlIndex = urlRequested?.split("/")
        if (urlIndex.size() > 0 && urlIndex.last()?.isNumber())
            urlRequested = urlRequested.replace(urlIndex.last(), "{id}")

        for (def it : requestSecured.requestPermit)
            if (it.key == urlRequested && it.value == requestMethod) return new AuthorizationDecision(true)

        RequestMap requestMap = requestMapRepository
                .findByRoleAndUrlAndMetodoAndModuloRequest(rolesToken, urlRequested, requestMethod, ModuloRequest.MODULO_MONITOREO_API.id)
                .orElse(new RequestMap())

        boolean accessValidate = (requestMap.getId() != null && requestMap.getId() != 0L)
        return new AuthorizationDecision(accessValidate)
    }
}