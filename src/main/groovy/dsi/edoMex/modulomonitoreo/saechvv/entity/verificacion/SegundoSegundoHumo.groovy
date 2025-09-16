package dsi.edoMex.modulomonitoreo.saechvv.entity.verificacion

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificacion
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla SS_HUMOS de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Entity
@Table(name = "SS_HUMOS")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class SegundoSegundoHumo {

    @Id
    @Column(name = "ID_SS_HUMOS")
    Integer id

    @ManyToOne
    @JoinColumn(name = "ID_VERIFICACION")
    Verificacion verificacion

    @Column(name = "SS")
    String datoSegundoSegundo

    @Column(name = "ESTADO")
    Integer estado

    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro

}