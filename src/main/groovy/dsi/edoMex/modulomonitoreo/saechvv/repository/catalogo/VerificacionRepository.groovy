package dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo

import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificacion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Contiene funciones para la administración del catálogo de verificacion
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Repository
interface VerificacionRepository extends JpaRepository<Verificacion, Integer> {

    /**
     * Consulta la información de las verificaciones de los últimos 7 días de un verificentro
     * @param idVerificentro Integer identificador del verificentro
     * @param fechaInicio String fecha inicio del rango de consulta de verificaciones
     * @param fechaFin String fecha fin del rango de consulta de verificaciones
     * @return  List<Map> Lista de verificaciones del verificentro en los últimos 7 días
     */
    @Query(value = """
        SELECT DISTINCT 
            CASE WHEN VE.T_PRUEBA IS NULL THEN 'Dinamica' END,
            CASE VE.T_PRUEBA WHEN 0 THEN 'Estatica' ELSE 'Dinamica' END AS TIPO_PRUEBA, 
            ISNULL(CTC.descripcion,'Gasolina') AS tipocombus, 
            CONVERT(VARCHAR, VE.FECHA_INI_VERIF, 103) AS FechaVerificacion, 
            UPPER(pv.no_placa) AS placa,
            UPPER(pv.no_serie) AS serie,
            pv.modelo, 
            cm.descripcion AS Motivo, 
            VE.idtipoholograma AS idholofin, 
            VE.idtipoholograma_ini AS idholoini, 
            cth1.descripcion AS HologramaIni,
            cth.descripcion AS HologramaObt, 
            VE.folio,
            CASE VE.idtipoholograma WHEN 5 THEN 'Rechazado' ELSE 'Aprobado' END AS Resultado, 
            CASE WHEN VE.TIPO_RECHAZO IS NULL THEN 'No Aplica' 
                 ELSE (SELECT RESULTADO FROM CAT_TIPORECHAZO WHERE TIPO_RECHAZO=VE.TIPO_RECHAZO) 
            END AS TIPO_RECHAZO,
            PV.CILINDROS, 
            CONCAT(CONVERT(VARCHAR, FECHA_INI_VERIF, 103), ' ', CONVERT(VARCHAR, FECHA_INI_VERIF, 24)) AS finiv, 
            CONCAT(CONVERT(VARCHAR, FECHA_INI_PRUEBA, 103), ' ', CONVERT(VARCHAR, FECHA_INI_PRUEBA, 24)) AS finip, 
            CONCAT(CONVERT(VARCHAR, FECHA_FIN_PRUEBA, 103), ' ', CONVERT(VARCHAR, FECHA_FIN_PRUEBA, 24)) AS ffinp, 
            CONCAT(CONVERT(VARCHAR, FECHA_FIN_VERIF, 103), ' ', CONVERT(VARCHAR, FECHA_FIN_VERIF, 24)) AS ffinv, 
            (SELECT no_linea FROM cat_lineaverif WHERE idlineaverif = ve.idlineaverif) AS nolineaverificancia, 
            ISNULL(PPC.CO, 0) AS EVALPIRCO, 
            ISNULL(PPC.O2, 0) AS EVALPIRO2, 
            ISNULL(PPC.CO2, 0) AS EVALPIRCO2, 
            ISNULL(PVA.CAL, 0) AS EVALCAL, 
            ISNULL(PVA.HC, 0) AS EVALHC, 
            ISNULL(PVA.CO, 0) AS EVALCO, 
            ISNULL(PVA.NOX, 0) AS EVALNOX, 
            ISNULL(PVA.O2, 0) AS EVALO2, 
            ISNULL(PVA.LAMDA, 0) AS EVAL_LAMDA, 
            ISNULL(PVA.CO_CO2_INI, 0) AS EVALCOCO2INI, 
            ISNULL(PVA.CO_CO2_FIN, 0) AS EVALCOCO2FIN, 
            ISNULL(PVA.O2_EST, 0) AS EVALO2EST, 
            ISNULL(PVA.LAMDA_EST, 0) AS EVAL_LAMDAEST, 
            pv.POT_5024, 
            pv.POT_2540, 
            ISNULL(DVE.THP_HUMO, 0) AS THP_HUMO, 
            ISNULL(DVE.HC_5024_B, 0) AS HC_5024_B, 
            ISNULL(DVE.CO_5024_B, 0) AS CO_5024_B, 
            ISNULL(DVE.CO2_5024_B, 0) AS CO2_5024_B, 
            ISNULL(DVE.O2_5024_B, 0) AS O2_5024_B, 
            ISNULL(DVE.NOX_5024_B, 0) AS NOX_5024_B, 
            ISNULL(DVE.LAMDA_5024, 0) AS LAMDA_5024, 
            ISNULL(DVE.TEMP_5024, 0) AS TEMP_5024, 
            ISNULL(DVE.HR_5024, 0) AS HR_5024, 
            ISNULL(DVE.PSI_5024, 0) AS PSI_5024, 
            ISNULL(DVE.FCNOX_5024, 0) AS FCNOX_5024, 
            ISNULL(DVE.FCDIL_5024, 0) AS FCDIL_5024, 
            ISNULL(DVE.RPM_5024, 0) AS RPM_5024, 
            ISNULL(DVE.KPH_5024, 0) AS KPH_5024, 
            ISNULL(DVE.THP_5024, 0) AS THP_5024, 
            ISNULL(DVE.VOLTS_5024, 0) AS VOLTS_5024, 
            ISNULL(DVE.HC_5024, 0) AS HC_5024, 
            ISNULL(DVE.CO_5024, 0) AS CO_5024, 
            ISNULL(DVE.CO2_5024, 0) AS CO2_5024, 
            ISNULL(DVE.COCO2_5024, 0) AS COCO2_5024, 
            ISNULL(DVE.O2_5024, 0) AS O2_5024, 
            ISNULL(DVE.NO_5024, 0) AS NO_5024, 
            ISNULL(DVE.EFIC_5024, 0) AS EFIC_5024, 
            ISNULL(DVE.HC_2540_B, 0) AS HC_2540_B, 
            ISNULL(DVE.CO_2540_B, 0) AS CO_2540_B, 
            ISNULL(DVE.CO2_2540_B, 0) AS CO2_2540_B, 
            ISNULL(DVE.O2_2540_B, 0) AS O2_2540_B, 
            ISNULL(DVE.NOX_2540_B, 0) AS NOX_2540_B, 
            ISNULL(DVE.LAMDA_2540, 0) AS LAMDA_2540, 
            ISNULL(DVE.TEMP_2540, 0) AS TEMP_2540, 
            ISNULL(DVE.HR_2540, 0) AS HR_2540, 
            ISNULL(DVE.PSI_2540, 0) AS PSI_2540, 
            ISNULL(DVE.FCNOX_2540, 0) AS FCNOX_2540, 
            ISNULL(DVE.FCDIL_2540, 0) AS FCDIL_2540, 
            ISNULL(DVE.RPM_2540, 0) AS RPM_2540, 
            ISNULL(DVE.KPH_2540, 0) AS KPH_2540, 
            ISNULL(DVE.THP_2540, 0) AS THP_2540, 
            ISNULL(DVE.VOLTS_2540, 0) AS VOLTS_2540, 
            ISNULL(DVE.HC_2540, 0) AS HC_2540, 
            ISNULL(DVE.CO_2540, 0) AS CO_2540, 
            ISNULL(DVE.CO2_2540, 0) AS CO2_2540, 
            ISNULL(DVE.COCO2_2540, 0) AS COCO2_2540, 
            ISNULL(DVE.O2_2540, 0) AS O2_2540, 
            ISNULL(DVE.NO_2540, 0) AS NO_2540, 
            ISNULL(DVE.EFIC_2540, 0) AS EFIC_2540, 
            ISNULL(DVE.OPACIDAD, 0) AS OPACIDAD, 
            ISNULL(DVE.TEMP_MOT, 0) AS TEMP_MOT, 
            ISNULL(DVE.VEL_GOB, 0) AS VEL_GOB, 
            ISNULL(DVE.POTMAX_RPM, 0) AS POTMAX_RPM, 
            ISNULL(DVE.TEM_GAS, 0) AS TEM_GAS, 
            ISNULL(DVE.TEM_CAM, 0) AS TEM_CAM, 
            ISNULL(DVE.PRES_GAS, 0) AS PRES_GAS, 
            ISNULL(DVE.GOBERNADOR, 0) AS GOBERNADOR, 
            ISNULL(DVE.C_RECHAZO, 0) AS C_RECHAZO, 
            ISNULL(DVE.PROTOCOLO_APLICADO, 0) AS PROTOCOLO_APLICADO, 
            ISNULL(DVE.POT_MAXRPM, 0) AS POT_MAXRPM, 
            ISNULL(DVE.NUMERO_SERIE, 0) AS NUMERO_SERIE, 
            ISNULL(DVE.CAL, 0) AS CAL,
            VE.FECHA_INI_VERIF, 
            ctv.centro_nom, 
            ctv.razon_social, 
            ve.IDVERIFICACION, 
            VE.idmotivo AS idmotivoverif, 
            ISNULL(pv.IDTABLAMAESTRA, 0) AS IDTABLAMAESTRA, 
            ISNULL(ve.KILOMETRAJE, 0) AS kilometraje, 
            ISNULL(pv.protocolo, 0) AS protocolo,
            ISNULL(ve.p_obd, 0) AS p_obd 
        FROM verificacion VE 
        INNER JOIN padron_vehicular pv ON pv.idpadronvehicular = VE.idpadronvehicular 
        LEFT JOIN detverifica DVE ON DVE.iddetverificacion = (
            SELECT TOP 1 iddetverificacion FROM detverifica WHERE idverificacion = VE.idverificacion
        )  
        INNER JOIN cat_motivo cm ON cm.idmotivo = VE.idmotivo 
        INNER JOIN cat_tipoholograma cth ON cth.idtipoholograma = VE.idtipoholograma 
        INNER JOIN cat_tipoholograma cth1 ON cth1.idtipoholograma = VE.idtipoholograma_ini 
        INNER JOIN CAT_TIPOCOMBUS CTC ON pv.COMB_ORIG = CTC.IDTIPOCOMBUS 
        LEFT JOIN PARAM_VERIFADM PVA ON PVA.IDPARAMVERIFADM = VE.ID_PARAM_VERIFADMIN 
        LEFT JOIN PARAM_PIREC PPC ON PPC.IDPARAMPIREC = VE.ID_PARAM_PIREC 
        INNER JOIN cat_verificentro ctv ON ve.idverificentro = ctv.idverificentro 
        WHERE VE.face = 1 
          AND VE.estatus_operacion = 11  
          AND VE.FOLIO NOT IN (99999, -999999999, 1, 0)  
          AND VE.IDVERIFICENTRO = :idVerificentro 
          AND CONVERT(DATE, VE.FECHA_INI_VERIF, 103) BETWEEN CONVERT(DATE, :fechaInicio, 103) AND CONVERT(DATE, :fechaFin, 103)
        ORDER BY ctv.centro_nom, VE.IDVERIFICACION
    """, nativeQuery = true)
    List<Map<String, Object>> findVerificacionesByVerificentro(@Param("idVerificentro") Integer idVerificentro, @Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin)


