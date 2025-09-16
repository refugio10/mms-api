package dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla MESA_CONTROL de la base de SAECHVV
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Entity
@Table(name = "MESA_CONTROL")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class MesaControl {

    @Id
    @Column(name = "ID_MESA_CONTROL")
    Integer id

    @Column(name = "IDUSUARIO")
    Integer usuario

    @Column(name = "IDUNIQUE")
    String idUnique

    @Column(name = "ANOMALIA")
    String anomalia

    @Column(name = "FECHA_MONITOREO")
    Date fechaMonitoreo

    @Column(name = "VERIFICACION_ID")
    Integer verificacion

}