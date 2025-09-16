package dsi.edoMex.modulomonitoreo.modulomonitoreo.config

import org.springframework.stereotype.Component

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Componente que genera llaves RSA
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Component
class RSAKeyProperties {

    RSAPublicKey publicKey = null
    RSAPrivateKey privateKey = null

    /**
     * Constructor de la clase
     * Inicializa las llaves de RSA
     */
    RSAKeyProperties() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            KeyPair keyPair = keyPairGenerator.generateKeyPair()

            publicKey = (RSAPublicKey) keyPair.getPublic()
            privateKey = (RSAPrivateKey) keyPair.getPrivate()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}