package dsi.edoMex.modulomonitoreo.saechvv.entity.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla request_map
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Entity
@Table(name = "ROLE_API")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class RoleApi {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false)
    String nombre

    @Column(nullable = false)
    String descripcion

    @Column(nullable = false)
    Integer activo = 1

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "REQUEST_MAP_ROLE_API",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "REQUEST_ID"))
    Set<RequestMap> permisosApi

    @JsonIgnore
    @OneToMany(mappedBy = "roleApi", fetch = FetchType.LAZY)
    Set<UsuarioRoleApi> usuariosRoleApi
}