package dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo

import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificentro
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.repository.query.Param
import org.springframework.data.jpa.repository.Query

/**
 * Contiene funciones para la administración del catálogo de verificentro
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface VerificentroRepository extends JpaRepository<Verificentro, Integer> {

    /**
     * Obtiene los datos del domicilio del verificentro
     * @param verificentro id del verificentro
     * @return Map con los datos del domicilio del verificentro
     */
    @Query(value = '''SELECT ce.IDENTIDAD AS idEntidad, ce.DESCRIPCION AS descripcionEntidad,
    cm.IDMUNICIPIO AS idMunicipio, cm.DESCRIPCION AS descripcionMunicipio,
    cv.IDCOLONIA AS idColonia,
    case when cc.IDCOLONIA is null then cv.AUX_COLONIA else cc.descripcion end AS descripcionColonia,
    cv.IDCALLE AS idCalle, cv.IDCALLE_CRUZA AS idCalleCruza,
    case when cc2.IDCALLE is null then cv.AUX_CALLE  else cc2.descripcion end AS descripcionCalle,
    case when cc3.IDCALLE is null then cv.AUX_CALLECRUZA  else cc3.descripcion end AS descripcionCalleCruza
    FROM CAT_VERIFICENTRO cv
    LEFT JOIN CAT_ENTIDAD ce ON cv.IDENTIDAD  = ce.IDENTIDAD
    LEFT JOIN CAT_MUNICIPIO cm ON cv.IDENTIDAD = cm.IDENTIDAD AND cv.IDMUNICIPIO = cm.IDMUNICIPIO
    LEFT JOIN CAT_COLONIA cc ON cv.IDENTIDAD = cc.IDENTIDAD AND cv.IDMUNICIPIO = cc.IDMUNICIPIO AND cv.IDCOLONIA = cc.IDCOLONIA
    LEFT JOIN CAT_CALLE cc2 ON cc.IDCOLONIA = cc2.IDCOLONIA AND cc2.IDCALLE = cv.IDCALLE
    LEFT JOIN CAT_CALLE cc3 ON cc.IDCOLONIA = cc3.IDCOLONIA AND cc3.IDCALLE = cv.IDCALLE_CRUZA
    where cv.IDVERIFICENTRO = :verificentro''', nativeQuery = true)
    Map findByVerificentro(@Param("verificentro") Integer verificentro)

    /**
     * Obtiene los verificentros con respecto al estado en la que se encuentran
     *
     * @param activo [1: activo, 0: inactivo]
     * @return Lista de objetos de clase Verificentro
     */
    List<Verificentro> findAllByActivo(Integer activo)


    /**
     * Obtiene el siguiente Id a asignar a la tabla de Verificentro
     *
     * @return Integer con el valor máximo de la tabla de CAT_PERFIL mas uno
     */
    @Query(value = "SELECT MAX(IDVERIFICENTRO) + 1 AS id FROM CAT_VERIFICENTRO cp ", nativeQuery = true)
    Integer getSiguienteId()

    /**
     * Muestra una lista de verificentro respecto al centro
     * @param centroNombre campo dentro
     * @return muestra una lista de acuerdo al centro
     */
    List<Verificentro> findByCentro(Integer centro);
    /**
     * Mustra una lista de verificentros respecto al rfc
     * @param centroNombre
     * @return
     */
    List<Verificentro> findByRfc(String rfc);
    /**
     * Muestra una lista de verificentros respecto al centro y el id proporcionado para actualizar
     * @param centro del verificentro
     * @param id del verificentro
     * @return
     */
    List<Verificentro> findByCentroAndIdNot(Integer centro, Integer id);
    /**
     * Muestra una lista de verificentros respecto al rfc y el id proporcionado para actualizar
     * @param rfc rfc del verificentro
     * @param id del verificentro
     * @return
     */
    List<Verificentro> findByRfcAndIdNot(String rfc, Integer id);

    /**
     * Obtiene el identificador de un verificentro aleatorio del catálogo
     *
     * @return Integer identificador del verificentro obtenido aletoriamente
     */
    @Query(value = '''SELECT TOP 1 IDVERIFICENTRO FROM CAT_VERIFICENTRO WHERE IDVERIFICENTRO <> 97 AND IDVERIFICENTRO <> 98 
    and IDVERIFICENTRO<100 AND IDVERIFICENTRO <> -1 ORDER BY NEWID()''', nativeQuery = true)
    Integer findIdCentroAleatorio()


    /**
     * Obtiene la fecha de la proxima supervisión remota de un verificentro en específico
     * @param centroId Integer identificador del verificentro
     * @return String fecha de la supervisión remota del verificentro
     */
    @Query(value = '''SELECT CONVERT(varchar, CONVERT(DATE, FECHA_PROXIMASR), 103) AS fechaProxima 
                      FROM CAT_VERIFICENTRO WHERE IDVERIFICENTRO = :idVerificentro''', nativeQuery = true)
    String findProximaFechaSupervisionByCentroId(@Param("idVerificentro") Integer idVerificentro);

    /**
     * Obtiene la información de un verificentro por su identificador
     * @param idVerificentro Integer identificador del verificentro
     * @return String fecha de la supervisión remota del verificentro
     */
    @Query(value = """
        SELECT  
            CV.IDVERIFICENTRO as idVerificentro, 
            CV.CENTRO_NOM as centroNombre,
            CV.RAZON_SOCIAL as razonSocial,
            CV.REPR_LEGAL as representante,
            CV.NO_TEL1 as telefono1,
            CV.NO_TEL2 as telefono2,
            CV.NO_TELFAX as fax,
            CV.CODIGO_POSTAL as cp,
            ISNULL(CV.E_MAIL1,'------') as email1,
            ISNULL(CV.E_MAIL2,'-----') as email2,
            ce.descripcion as entidad,
            ISNULL(cm.descripcion,'No Especificado') as municipio,
            CASE WHEN aux_colonia IS NULL THEN col.DESCRIPCION ELSE ISNULL(cv.aux_colonia,'') END as colonia,
            CASE WHEN aux_calle IS NULL THEN cca.descripcion ELSE ISNULL(cv.aux_calle,'') END as calle,
            CASE WHEN aux_callecruza IS NULL THEN cax.descripcion ELSE ISNULL(cv.aux_callecruza,'') END as calleCruza,
            cv.NO_INTERIOR as noInterior, 
            cv.NO_EXTERIOR as noExterior
        FROM CAT_VERIFICENTRO CV
        INNER JOIN cat_entidad ce ON ce.IDENTIDAD = cv.IDENTIDAD
        LEFT JOIN cat_municipio cm ON cm.idmunicipio = cv.idmunicipio AND cm.identidad = ce.identidad
        LEFT JOIN cat_colonia col ON col.IDCOLONIA = cv.IDCOLONIA
        LEFT JOIN cat_calle cca ON cca.IDCOLONIA = col.IDCOLONIA AND cca.idcalle = cv.idcalle
        LEFT JOIN cat_calle cax ON cax.IDCOLONIA = col.IDCOLONIA AND cax.idcalle = cv.idcalle_cruza
        WHERE CV.IDVERIFICENTRO = :idVerificentro
    """, nativeQuery = true)
    Map<String, Object> findVerificentroById(@Param("idVerificentro") Integer idVerificentro)

    /**
     * Obtiene el total de verificaciones de un verificentro entre un rango de fechas
     * @idVerificentro Integer identificador del verificentro
     * @fechaInicio String fecha de inicio de rango de consulta
     * @fechaFin String fecha de fin de rango de consulta
     * @return Integer total de verificaciones
     */
    @Query(value = '''SELECT COUNT(IDVERIFICACION) AS totalVerificaciones FROM VERIFICACION WHERE ESTATUS_OPERACION = 5 
            AND IDVERIFICENTRO = :idVerificentro AND CONVERT(DATE,FECHA_INI_VERIF,103) BETWEEN CONVERT(DATE,:fechaInicio,103) AND CONVERT(DATE,:fechaFin,103)''', nativeQuery = true)
    Integer findTotalVerificacionesByVerificentro(@Param("idVerificentro") Integer idVerificentro, @Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin)


    /**
     * Consulta la información de una verificación en específico
     * @param idverificacion Integer identificador de la verificación
     * @return Map mapa con la información de la verificación
     */
    @Query(value = """
    SELECT
        VE.FECHA_INI_VERIF  AS fechaVerificacion,
        PV.COMB_ORIG AS combustible,
        VE.P_OBD AS pruebaOBD, VE.FOLIO,
        CASE WHEN VE.IDTIPOHOLOGRAMA = 5 THEN -1 ELSE 1 END as resultado,
        CASE WHEN CV.aux_calle IS NULL THEN cca.descripcion ELSE COALESCE(CV.aux_calle, '') END as calle,
        CV.CENTRO_NOM as nombreCentro,
        CASE WHEN CV.aux_colonia IS NULL THEN col.DESCRIPCION ELSE COALESCE(CV.aux_colonia, '') END as colonia,
        CV.CODIGO_POSTAL as cp,
        CASE WHEN CV.aux_callecruza IS NULL THEN cax.descripcion ELSE COALESCE(CV.aux_callecruza, '') END as cruce,
        ce.descripcion as entidad,
        CV.NO_TELFAX as fax,
        CV.IDVERIFICENTRO as idCentro,
        COALESCE(CV.E_MAIL1, '------') as email1,
        COALESCE(CV.E_MAIL2, '-----') as email2,
        COALESCE(cm.descripcion, 'No Especificado') as municipio,
        CV.NO_EXTERIOR as noExt,
        CV.NO_INTERIOR as noInt,
        CV.RAZON_SOCIAL as razonSocial,
        CV.REPR_LEGAL as representante,
        CV.NO_TEL1 as telefono1,
        CV.NO_TEL2 as telefono2,
        VE.IDVERIFICACION as idVerificacion,
        VE.IDUNIQUE as ticket,
        VE.ESTATUS_OPERACION as estatusVerif,
        CL.NO_LINEA as noLinea,
        CL.IDLINEAVERIF as idLineaVerificacion,
        CASE
            WHEN CL.TIPO_LINEA = 1 THEN 'Gasolina'
            WHEN CL.TIPO_LINEA = 2 THEN 'Diesel'
            WHEN CL.TIPO_LINEA = 3 THEN 'Dual'
        END as tipoLinea,
    M.DESCRIPCION as marca,
    CMS.DESCRIPCION as submarca,
    PV.MODELO as modelo,
    PV.NO_SERIE as numeroSerie,
    PV.NO_PLACA as numeroPlaca,
    CASE VE.IDTIPOHOLOGRAMA WHEN 5 THEN 'Rechazado' ELSE 'Aprobado' END AS descripcionResultado, 
            CASE WHEN VE.TIPO_RECHAZO IS NULL THEN 'No Aplica' 
                 ELSE (SELECT RESULTADO FROM CAT_TIPORECHAZO WHERE TIPO_RECHAZO=VE.TIPO_RECHAZO) 
            END AS tipoRechazo,
    VE.IDTIPOHOLOGRAMA    
    FROM VERIFICACION VE
    INNER JOIN PADRON_VEHICULAR PV ON VE.IDPADRONVEHICULAR = PV.IDPADRONVEHICULAR
    INNER JOIN CAT_MARCA M ON PV.IDMARCA = M.IDMARCA
    INNER JOIN CAT_MARCA_SUBMARCA CMS ON PV.IDMARCA_SUBMARCA = CMS.IDMARCA_SUBMARCA
    INNER JOIN CAT_LINEAVERIF CL ON CL.IDLINEAVERIF = VE.IDLINEAVERIF
    INNER JOIN CAT_VERIFICENTRO CV ON CV.IDVERIFICENTRO = VE.IDVERIFICENTRO
    INNER JOIN cat_entidad ce ON ce.IDENTIDAD = cv.IDENTIDAD
    LEFT JOIN cat_municipio cm ON cm.idmunicipio = cv.idmunicipio AND cm.identidad = ce.identidad
    LEFT JOIN cat_colonia col ON col.IDCOLONIA = cv.IDCOLONIA
    LEFT JOIN cat_calle cca ON cca.IDCOLONIA = col.IDCOLONIA AND cca.idcalle = cv.idcalle
    LEFT JOIN cat_calle cax ON cax.IDCOLONIA = col.IDCOLONIA AND cax.idcalle = cv.idcalle_cruza
    WHERE VE.IDVERIFICACION = :idverificacion
    """, nativeQuery = true)
    Map<String, Object> findDatosVerificacionById(@Param("idverificacion") Integer idverificacion);

}