    /**
     * Consulta el identificador de un verificentro aleatorio
     * @return Integer identificador del verificentro aleatorio
     */
    @Query(value = """
        SELECT TOP 1 IDVERIFICACION FROM VERIFICACION WHERE IDVERIFICENTRO = 12
        --(SELECT TOP 1 IDVERIFICENTRO FROM CAT_VERIFICENTRO WHERE ES_ACTIVO = 1 ORDER BY NEWID())
        AND CONVERT(VARCHAR,CONVERT(date,FECHA_INI_VERIF),103) = convert(VARCHAR,convert(DATE,GETDATE()),103) AND
        ESTATUS_OPERACION IN (1,2,4,11) AND FACE = 1 AND FECHA_MONITOREO IS NULL
        ORDER BY NEWID()
    """, nativeQuery = true)
    Integer getIdVerificacionAleatoria()


    /**
     * Consulta la información detallada de una verificación en especifico
     * @param idVerificacion Integer identificador de la verificación
     * @return Map mapa con la información detallada de la verificación
     */
    @Query(value = """
SELECT VE.IDVERIFICACION,VE.IDUNIQUE,PV.NO_SERIE, VE.IDTIPOHOLOGRAMA,
     case when VE.FECHA_INI_PRUEBA is null then '-------' else (convert(varchar,VE.FECHA_INI_PRUEBA,103)+' '+convert(varchar,VE.FECHA_INI_PRUEBA,24)) end AS FECHA_INI_PRUEBA,
     case when VE.FECHA_FIN_PRUEBA is null then '-------' else (convert(varchar,VE.FECHA_FIN_PRUEBA,103)+' '+convert(varchar,VE.FECHA_FIN_PRUEBA,24)) end AS FECHA_FIN_PRUEBA,
     case when VE.FOLIO_ANTERIOR is null then 'Sin Folio Anterior' else VE.FOLIO_ANTERIOR end as FOLIO_ANTERIOR,
     case when VE.FOLIO_MULTA is null then 'No Aplica' else VE.FOLIO_MULTA end as FOLIO_MULTA,
     case when VE.TIPO_RECHAZO is null then 'No Aplica' else (select descripcion from CAT_TIPORECHAZO where IDTIPORECHAZO=VE.TIPO_RECHAZO) end as TIPO_RECHAZO,
    (select descripcion from CAT_MOTIVO where IDMOTIVO=VE.IDMOTIVO) as Motivo,
    VE.ESTATUS_OPERACION as estatus,
    PV.NO_TARJETACIRC as tarjeta,
   isnull(convert(varchar,convert(date,PV.FECHA_FACT),103),'-------') AS FECHAFAC,isnull(convert(varchar,convert(date,PV.FECHA_FACT),103),'-------') AS FECHA_TARJETA,
   PV.MODELO, PV.IDPADRONVEHICULAR,
   (SELECT DESCRIPCION FROM CAT_TIPOSERVICIO WHERE IDTIPOSERVICIO=PV.IDTIPOSERVICIO) AS SERVICIO,
   case when VE.FECHA_FIN_PRUEBA is null then '-------' else (case VE.IDTIPOHOLOGRAMA when 5 then 'Rechazado' else 'Aprobado' end) end as resultado,
   CLV.NO_LINEA,
 isnull(CEV.DESCRIPCION,isnull((select descripcion from cat_equipoverif where idequipoverif=CV.idequipoverif),'Proveedor no registrado')) as equipover,
 case
 when CLV.TIPO_LINEA=1 then 'Gasolina'
 when CLV.TIPO_LINEA=2 then 'Diesel'
 when CLV.TIPO_LINEA=3 then 'Gasolina y Diesel'
 end as tipolinea,
 isnull(PV.no_motor,'--') as no_motor,
 CM.DESCRIPCION as Marca,
 case pv.idmarca_submarca when -1 then  'OTRO' +' ' + CM.DESCRIPCION  else CMS.DESCRIPCION end as Submarca,
 VE.NO_PLACA,VE.IDUNIQUE,VE.IDLINEAVERIF,VE.IDVERIFICENTRO,
 isnull(DVE.CAL,0.0) AS CAL,
 isnull(DVE.TEM_CAM,0) AS TEMCAM,
 isnull(DVE.OPACIDAD,'0') AS OPACIDAD,
 isnull(DVE.LAMDA_2540,0.0) AS LAMDA1,
 isnull(DVE.LAMDA_5024,0.0) AS LAMDA2,
 isnull(DVE.VEL_GOB,0) AS MAXRPM,
 isnull(DVE.HC_2540,0.0) AS HC1,
 isnull(DVE.HC_5024,0.0) AS HC2,
 isnull(DVE.CO_2540,0.0) AS CO1,
 isnull(DVE.CO_5024,0.0) AS CO2,
 isnull(DVE.CO2_2540,0.0) AS CO21,
 isnull(DVE.CO2_5024,0.0) AS CO22,
 isnull(DVE.COCO2_2540,0.0) AS COCO21,
 isnull(DVE.COCO2_5024,0.0) AS COCO22,
 isnull(DVE.O2_2540,0.0) AS O21,
 isnull(DVE.O2_5024,0.0) AS O22,
 isnull(DVE.KPH_2540,0.0) AS KPH1,
 isnull(DVE.KPH_5024,0.0) AS KPH2,
 isnull(DVE.NO_2540,0.0) AS NO1,
 isnull(DVE.NO_5024,0.0) AS NO2,
 isnull(DVE.HC_2540_B,0) AS HC1B,
 isnull(DVE.CO_2540_B,0) AS CO1B,
 isnull(DVE.CO_5024_B,0) AS CO2B,
 isnull(DVE.HC_5024_B,0) AS HC2B,
 isnull(DVE.CO2_2540_B,0) AS CO21B,
 isnull(DVE.CO2_5024_B,0) AS CO22B,
 isnull(DVE.O2_2540_B,0) AS O21B,
 isnull(DVE.O2_5024_B,0) AS O22B,
 isnull(DVE.NOX_2540_B,0) AS NO1B,
 isnull(DVE.NOX_5024_b,0) AS NO2B,
 isnull(DVE.GOBERNADOR,0) AS GOBERNADOR,
 isnull(DVE.TEMP_MOT,0) AS TEMP_MOT,
 isnull(DVE.TEM_CAM,0) AS TEM_CAM,
 isnull(DVE.TEM_GAS,0) AS TEM_GAS,
 isnull(DVE.VEL_GOB,0) AS VEL_GOB,
 isnull(DVE.PRES_GAS,0) AS PRES_GAS,
 isnull(DVE.PSI_5024,0) AS PSI_5024,
 isnull(DVE.PSI_2540,0) AS PSI_2540, 
 isnull(DVE.RPM_5024,0) AS RPM_5024,
 isnull(DVE.RPM_2540,0) AS RPM_2540,
 isnull(DVE.VOLTS_5024,0) AS VOLTS_5024,
 isnull(DVE.VOLTS_2540,0) AS VOLTS_2540,
 isnull(DVE.HR_5024,0) AS HR_5024,
 isnull(DVE.HR_2540,0) AS HR_2540,
 isnull(DVE.THP_5024,0) AS THP_5024,
 isnull(DVE.THP_2540,0) AS THP_2540,
 isnull(DVE.FCNOX_5024,0) AS FCNOX_5024,
 isnull(DVE.FCNOX_2540,0) AS FCNOX_2540, 
 isnull(DVE.FCDIL_5024,0) AS FCDIL_5024,
 isnull(DVE.FCDIL_2540,0) AS FCDIL_2540,
 isnull(DVE.EFIC_5024,0) AS EFIC_5024,
 isnull(DVE.EFIC_2540,0) AS EFIC_2540,
 isnull(VE.ID_PARAM_PIREC,0) AS ID_PARAM_PIREC,
 isnull(PPC.CO,0) AS EVALPIRCO,
 isnull(PPC.O2,0) AS EVALPIRO2,
 isnull(PPC.CO2,0) AS EVALPIRCO2,
 isnull(PVA.CAL,0) AS EVALCAL,
 isnull(PVA.HC,0) AS EVALHC,
 isnull(PVA.CO,0) AS  EVALCO,
 isnull(PVA.NOX,0) AS EVALNOX,
 isnull(PVA.O2,0) AS EVALO2,
 isnull(PVA.LAMDA,0) AS EVAL_LAMDA,
 isnull(PVA.CO_CO2_INI,0) AS EVALCOCO2INI,
 isnull(PVA.CO_CO2_FIN,0) AS EVALCOCO2FIN,
 isnull(PVA.O2_EST,0) AS EVALO2EST,
 isnull(PVA.LAMDA_EST,0) AS EVAL_LAMDAEST,
 isnull(PV.COMB_ORIG,1) AS COMBUSTIBLE,
 VE.T_PRUEBA as tipoprueba,
 (select CTH.DESCRIPCION from CAT_TIPOHOLOGRAMA CTH where CTH.IDTIPOHOLOGRAMA=VE.IDTIPOHOLOGRAMA_INI ) AS TIPOHOLOINI,
 case when VE.FECHA_FIN_PRUEBA is null then '-------' else (select CTH.DESCRIPCION from CAT_TIPOHOLOGRAMA CTH where CTH.IDTIPOHOLOGRAMA=VE.IDTIPOHOLOGRAMA ) end AS TIPOHOLOFIN,
 isnull(VE.FOLIO,'0') AS FOLIO,
 VE.IDVERIFICACION,
 (convert(varchar,VE.FECHA_INI_VERIF,103)+' '+convert(varchar,VE.FECHA_INI_VERIF,24)) as FECHA_REGISTRO,
 CV.RAZON_SOCIAL, CV.CENTRO_NOM,
 isnull(convert(varchar,VE.FECHA_FIN_VERIF,103)+' '+convert(varchar,VE.FECHA_FIN_VERIF,24),'-------') AS FECHAFIN,
 CV.CENTRO_NOM, PV.NOMBRE+' '+ isnull(PV.APELLIDO_PATERNO,' ') + ' ' + isnull(PV.APELLIDO_MATERNO,' ') AS NOMBRE,
 US.NOMBRE+' '+ isnull(US.APELLIDOPATERNO,'--') + ' ' + isnull(US.APELLIDOMATERNO,'--') AS NOMTECNICO,
 PV.CODIGO_POSTAL,(SELECT DESCRIPCION FROM CAT_ENTIDAD WHERE IDENTIDAD=PV.IDENTIDAD) AS ENTIDAD,
 case PV.IDMUNICIPIO when  0 then 'NO ESPECIFICADO|NO ESPECIFICADO|--' else  concat('',(case PV.IDCALLE when 0 then PV.AUX_CALLE else
             (SELECT DESCRIPCION FROM CAT_CALLE WHERE IDCALLE=PV.IDCALLE AND IDCOLONIA=PV.IDCOLONIA) end ) +' '+PV.NO_EXTERIOR+'|'+ (case PV.IDCOLONIA when 0 then PV.AUX_COLONIA else (SELECT DESCRIPCION FROM CAT_COLONIA WHERE IDCOLONIA=PV.IDCOLONIA AND IDMUNICIPIO=PV.IDMUNICIPIO) end)+ '|' + PV.IDMUNICIPIO) end AS DOMICILIO ,
             isnull(PV.POT_5024,'0') AS POT_5024, isnull(PV.POT_2540,'0') AS POT_2540,
isnull(VE.kilometraje,0) as km, isnull(CTC.descripcion,'Gasolina') as tipocombus, isnull(PV.IDTABLAMAESTRA,0) AS idtablamaestra, case VE.T_PRUEBA when 0 then 'Estatica' else 'Dinamica' end AS TIPO_PRUEBA
 FROM VERIFICACION VE
 LEFT JOIN PADRON_VEHICULAR PV ON PV.IDPADRONVEHICULAR= VE.IDPADRONVEHICULAR
 LEFT JOIN CAT_MARCA CM ON PV.IDMARCA=CM.IDMARCA
 LEFT JOIN CAT_MARCA_SUBMARCA CMS ON cms.idmarca_submarca=pv.idmarca_submarca
 LEFT JOIN CAT_VERIFICENTRO CV ON VE.IDVERIFICENTRO=CV.IDVERIFICENTRO
 LEFT JOIN CAT_TIPOHOLOGRAMA CTH ON VE.IDTIPOHOLOGRAMA=CTH.IDTIPOHOLOGRAMA
 LEFT JOIN USUARIO US ON US.IDUSUARIO=VE.IDUSUARIO_PRUEBA
 LEFT JOIN DETVERIFICA DVE ON DVE.IDVERIFICACION=VE.IDVERIFICACION
 LEFT JOIN cat_lineaverif CLV ON CLV.IDLINEAVERIF=VE.IDLINEAVERIF
 LEFT JOIN PARAM_VERIFADM PVA ON PVA.IDPARAMVERIFADM=VE.ID_PARAM_VERIFADMIN
 LEFT JOIN PARAM_PIREC PPC ON PPC.IDPARAMPIREC=VE.ID_PARAM_PIREC
 LEFT JOIN CAT_TIPOCOMBUS CTC ON PV.COMB_ORIG=CTC.IDTIPOCOMBUS
LEFT JOIN CAT_EQUIPOVERIF CEV ON CEV.IDEQUIPOVERIF=VE.IDEQUIPOVERIF
 where VE.IDVERIFICACION=:idVerificacion
""", nativeQuery = true)
    Map getDatosVerificacion(@Param("idVerificacion") Integer idVerificacion)


