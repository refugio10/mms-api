package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias
import com.google.gson.Gson
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
//import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.GeneralRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.UsuarioSaechvvRepository
import org.apache.hc.core5.net.URIBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.StandardCopyOption

import static org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Contiene funciones que pueden ser para el uso dentro del api y no de una sección específica
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Service
class GeneralService {

//    @Autowired
//    GeneralRepository generalRepository

    @Autowired
    private UsuarioSaechvvRepository usuarioSaechvvRepository

    @Value('${modulomonitoreo.general.folder}')
    private String folderGeneral

    @Value('${modulomonitoreo.general.folderTemporal}')
    private String folderTemporal

    /**
     * Crea un mapa con el estatus para la respuesta del request
     *
     * @param estatus Objeto de clase HttpStatus con la que se le responderá al usuario
     * @return Mapa con los siguientes datos {status, code, success, mensaje}
     */
    def respuestaRequest(def estatus = UNAUTHORIZED) {
        def respuesta = [:]
        respuesta.status = estatus
        respuesta.code = estatus.value()
        respuesta.success = respuesta.code < 400
        respuesta.mensaje = ""
        return respuesta
    }

    /**
     * Modifica la cadena recibida a proceso de encriptación de SAECHVV
     *
     * @param cadena Cadena a encriptar
     * @return Cadena encriptada
     */
    String encripta(String cadena) {
        if (cadena.length() < 6 || cadena.length() > 12) return "0"
        else {
            String auxiliar = "", total = ""
            int largo = cadena.length()
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(cadena.charAt(i) * (8624 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (1357 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (5428 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (9753 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (7351 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (6547 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (4698 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (3972 + i))
            for (int i = 0; i < largo; i++) auxiliar += String.valueOf(auxiliar.charAt(i) * (2468 + i))

            int contador = 1
            int posicion = 1
            while (total.length() < 80 && posicion < auxiliar.length()) {
                total += auxiliar.charAt(posicion)
                contador++
                posicion += contador
                if (contador == 6) {
                    contador = 0
                }
            }
            return total
        }
    }

    /**
     * Obtiene el usuario que solicito una petición al sistema
     *
     * @return Objeto de clase Usuario
     */
    Usuario getUsuarioSession() {
        Authentication authentication = SecurityContextHolder.context.authentication
        return usuarioSaechvvRepository.findByClave(authentication.getName()).orElse(null)
    }

    /**
     * Verifica que un id se encuentre en una lista
     *
     * @param idConsulta Identificador a buscar
     * @param listaConsultar Lista de objetos a consultar
     * @return [true: existe el id, false: no existe el objeto]
     */
    boolean containsLista(Long idConsulta, List<Long> listaConsultar) {
        boolean contieneId = false

        for (Long it : listaConsultar) {
            contieneId = idConsulta == it
            if (contieneId) break
        }

        return contieneId
    }

    /**
     * Almacena un archivo en el servidor
     *
     * @param inputStream Datos del archivo a almacenar
     * @params rutaArchivo ruta en la que se almacenará el archivo
     * @param nombreArchivo Nombre con el que será almacenado el archivo
     * @return boolean Estado del proceso
     *
     * @author Felipe Ocampo Araujo
     * @version 1.0 26/11/2024
     */
    boolean guardaArchivo(InputStream inputStream, String rutaArchivo, String nombreArchivo){
        boolean retorno = false
        File carpeta = new File(rutaArchivo)
        if(!carpeta.isDirectory()){
            carpeta.mkdirs()
        }
        try (OutputStream  outputStream = new FileOutputStream(new File(rutaArchivo + nombreArchivo))){
            int read = 0
            byte[] bytes = new byte[1024]
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read)
            }
            retorno=true
        } catch (IOException e){
            e.printStackTrace()
        }
        return retorno
    }

    /**
     * Permite almacenar archivos en la carpeta de temporales del sistema
     * @param archivoAdjunto Documento a almacenar
     * @return Mapa con la información general del archivo
     */
    def adjuntarDocumentoTemporal(MultipartFile archivoAdjunto) {
        try {
            String nombreOriginal = archivoAdjunto.originalFilename
            String extension = nombreOriginal.split('\\.').reverse()[0].toLowerCase()

            String nombreCompuesto = System.currentTimeMillis() + "." + extension

            File carpeta = new File(folderTemporal)
            if (!carpeta.exists()) carpeta.mkdirs()

            File archivo = new File(carpeta.path, nombreCompuesto)
            archivoAdjunto.transferTo(archivo)

            return [id: nombreCompuesto, extension: extension]
        } catch (Exception e) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Mueve el archivo temporal a la carpeta destino
     * @param nombreArchivo Nombre del archivo a mover
     * @param carpetaDestino Nombre de la carpeta destino
     * @return Información general del archivo
     */
    def moverDocumentoTemporalAReal(String nombreArchivo, String carpetaDestino) {
        try {
            File carpetaTemporal = new File(folderTemporal)
            if (!carpetaTemporal.exists()) return null

            File archivoTemporal = new File(carpetaTemporal.path + File.separator + nombreArchivo)
            if (!archivoTemporal.exists()) return null

            File carpetaGeneral = new File(folderGeneral + File.separator + carpetaDestino)
            if (!carpetaGeneral.exists()) carpetaGeneral.mkdirs()

            File archivoGeneral = new File(carpetaGeneral.path + File.separator + nombreArchivo)

            Files.move(archivoTemporal.toPath(), archivoGeneral.toPath(), StandardCopyOption.REPLACE_EXISTING)

            return [ruta: archivoGeneral.toPath(), nombre: archivoTemporal.getName()]
        } catch (Exception e) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Obtiene un archivo del servidor
     * @param ruta Ruta en la que se encuentra el archivo
     * @return Mapa con los bytes del archivo
     */
    def getArchivo(String ruta) {
        try {
            File file = new File(ruta)
            if (!file.exists()) null

            return [nombre: file.name, bytes: file.getBytes()]
        } catch (Exception excepcion) {
            excepcion.printStackTrace()
        }

        return null
    }

    /**
     * Realiza una petición HTTP GET a la ruta especificada
     * @param ruta Ruta a la que se realizará la petición
     * @return true si la respuesta es OK, false en caso contrario
     */
    def realizarPeticion(String ruta){
        try {
            URL peticion = new URL(ruta)
            HttpURLConnection conexion = (HttpURLConnection) peticion.openConnection()
            conexion.setRequestMethod("GET")

            if (conexion.responseCode in [HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_NO_CONTENT]) {
                return true
            }
            conexion.disconnect()
            return null
        }catch (Exception ignored){
            return false
        }
    }

    /**
     * Permite consumir servicios api-rest de tipo POST, PUT y GET
     *
     * @param urlPeticion String url de la función a consumir
     * @param token String cadena del token para la autorización
     * @param metodo String cadena con el método HTTP
     * @param parametros Map lista de parámetros que se incorporan a la URL(query params RequestParam)
     * @param cuerpoContenido Map lista de parámetros que se incorporan al cuerpo de la solicitud (bodycontent RequestBody)
     * @param claseRetorno Class<?> especifica el tipo de clase que espera de retorno: [Map.class, LinkedHashMap.class, List.class]
     * @return Respuesta de los servicios consumidos o null si ocurre un error
     */
    def consumeServiciosRest(String urlPeticion, String token = null, String metodo, def parametros = [:], def cuerpoContenido = [:], Class<?> claseRetorno) {
        def respuesta = null

        URIBuilder constructorURI = new URIBuilder(urlPeticion)
        parametros.each { parametro ->
            constructorURI.addParameter(parametro.key, parametro.value.toString())
        }
        HttpURLConnection conexionHTTP = (HttpURLConnection) (constructorURI.build().toURL()).openConnection()

        try {
            conexionHTTP.setDoOutput(true)
            conexionHTTP.setUseCaches(false)
            conexionHTTP.setInstanceFollowRedirects(false)
            conexionHTTP.setConnectTimeout(5000)
            conexionHTTP.setRequestMethod(metodo)
            if (token != null) conexionHTTP.setRequestProperty("Authorization", "Bearer " + token)
            conexionHTTP.setRequestProperty("Content-Type", "application/json")
            conexionHTTP.setRequestProperty("Accept", "application/json")
            conexionHTTP.setRequestProperty("charset", "UTF-8")

            if (metodo.toUpperCase() in ['POST', 'PUT', 'GET'])
                conexionHTTP.outputStream.withCloseable { salida ->
                    salida << new Gson().toJson(cuerpoContenido)
                }

            respuesta = new Gson().fromJson(conexionHTTP.inputStream.text, claseRetorno)
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            conexionHTTP.disconnect()
        }
        return respuesta
    }
}