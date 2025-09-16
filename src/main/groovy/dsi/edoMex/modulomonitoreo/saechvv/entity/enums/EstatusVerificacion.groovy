package dsi.edoMex.modulomonitoreo.saechvv.entity.enums

/**
 * Contiene los valores esperados para el estatus de la verificación
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
enum EstatusVerificacion {
    DESCONOCIDO(0, "Desconocido"),
    EN_ESPERA_PRUEBA(1, "En espera de prueba"),
    EN_PRUEBA(2, "En prueba"),
    EN_EVALUACION(3, "En evaluación"),
    EN_IMPRESION(4, "En impresión"),
    CONCLUIDA_CON_FOLIO(5, "Concluida con folio"),
    CANCELADA(6, "Cancelada"),
    INSPECCION_VISUAL(11, "En inspección visual")

    final Integer id
    final String descripcion

    /**
     * Constructor de la clase
     * @param id Identificador del enum
     * @param descripcion Cadena con la significado del enum
     */
    EstatusVerificacion(Integer id, String descripcion){
        this.id = id
        this.descripcion = descripcion
    }

    /**
     * Método que obtiene EstatusVerificacion respecto al id
     *
     * @param id int Identificador de la clase enum EstatusVerificacion
     *
     * @return EstatusVerificacion Objeto de la clase enum EstatusVerificacion
     */
    static EstatusVerificacion get(Integer id) {
        values().find { it.id == id } ?: DESCONOCIDO
    }

    /**
     * Se sobreescribe el método toString para la clase EstatusVerificacion
     *
     * @return String descripción de la clase enum EstatusVerificacion
     */
    @Override
    String toString(){
        return this.descripcion
    }

    /**
     * Método que retorna la lista de EstatusVerificacion, excluyendo la constante DESCONOCIDO
     *
     * @return List&lt;EstatusVerificacion&gt; Lista de constantes de la clase enum EstatusVerificacion
     */
    static List<EstatusVerificacion> getAll(){
        values().findAll{it != DESCONOCIDO}
    }

    /**
     * Obtiene el mapa de propiedades del enum
     *
     * @param id Identificador de la clase enum
     * @return Mapa de propiedades del enum
     */
    static def getProperties(Integer id) {
        EstatusVerificacion estatusVerificacion = get(id)
        return [id: estatusVerificacion?.id, descripcion: estatusVerificacion?.descripcion]
    }

}