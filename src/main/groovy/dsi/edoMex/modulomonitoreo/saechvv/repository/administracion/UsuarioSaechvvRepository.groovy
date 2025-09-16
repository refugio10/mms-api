package dsi.edoMex.modulomonitoreo.saechvv.repository.administracion

//import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Perfil
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
//import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.TipoUsuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.repository.query.Param
import org.springframework.data.jpa.repository.Query

/**
 * Contiene funciones para la administración de usuario
 *
 * @author lorenav
 * @version 1.0 12/11/2024
 */
@Repository
interface UsuarioSaechvvRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Obtener un usuario por medio de la clave
     *
     * @param clave Cadena con la clave de usuario
     * @return Objeto de clase Optional de tipo clase Usuario
     */
    Optional<Usuario> findByClave(String clave)

    /**
     * Obtiene una lista de usuarios de acuerdo al perfil especificado
     *
     * @param perfil Pefil correspondiente
     * @return List<Usuario> Usuario consultado
     * @author Felipe Ocampo Araujo
     * @version 03/12/2024 1.0
     */
    //List<Usuario> findAllByPerfil(Perfil perfil)


    /**
     * Obtiene una lista de usuarios asociados a un verificentro
     *
     * @param idTipoUsuario Identificador del verificentro
     * @param tipoUsuario Identificador del tipo usuario 0 = verificentro
     * @return List<Usuario> Lista de usuarios encontrados
     */
    List<Usuario> findAllByIdTipoUsuarioAndTipoUsuario(Integer idTipoUsuario, Integer tipoUsuario)


    /**
     * Obtiene una lista de usuarios de acuerdo al activo especificado
     *
     * @param activo Identificador del estatus
     * @return List<Usuario> Lista de usuarios encontrados
     * @author Felipe Ocampo Araujo
     * @version 04/12/2024 1.0
     */
    List<Usuario> findAllByActivo(Integer activo)

    /**
     *Obtiene una lista de usuarios para asignar como encargado de verificentro
     * @param verificentro id del verificentro
     * @return lisuta de la clase Usuario
     */
    @Query(value = '''SELECT us.IDUSUARIO AS idUsuario,
       CONCAT(us.CLAVEUSER, ' - ', us.NOMBRE, ' ', us.APELLIDOPATERNO, ' ', us.APELLIDOMATERNO)
       AS descripcion
       FROM USUARIO us
       WHERE us.ES_ACTIVO = 1
       AND us.TIPO_USUARIO = 0
       AND us.ID_TIPO_USUARIO = :verificentro
       AND us.IDPERFIL in (44,46)
       ORDER BY descripcion ASC''', nativeQuery = true)
    List<Map> findByVerificentro(@Param("verificentro") Integer verificentro)


    /**
     *Obtien el usuario encargado de un verificentro en especifico
     * @param verificentro id del verificentro
     * @param tipoUsuario tipo del usuario del verificentro
     * @return mapa con el encargado del verificentro
     */
    @Query(value = '''SELECT ec.IDENCARGADO_CENTRO,
    u.idUsuario,
    CONCAT(u.CLAVEUSER, ' - ', u.NOMBRE, ' ', u.APELLIDOPATERNO, ' ', u.APELLIDOMATERNO) AS descripcion
    FROM usuario u
    JOIN ENCARGADOS_CENTRO ec ON ec.IDUSUARIO = u.IDUSUARIO
    WHERE u.ES_ACTIVO = 1
    AND ec.ES_ACTIVO = 1
    AND u.TIPO_USUARIO = 0
    AND ec.TIPO = :tipo
    AND u.IDPERFIL IN (44, 46)
    AND ec.IDVERIFICENTRO = :verificentro''', nativeQuery = true)
    Map findByVerificentroAndTipoUsuario(@Param("verificentro") Integer verificentro,  @Param("tipo") Integer tipoUsuario)


    /**
     * Obtiene una lista de usuarios de destinatario a los que se les enviará un mensaje
     * @param idTipoUsuario Integer identifiacador del tipo de usuario
     * @return List<Integer> lista de ids de los usuarios destinatarios a enviar el mensaje
     */
    @Query(value = '''SELECT IDUSUARIO FROM USUARIO U WHERE U.TIPO_USUARIO = 0 AND U.ID_TIPO_USUARIO = :idTipoUsuario AND U.IDPERFIL = 44 AND U.ES_ACTIVO = 1''', nativeQuery = true)
    List<Integer> detinatariosMensaje(@Param("idTipoUsuario") Integer idTipoUsuario)

}