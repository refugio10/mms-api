package dsi.edoMex.modulomonitoreo.saechvv.entity.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.ModuloRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla request_map
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Entity
@Table(name = "REQUEST_MAP")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class RequestMap {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column
    String url

    @Column
    String controller

    @Column
    String accion

    @Column
    String metodo

    @Column
    Integer activo

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "modulo")
    ModuloRequest moduloRequest

    @JsonIgnore
    @ManyToMany(mappedBy = "permisosApi", fetch = FetchType.EAGER)
    Set<RoleApi> roles
}