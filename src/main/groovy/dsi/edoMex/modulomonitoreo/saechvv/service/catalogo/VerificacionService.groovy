//package dsi.edoMex.modulomonitoreo.saechvv.service.catalogo
//
//import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.VerificacionRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
//import static org.springframework.http.HttpStatus.*
//
//@Service
//class VerificacionService {
//    @Autowired
//    VerificacionRepository verificacionRepository
//
//    /**
//     * Obtiene la lista de documentos de un vehículo asociados a una verificación
//     * @param idVerificacion Integer identificador de la verificación
//     * @return List<Map> lista de documentos asociados a la verificación
//     */
//    def getDocumentosVerificacion(Integer idVerificacion) {
//        if (!idVerificacion) {
//            return [error: true, message: "No se encontró la verificación a consultar",status: NOT_FOUND, statusCode: NOT_FOUND.value()]
//        }
//
//        def documentos = verificacionRepository.getDocumentosVerificacion(idVerificacion)
//        if (!documentos || documentos.isEmpty()) {
//            return [error: true, message: "No se encontraron documentos asociados a la verificación", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
//        }
//        return [documentos: documentos, message: "Documentos encontrados", status: OK, statusCode: OK.value()]
//    }
//}