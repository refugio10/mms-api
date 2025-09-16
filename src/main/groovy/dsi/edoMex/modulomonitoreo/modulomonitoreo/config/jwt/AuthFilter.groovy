package dsi.edoMex.modulomonitoreo.modulomonitoreo.config.jwt

import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.config.CustomUserDetailsService
import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.config.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter


/**
 * Filtro de solicitudes HTTP el cual solo se invocará una sola vez por solicitud
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
class AuthFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService

    @Autowired
    private CustomUserDetailsService customUserDetailsService

    /**
     * @param request Proporciona la información de las solicitudes HTTP
     * @param response Proporciona funcionalidad específica de HTTP para enviar una respuesta
     * @param filterChain Cadena de invocación de una solicitud filtrada
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request)
            if (jwt != null && tokenService.validateJwtToken(jwt) && tokenService.isBearer(jwt) && !tokenService.isExpired(jwt)) {
                String username = tokenService.getUserNameFromJwtToken(jwt)
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                )
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
                SecurityContextHolder.getContext().setAuthentication(authentication)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        filterChain.doFilter(request, response)
    }

    /**
     * Obtiene el  token de autorización de la solicitud HTTP
     *
     * @param request Proporciona la información de las solicitudes HTTP
     * @return Cadena con el token de autorización
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization")
        return (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer "))
                ? headerAuth.substring(7)
                : null
    }
}