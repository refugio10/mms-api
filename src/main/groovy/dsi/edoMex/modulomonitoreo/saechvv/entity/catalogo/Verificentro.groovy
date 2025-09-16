package dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla cat_verificentro de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Entity
@Table(name = "CAT_VERIFICENTRO")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class Verificentro {

    @Id
    @Column(name = "IDVERIFICENTRO")
    Integer id

    @Column(name = "CENTRO")
    Integer centro

    @Column(name = "CENTRO_NOM")
    String centroNombre

    @Column(name = "RAZON_SOCIAL")
    String razonSocial

    @Column(name = "REPR_LEGAL")
    String representanteLegal

    @Column(name = "NO_TEL1")
    String numeroTelefono

    @Column(name = "NO_TEL2")
    String numeroTelefonoSecundario

    @Column(name = "NO_TELFAX")
    String numeroFax

    @Column(name = "NO_TELMODEM")
    String numeroTelefonoModem

    @Column(name = "IDCALLE")
    Integer calle

    @Column(name = "IDCALLE_CRUZA")
    Integer calleCruza

    @Column(name = "NO_INTERIOR")
    Integer numeroInterior

    @Column(name = "NO_EXTERIOR")
    Integer numeroExterior

    @Column(name = "CODIGO_POSTAL")
    Integer codigoPostal

    @Column(name = "E_MAIL1")
    String correoElectronico

    @Column(name = "E_MAIL2")
    String correoElectronicoSecundario

    @Column(name = "PORCENT_RECHAZO")
    Integer porcentajeRechazo

    @Column(name = "FECHA_INICIO_OPER")
    Date fechaInicioOperacion

    @Column(name = "FECHA_SUSP_OPER")
    Date fechaSuspendeOperacion

    @Column(name = "FECHA_REINICIO_OPER")
    Date fechaReinicioOperacion

    @Column(name = "ES_ACTIVO")
    Integer activo

    @Column(name = "IDENTIDAD")
    Integer entidad

    @Column(name = "IDMUNICIPIO")
    Integer municipio

    @Column(name = "IDCOLONIA")
    Integer colonia

    @Column(name = "AUX_COLONIA")
    String auxiliarColonia

    @Column(name = "AUX_CALLE")
    String auxiliarCalle

    @Column(name = "AUX_CALLECRUZA")
    String auxiliarCalleCruza

    @Column(name = "ENTRADA")
    Integer entrada

    @Column(name = "SALIDA")
    Integer salida

    @Column(name = "RFC")
    String rfc

    @Column(name = "FECHA_PROXIMASR")
    Date fechaProximaSupervision


    @Column(name = "IDUSUARIO")
    Integer usuario

    @Column(name = "PUERTA_ENLACE")
    String puertaEnlace

    @Column(name = "LATITUD")
    String latitud

    @Column(name = "LONGITUD")
    String longitud

    @Column(name = "IP_PUBLICA")
    String ipPublica

    @Column(name = "LIBERADO")
    Integer liberado

    @Column(name = "TIEMPO_GRACIA")
    Integer tipoGracia
}