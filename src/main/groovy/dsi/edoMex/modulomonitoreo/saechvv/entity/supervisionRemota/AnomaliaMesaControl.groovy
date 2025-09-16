package dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.MotivoAnomalia
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificacion
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla ANOMALIAS_MESA_CONTROL de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Entity
@Table(name = "ANOMALIAS_MESA_CONTROL")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class AnomaliaMesaControl {

    @Id
    @Column(name = "ID_ANOMALIAS_MESA_CONTROL")
    Integer id

    @Column(name = "CLAVE")
    String clave

    @Column(name = "DESCRIPCION")
    String descripcion

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "IDVERIFICACION")
    Verificacion verificacion

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "IDUSUARIO")
    Usuario usuario

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ID_CAT_MOTIVO_ANOMALIA")
    MotivoAnomalia motivoAnomalia

    @Column(name = "TIPO_ACCION")
    Integer tipoAccion

    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro
}