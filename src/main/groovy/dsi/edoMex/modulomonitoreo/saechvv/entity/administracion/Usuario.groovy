package dsi.edoMex.modulomonitoreo.saechvv.entity.administracion

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dsi.edoMex.modulomonitoreo.saechvv.entity.config.UsuarioRoleApi
//import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.AccesoTablaMaestra
//import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.TipoUsuario
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla usuario de la base de SAECHVV
 *
 * @author lorenav
 * @version 1.0 12/11/2024
 */
@Entity
@Table(name = "USUARIO")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class Usuario {
    @Id
    @Column(name = "IDUSUARIO", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUsuario")
    @SequenceGenerator(name = "seqUsuario", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    Integer id

    @Column(name = "NOMBRE", nullable = false)
    String nombre

    @Column(name = "APELLIDOPATERNO", nullable = false)
    String apellidoPaterno

    @Column(name = "APELLIDOMATERNO", nullable = false)
    String apellidoMaterno

    @Column(name = "CLAVEUSER", nullable = false)
    String clave

    @JsonIgnore
    @Column(name = "PASSWORD", nullable = false)
    String password

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "IDPERFIL", nullable = false)
//    Perfil perfil

    @Column(name = "ES_ACTIVO", nullable = false)
    Integer activo = 3

    @Column(name = "PATH_FOTO")
    String rutaFotografia

//    @Enumerated(EnumType.ORDINAL)
//    @Column(name = "TIPO_USUARIO")
//    TipoUsuario tipoUsuario = TipoUsuario.UNDEFINED

    @Column(name = "ID_TIPO_USUARIO")
    Integer idTipoUsuario = 0


    @Column(name = "VALIDA_BIOMETRICO")
    Integer validaBiometrico = 0 // [0: no, 1: si]

    @JsonIgnore
    @Column(name = "IDUSUARIO_REGISTRA")
    Integer idUsuarioRegistra

    @JsonIgnore
    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro = new Date()

    @JsonIgnore
    @Column(name = "FECHA_MODIFICACION")
    Date fechaModificacion = null


    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    Set<UsuarioRoleApi> usuarioRolesApi

    @Column(name = "PROCEDENCIA")
    Integer procedencia = 1  // 1: saech, 2: pirec, 3:control de folios


}