    /**
     * Consulta el folio de una verificación en específico
     * @param idVerificacion Integer identificador de la verificación
     * @return String folio de la verificación
     */
    @Query(value = "SELECT FOLIO FROM VERIFICACION WHERE IDVERIFICACION=:idVerificacion", nativeQuery = true)
    String getFolioVerificacion(@Param("idVerificacion") Integer idVerificacion)

    /**
     * Obtiene los documentos de un vehículo por una verificación específica.
     * @param idVerificacion Integer identificador de la verificación
     * @return List<Map> lista de documentos del vehículo
     */
    @Query(value = '''SELECT DISTINCT(DV.ID_DOCUMENTO_VEHICULO) AS iddoc, DVH.ID_TIPO_DOCUMENTO,isnull(DVH.UBICACION,'-1') AS ruta,
            CASE DVH.ID_TIPO_DOCUMENTO WHEN 99 THEN 'Evidencia Proceso de Verificacion' ELSE (CASE DVH.ID_TIPO_DOCUMENTO WHEN 98 THEN 'Evidencia Mesa de Control' ELSE cdm.descripcion END) END AS descripcion
            FROM DOCUMENTO_VERIFICACION DV
            INNER JOIN DOCUMENTO_VEHICULO DVH ON DV.ID_DOCUMENTO_VEHICULO = DVH.ID_DOCUMENTO_VEHICULO
            LEFT JOIN CAT_DOCUMENTACION_MOTIVO CDM on DVH.ID_TIPO_DOCUMENTO=CDM.id_cat_documentacion_motivo
            where IDVERIFICACION = :idVerificacion ORDER BY DV.ID_DOCUMENTO_VEHICULO ASC''', nativeQuery = true)
    List<Map> getDocumentosVerificacion(@Param("idVerificacion") Integer idVerificacion)


