package dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


/**
 * Contiene la información de la tabla VERIFICACION de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Entity
@Table(name = "VERIFICACION")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class Verificacion {

    @Id
    @Column(name = "IDVERIFICACION")
    Integer id

    @Column(name = "IDUNIQUE")
    String idUnique


    @Column(name = "FECHA_INI_VERIF")
    Date fechaInicioVerificacion

    @Column(name = "FECHA_INI_PRUEBA")
    Date fechaInicioPrueba

    @Column(name = "FECHA_FIN_PRUEBA")
    Date fechaFinPrueba

    @Column(name = "FECHA_FIN_VERIF")
    Date fechaFinVerificacion

    @Column(name = "FOLIO")
    BigInteger folio


    @Column(name = "IDINVCATALIZADOR")
    Integer invCatalizador


    @Column(name = "NO_SEMESTRE")
    Integer numeroSemestre

    @Column(name = "ANIO_SEMESTRE")
    Integer anioSemestre

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "IDVERIFICENTRO")
    Verificentro verificentro


    @Column(name = "FOLIO_ANTERIOR")
    String folioAnterior

    @Column(name = "FOLIO_MULTA")
    String folioMulta

    @Column(name = "FECHA_MULTA")
    Date fechaMulta

    @Column(name = "ESTATUS_OPERACION")
    Integer estatusOperacion

    @Column(name = "INTENTOS")
    Integer intentos

    @Column(name = "AUTORIZACION")
    Integer autorizacion

    @Column(name = "FOLIOANT_CANCELADO")
    Integer folioAnteriorCancelado

    @Column(name = "FOLIO_BAJA")
    String folioBaja

    @Column(name = "FOLIO_ALTA")
    String folioAlta

    @Column(name = "IDENTIDAD_HOLANTERIOR")
    Integer entidadHolAnterior

    @Column(name = "NO_PLACA")
    String numeroPlaca

    @Column(name = "ES_REIMPRESION")
    Integer esReimpresion

    @Column(name = "FOLIO_GAS")
    Integer folioGas

    @Column(name = "FECHA_CANCELACIONVERIF")
    Date fechaCancelacionVerificacion

    @Column(name = "ES_LIBERADO")
    Integer esLiberado

    @Column(name = "FECHA_LIBERADO")
    Date fechaLiberado

    @Column(name = "DICTAMEN")
    String dictamen

    @Column(name = "FECHA_MONITOREO")
    Date fechaMonitoreo

    @Column(name = "PRUEBA_VISUAL")
    String pruebaVisual

    @Column(name = "TIPO_TRACCION")
    Integer tipoTraccion

    @Column(name = "T_PRUEBA")
    Integer tipoPrueba

    @Column(name = "ID_PARAM_VERIFADMIN")
    Integer parametroVerificentroAdmin

    @Column(name = "ID_PARAM_PIREC")
    Integer parametroPirec

    @Column(name = "KILOMETRAJE")
    String kilometraje

    @Column(name = "OCR")
    Integer ocr

    @Column(name = "P_OBD")
    Integer pruebaOBD

    @Column(name = "APR")
    Integer apr

    @Column(name = "FACE")
    Integer fase

    @Column(name = "IDEQUIPOVERIF")
    Integer equipoVerificacion
}