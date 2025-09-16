package dsi.edoMex.modulomonitoreo.saechvv.entity.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla USUARIO_ROLE_API de la base de SAECHVV
 *
 * @author lorenav
 * @version 1.0 26/11/2024
 */
@Entity
@Table(name = "USUARIO_ROLE_API")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class UsuarioRoleApi {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_ID")
    RoleApi roleApi

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID")
    Usuario usuario
}