    /**
     * Consulta la información necesaria para generar el acta de supervisión remota por la clave de la anomalía
     * agregada en una verificación
     * @param clave String clave de la anomalía registrada en la verificación
     * @return Map información a mostrar el acta de supervisión
     */
    @Query(value = """
        SELECT VE.IDVERIFICACION, VE.IDVERIFICENTRO, PV.IDPADRONVEHICULAR, CV.CENTRO_NOM, CV.RAZON_SOCIAL, isnull(CEV.DESCRIPCION,(select descripcion from CAT_EQUIPOVERIF where IDEQUIPOVERIF=CV.IDEQUIPOVERIF)) AS EQUIPOVER, CLV.NO_LINEA, CASE WHEN CLV.TIPO_LINEA=1 THEN 'Gasolina' WHEN CLV.TIPO_LINEA=2 THEN 'Diesel' WHEN CLV.TIPO_LINEA=3 THEN 'Gasolina y Diesel' END AS TIPOLINEA, 
        US.NOMBRE+' '+ isnull(US.APELLIDOPATERNO,'--') + ' ' + isnull(US.APELLIDOMATERNO,'--') AS NOMTECNICO, PV.CODIGO_POSTAL, (SELECT DESCRIPCION FROM CAT_ENTIDAD WHERE IDENTIDAD=PV.IDENTIDAD) AS ENTIDAD, 
        case PV.IDMUNICIPIO when  0 then 'NO ESPECIFICADO|NO ESPECIFICADO|--' else  concat('',(case PV.IDCALLE when 0 then PV.AUX_CALLE else 
        (SELECT DESCRIPCION FROM CAT_CALLE WHERE IDCALLE=PV.IDCALLE AND IDCOLONIA=PV.IDCOLONIA) end ) +' '+PV.NO_EXTERIOR+'|'+ (case PV.IDCOLONIA when 0 then PV.AUX_COLONIA else (SELECT DESCRIPCION FROM CAT_COLONIA WHERE IDCOLONIA=PV.IDCOLONIA AND IDMUNICIPIO=PV.IDMUNICIPIO) end)+ '|' + PV.IDMUNICIPIO) end AS DOMICILIO ,
        CM.DESCRIPCION AS MARCA, case PV.IDMARCA_SUBMARCA when -1 then  'OTRO' +' ' + CM.DESCRIPCION else CMS.DESCRIPCION end AS SUBMARCA, 
        VE.NO_PLACA, PV.NO_SERIE, PV.MODELO, (SELECT DESCRIPCION FROM CAT_TIPOSERVICIO WHERE IDTIPOSERVICIO=PV.IDTIPOSERVICIO) AS SERVICIO, 
        PV.NO_TARJETACIRC AS TARJETA, case when PV.FECHA_FACT is null then '-------' else (convert(varchar,PV.FECHA_FACT,103)) end AS FECHAFAC, isnull(PV.NO_MOTOR,'--') AS NO_MOTOR, 
        VE.IDUNIQUE, VE.ESTATUS_OPERACION AS ESTATUS, case when VE.FOLIO_ANTERIOR is NULL then 'Sin Folio Anterior' else VE.FOLIO_ANTERIOR end AS FOLIO_ANTERIOR, 
        case when VE.FOLIO_MULTA is NULL then 'No Aplica' else VE.FOLIO_MULTA end AS FOLIO_MULTA, 
        isnull(convert(varchar,VE.FECHA_INI_VERIF,103)+' '+convert(varchar,VE.FECHA_INI_VERIF,24),'-------') AS FECHA_REGISTRO, 
        isnull(convert(varchar,VE.FECHA_FIN_VERIF,103)+' '+convert(varchar,VE.FECHA_FIN_VERIF,24),'-------') AS FECHAFIN, 
        isnull(convert(varchar,VE.FECHA_INI_PRUEBA,103)+' '+convert(varchar,VE.FECHA_INI_PRUEBA,24),'-------') AS FECHA_INI_PRUEBA, 
        isnull(convert(varchar,VE.FECHA_FIN_PRUEBA,103)+' '+convert(varchar,VE.FECHA_FIN_PRUEBA,24),'-------') AS FECHA_FIN_PRUEBA, 
        (SELECT CTH.DESCRIPCION FROM CAT_TIPOHOLOGRAMA CTH WHERE CTH.IDTIPOHOLOGRAMA=VE.IDTIPOHOLOGRAMA_INI ) AS TIPOHOLOINI, 
        case when VE.FECHA_FIN_PRUEBA is NULL then '-------' else (SELECT CTH.DESCRIPCION FROM CAT_TIPOHOLOGRAMA CTH WHERE CTH.IDTIPOHOLOGRAMA=VE.IDTIPOHOLOGRAMA ) end AS TIPOHOLOFIN, 
        (SELECT DESCRIPCION FROM CAT_MOTIVO WHERE IDMOTIVO=VE.IDMOTIVO) AS MOTIVO, isnull(VE.FOLIO,'0') AS FOLIO, 
        case when VE.FECHA_FIN_PRUEBA is NULL then '-------' else (case VE.IDTIPOHOLOGRAMA when 5 then 'Rechazado' else 'Aprobado' end ) end AS RESULTADO, 
        case when VE.TIPO_RECHAZO is NULL then 'No Aplica' else (SELECT RESULTADO FROM CAT_TIPORECHAZO WHERE TIPO_RECHAZO=VE.TIPO_RECHAZO) end AS TIPO_RECHAZO, 
        VE.T_PRUEBA AS TIPOPRUEBA, isnull(PV.COMB_ORIG,1) AS COMBUSTIBLE, PV.POT_5024, PV.POT_2540, PV.POTMAX_RPM AS RPM_PV, 
        isnull(DVE.THP_HUMO, 0.0) AS THP_HUMO, 
        isnull(DVE.HC_5024_B, 0.0) AS HC_5024_B, 
        isnull(DVE.CO_5024_B, 0.0) AS CO_5024_B, 
        isnull(DVE.CO2_5024_B, 0.0) AS CO2_5024_B, 
        isnull(DVE.O2_5024_B, 0.0) AS O2_5024_B, 
        isnull(DVE.NOX_5024_B, 0.0) AS NOX_5024_B, 
        isnull(DVE.LAMDA_5024, 0.0) AS LAMDA_5024, 
        isnull(DVE.TEMP_5024, 0.0) AS TEMP_5024, 
        isnull(DVE.HR_5024, 0.0) AS HR_5024, 
        isnull(DVE.PSI_5024, 0.0) AS PSI_5024, 
        isnull(DVE.FCNOX_5024, 0.0) AS FCNOX_5024, 
        isnull(DVE.FCDIL_5024, 0.0) AS FCDIL_5024, 
        isnull(DVE.RPM_5024, 0.0) AS RPM_5024, 
        isnull(DVE.KPH_5024, 0.0) AS KPH_5024, 
        isnull(DVE.THP_5024, 0.0) AS THP_5024, 
        isnull(DVE.VOLTS_5024, 0.0) AS VOLTS_5024, 
        isnull(DVE.HC_5024, 0.0) AS HC_5024, 
        isnull(DVE.CO_5024, 0.0) AS CO_5024, 
        isnull(DVE.CO2_5024, 0.0) AS CO2_5024, 
        isnull(DVE.COCO2_5024, 0.0) AS COCO2_5024, 
        isnull(DVE.O2_5024, 0.0) AS O2_5024, 
        isnull(DVE.NO_5024, 0.0) AS NO_5024, 
        isnull(DVE.EFIC_5024, 0.0) AS EFIC_5024, 
        isnull(DVE.HC_2540_B, 0.0) AS HC_2540_B, 
        isnull(DVE.CO_2540_B, 0.0) AS CO_2540_B, 
        isnull(DVE.CO2_2540_B, 0.0) AS CO2_2540_B, 
        isnull(DVE.O2_2540_B, 0.0) AS O2_2540_B, 
        isnull(DVE.NOX_2540_B, 0.0) AS NOX_2540_B, 
        isnull(DVE.LAMDA_2540, 0.0) AS LAMDA_2540, 
        isnull(DVE.TEMP_2540, 0.0) AS TEMP_2540, 
        isnull(DVE.HR_2540, 0.0) AS HR_2540, 
        isnull(DVE.PSI_2540, 0.0) AS PSI_2540, 
        isnull(DVE.FCNOX_2540, 0.0) AS FCNOX_2540, 
        isnull(DVE.FCDIL_2540, 0.0) AS FCDIL_2540, 
        isnull(DVE.RPM_2540, 0.0) AS RPM_2540, 
        isnull(DVE.KPH_2540, 0.0) AS KPH_2540, 
        isnull(DVE.THP_2540, 0.0) AS THP_2540, 
        isnull(DVE.VOLTS_2540, 0.0) AS VOLTS_2540, 
        isnull(DVE.HC_2540, 0.0) AS HC_2540, 
        isnull(DVE.CO_2540, 0.0) AS CO_2540, 
        isnull(DVE.CO2_2540, 0.0) AS CO2_2540, 
        isnull(DVE.COCO2_2540, 0.0) AS COCO2_2540, 
        isnull(DVE.O2_2540, 0.0) AS O2_2540, 
        isnull(DVE.NO_2540, 0.0) AS NO_2540, 
        isnull(DVE.EFIC_2540, 0.0) AS EFIC_2540, 
        isnull(DVE.OPACIDAD, 0.0) AS OPACIDAD, 
        isnull(DVE.TEMP_MOT, 0.0) AS TEMP_MOT, 
        isnull(DVE.VEL_GOB, 0.0) AS VEL_GOB, 
        isnull(DVE.POTMAX_RPM, 0.0) AS POTMAX_RPM, 
        isnull(DVE.TEM_GAS, 0.0) AS TEM_GAS, 
        isnull(DVE.TEM_CAM, 0.0) AS TEM_CAM, 
        isnull(DVE.PRES_GAS, 0.0) AS PRES_GAS, 
        isnull(DVE.GOBERNADOR, 0.0) AS GOBERNADOR, 
        isnull(DVE.C_RECHAZO, 0.0) AS C_RECHAZO, 
        isnull(DVE.PROTOCOLO_APLICADO, 0.0) AS PROTOCOLO_APLICADO, 
        isnull(DVE.POT_MAXRPM, 0.0) AS POT_MAXRPM, 
        isnull(DVE.NUMERO_SERIE, 0.0) AS NUMERO_SERIE, 
        isnull(DVE.CAL, 0.0) AS CAL, 
        0 AS FC, 
        0 AS PFC, AMC.CLAVE, case AMC.TIPO_ACCION when 1 then 'Notificación a Verificentro' when 2 then 'Notificación a Expediente' when 3 then 'Solicitar Cancelación de Verificación' when 4 then 'Solicitar Cancelación de Línea' when 5 then 'Solicitar Cancelación de Centro' end as tipo, 
        (convert(varchar,AMC.FECHA_REGISTRO,103)+' '+convert(varchar,AMC.FECHA_REGISTRO,24)) as FECHA_REGISTRO, AMC.DESCRIPCION, CMA.DESCRIPCION AS TIPOANOMAL, 
        isnull(VE.ID_PARAM_PIREC,0) AS ID_PARAM_PIREC, 
        isnull(PPC.CO,0) AS EVALPIRCO, 
        isnull(PPC.O2,0) AS EVALPIRO2, 
        isnull(PPC.CO2,0) AS EVALPIRCO2, 
        isnull(PVA.CAL,0) AS EVALCAL, 
        isnull(PVA.HC,0) AS EVALHC, 
        isnull(PVA.CO,0) AS  EVALCO, 
        isnull(PVA.NOX,0) AS EVALNOX, 
        isnull(PVA.O2,0) AS EVALO2, 
        isnull(PVA.LAMDA,0) AS EVAL_LAMDA, 
        isnull(PVA.CO_CO2_INI,0) AS EVALCOCO2INI, 
        isnull(PVA.CO_CO2_FIN,0) AS EVALCOCO2FIN, 
        isnull(PVA.O2_EST,0) AS EVALO2EST, 
        isnull(PVA.LAMDA_EST,0) AS EVAL_LAMDAEST, 
        US2.NOMBRE+' '+ isnull(US2.APELLIDOPATERNO,'--') + ' ' + isnull(US2.APELLIDOMATERNO,'--') AS NOMSUPERVISOR,
        AMC.IDUSUARIO
        FROM VERIFICACION VE 
        LEFT JOIN PADRON_VEHICULAR PV ON PV.IDPADRONVEHICULAR= VE.IDPADRONVEHICULAR 
        LEFT JOIN CAT_MARCA CM ON PV.IDMARCA=CM.IDMARCA 
        LEFT JOIN CAT_MARCA_SUBMARCA CMS ON CMS.IDMARCA_SUBMARCA=PV.IDMARCA_SUBMARCA 
        LEFT JOIN CAT_VERIFICENTRO CV ON VE.IDVERIFICENTRO=CV.IDVERIFICENTRO 
        LEFT JOIN CAT_TIPOHOLOGRAMA CTH ON VE.IDTIPOHOLOGRAMA=CTH.IDTIPOHOLOGRAMA 
        LEFT JOIN USUARIO US ON US.IDUSUARIO=VE.IDUSUARIO_PRUEBA 
        LEFT JOIN DETVERIFICA DVE ON DVE.IDVERIFICACION=VE.IDVERIFICACION 
        LEFT JOIN CAT_LINEAVERIF CLV ON CLV.IDLINEAVERIF=VE.IDLINEAVERIF 
        LEFT JOIN ANOMALIAS_MESA_CONTROL AMC ON VE.IDVERIFICACION=AMC.IDVERIFICACION 
        LEFT JOIN USUARIO US2 ON US2.IDUSUARIO=AMC.IDUSUARIO 
        left JOIN CAT_MOTIVO_ANOMALIA CMA ON AMC.ID_CAT_MOTIVO_ANOMALIA=CMA.ID_CAT_MOTIVO_ANOMALIA 
        LEFT JOIN PARAM_VERIFADM PVA ON PVA.IDPARAMVERIFADM=VE.ID_PARAM_VERIFADMIN 
        LEFT JOIN PARAM_PIREC PPC ON PPC.IDPARAMPIREC=VE.ID_PARAM_PIREC 
        LEFT JOIN CAT_EQUIPOVERIF CEV ON CEV.IDEQUIPOVERIF=VE.IDEQUIPOVERIF
        WHERE AMC.CLAVE = :clave
    """, nativeQuery = true)
    Map findDatosActaSupervisionByClave(@Param("clave") String clave)


