package dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


/**
 * Contiene la información de la tabla BITACORA_APR de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Entity
@Table(name = "BITACORA_APR")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class BitacoraAPR {
    @Id
    @Column(name = "ID_BITACORA_APR")
    Integer id

    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro

    @Column(name = "PLACA_ORIGINAL")
    String placaOriginal

    @Column(name = "PLACA_FOTO")
    String placaFoto

    @Column(name = "PORCENTAJE")
    Integer porcentaje

    @Column(name = "STATUS")
    Integer estatus

    @Column(name = "EVENTO")
    Integer evento

    @Column(name = "ACIERTOS")
    Integer aciertos

    @Column(name = "TAMANO_PLACA")
    Integer tamanioPlaca

    @Column(name = "ID_VERIFICACION")
    Integer verificacion

    @Column(name = "ID_VERIFICENTRO")
    Integer verificentro

    @Column(name = "FECHA_VALIDACION")
    Date fechaValidacion

    @Column(name = "IDUSUARIO_VALIDA")
    Integer usuarioValida

    @Column(name = "IDMOTIVOAPR")
    Integer motivoAPR

    @Column(name = "OBSERVACIONES")
    String observaciones

}