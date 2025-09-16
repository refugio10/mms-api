package dsi.edoMex.modulomonitoreo.saechvv.entity.enums

/**
 * Contiene los valores esperados para los tipos de módulos que almacenara request
 *
 * @author lorenav
 * @version 1.0 26/11/2024
 */
enum ModuloRequest {
    UNDEFINED(0, "Indefinido"),
    MODULO_MONITOREO_API(3,"modulo-monitoreo-api")

    final Integer id
    final String descripcion

    /**
     * Constructor de la clase
     * @param id Identificador del enum
     * @param descripcion Cadena con la significado del enum
     */
    ModuloRequest(Integer id, String descripcion) {
        this.id = id
        this.descripcion = descripcion
    }

    /**
     * Obtiene el módulo al que pertenece el request
     *
     * @param id Identificador de la clase enum
     * @return Enum de tipo de usuario
     */
    static ModuloRequest get(Integer id) {
        for (ModuloRequest moduloRequst : values()) {
            if (moduloRequst.id.equals(id)) return moduloRequst
        }
        return UNDEFINED
    }

    /**
     * Obtiene el mapa de propiedades del enum
     *
     * @param id Identificador de la clase enum
     * @return Mapa de propiedades del enum
     */
    static def getProperties(Integer id) {
        ModuloRequest moduloRequest = get(id)
        return [id: moduloRequest.id, name: moduloRequest.name(), descripcion: moduloRequest.descripcion]
    }

    /**
     * Obtiene todos los registros del enum
     * @return Lista de mapas del enum
     */
    static List<ModuloRequest> getAll() {
        def listaEnum = []

        for (ModuloRequest moduloRequest : values())
            if (moduloRequest != UNDEFINED)
                listaEnum.add([id: moduloRequest.id, name: moduloRequest.name(), descripcion: moduloRequest.descripcion])

        return listaEnum
    }
}