    /**
     * Consulta la información de una prueba obd relacionada a una verificación en específico
     * @param idVerificacion Integer identificador de la verificación de la cual se quiere obtener la información de la prueba obd
     * @return Map mapa con la información de la prueba obd
     */
    @Query(value = """
        SELECT TOP 1 VO.IDVERIFICACIONOBD, VO.IDVERIFICACION, VO.FOLIO, VO.FECHA, VO.IDUSUARIO, VO.MIL,
        VO.DTC, VO.FALLO_ENCENDIDO, VO.SISTEMA_COMBUSTIBLE, VO.COMPONENTES, VO.CATALIZADOR,
        VO.CATALIZADOR_SC, VO.SISTEMA_EVAPORATIVO,VO.SISTEMA_SECUNDARIO,VO.REFRIGERANTE_AC,
        VO.SENSOR_OXIGENO, VO.SENSOR_OXIGENO_SC, VO.SISTEMA_EGR, VO.RESULTADO, VO.ESTATUS,
        isnull(VO.TIPO_OBD,' ') AS TIPO_OBD, VO.FECHA_IMPRESION, isnull(BOV.NO_SERIE_OBD,'-1') AS SERIE
        FROM VERIFICACION_OBD VO LEFT JOIN BIT_OBD_VIN BOV ON BOV.IDVERIFICACION = VO.IDVERIFICACION
        WHERE VO.IDVERIFICACION = :idVerificacion ORDER BY IDVERIFICACIONOBD DESC
    """, nativeQuery = true)
    Map findVerificacionObdByVerificacion(@Param("idVerificacion") Integer idVerificacion)

    /**
     * Consulta los códigos de error agregados en una prueba obd relacionada a una verificación
     * @param idVerificacion Integer identificador de la verificación
     * @return List<Map> lista de códigos de error agregados en la prueba obd
     */
    @Query(value = """
        SELECT DISTINCT VO.IDVERIFICACION AS IDVERIFICACION, VOE.CODIGO AS CODIGO, ISNULL(CEO.DESCRIPCION,'Desconocido') AS DESCRIPCION
        FROM VERIFICACION_OBD VO
        INNER JOIN REL_VERIFICACIONOBD_ERROR VOE ON VOE.IDVERIFICACIONOBD = VO.IDVERIFICACIONOBD
        INNER JOIN CAT_ERRORES_OBD CEO ON CEO.CODIGO = VOE.CODIGO WHERE VO.IDVERIFICACION = :idVerificacion
    """, nativeQuery = true)
    List<Map> findCodigosErrorObdByVerificacion(@Param("idVerificacion") Integer idVerificacion)


}