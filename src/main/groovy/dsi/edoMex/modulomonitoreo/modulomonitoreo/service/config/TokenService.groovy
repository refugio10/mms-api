package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service

import java.time.Instant

/**
 * Contiene las funciones para la validación y generación de json web token
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Service
class TokenService {
    @Autowired
    private JwtEncoder jwtEncoder

    @Autowired
    private JwtDecoder jwtDecoder

    long expireToken = 1800 // media hora
    long expireRefreshToken = 3600 // 1 hora

    /**
     * Generación de json web token en base a una autorización
     *
     * @param clave Clave de usuario que inicio la sesión
     * @param roles Cadena con los nombres de los roles a los que tiene acceso el usuario
     * @return Cadena con el token de autenticación
     */
    String generateJwt(String clave, String roles) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .subject(clave)
                .claim("roles", roles)
                .claim("type", "Bearer")
                .expiresAt(Instant.now().plusSeconds(expireToken))
                .id(UUID.randomUUID().toString())
                .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue()
    }

    /**
     * Obtiene el clave de usuario de un json web token
     *
     * @param token Cadena con el token
     * @return Cadena con el clave de usuario
     */
    String getUserNameFromJwtToken(String token) {
        return jwtDecoder.decode(token).getSubject()
    }

    /**
     * Valida que el token sea correcto
     *
     * @param authToken Cadena con el token
     * @return [true: token válido, false: token invalido]
     */
    Boolean validateJwtToken(String authToken) {
        try {
            Jwt jwt = jwtDecoder.decode(authToken)
            return true
        } catch (Exception ignore) {
        }
        return false
    }

    /**
     * Valida que el token sea de autenticadción Bearer
     *
     * @param token Cadena con el json web token
     * @return [true: el token si contiene Bearer, false: la cadena no contiene Bearer]
     */
    Boolean isBearer(String token) {
        Jwt decoder
        String type = ""
        try {
            decoder = jwtDecoder.decode(token)
            type = decoder.getClaims().get("type").toString()
        } catch (Exception ignore) {
        }
        return type != null && type.equals("Bearer")
    }

    /**
     * Válida si el tiempo de vida del token se encuentre expirado
     *
     * @param token Cadena con el json web token
     * @return [true: el token ha expirado, false: el token aún no expira]
     */
    Boolean isExpired(String token) {
        try {
            Jwt decoder = jwtDecoder.decode(token)
            return Instant.now().isAfter(decoder.expiresAt)
        } catch (Exception ignore) {
        }
        return true
    }

    /**
     * Actualización de tiempo de vidada del token
     *
     * @param clave Clave de usuario que inicio la sesión
     * @return Cadena con un json web token
     */
    String createRefreshToken(String clave, String roles) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .subject(clave)
                .claim("type", "Refresh")
                .claim("roles", roles)
                .expiresAt(Instant.now().plusSeconds(expireRefreshToken))
                .id(UUID.randomUUID().toString())
                .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue()
    }
}