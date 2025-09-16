package dsi.edoMex.modulomonitoreo

import dsi.edoMex.modulomonitoreo.saechvv.entity.config.RequestMap
import dsi.edoMex.modulomonitoreo.saechvv.entity.config.RoleApi
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.ModuloRequest
import dsi.edoMex.modulomonitoreo.saechvv.repository.config.RequestMapRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.config.RoleApiRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Encargado de inicializar la aplicación de módulo de monitoreo
 *
 * @author Refugio Rodriguez Bueno
 * @version 1.0 Julio 2025
 */
@SpringBootApplication
class MmsApplication implements CommandLineRunner {

    @Autowired
    private RequestMapRepository requestMapRepository

    @Autowired
    private RoleApiRepository roleApiRepository


    /**
     * Hilo que se ejecuta para acciones posteriores al levantamiento de la api
     * @param args incoming main method arguments
     */
    @Override
    void run(String... args) throws Exception {
        addRequestMap()
        RoleApi roleApi = roleApiRepository.findByNombre("ROLE_ADMIN_MONITOREO").orElse(new RoleApi())
        if (roleApi.getId() == null) {
            roleApi.nombre = "ROLE_ADMIN_MONITOREO"
            roleApi.descripcion = "Rol administrador para el módulo de monitoreo y supervisión"
            roleApi.activo = 1
            roleApiRepository.save(roleApi)
        }
    }

    /**
     * Lectura y registro de RequesMap de los métodos que se encuentren en el controller de la carpeta principal de mms
     */
    void addRequestMap() {
        try {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false)
            scanner.addIncludeFilter(new AnnotationTypeFilter(RequestMapping.class))

            def tipoSeguridad = ["permitAll", "isAuthenticated()"]

            for (BeanDefinition beanDefinition : scanner.findCandidateComponents("dsi.edoMex.modulomonitoreo.modulomonitoreo.controller")) {
                Class classNew = Class.forName(beanDefinition.getBeanClassName())

                String pathPrincipal = (classNew.getAnnotation(RequestMapping.class) != null) ?
                        ((RequestMapping) classNew.getAnnotation(RequestMapping.class)).value()[0].toString() : ""

                def annotations = classNew.getMethods().findAll(it ->
                        (it.getAnnotation(GetMapping.class) != null || it.getAnnotation(PostMapping.class) != null ||
                                it.getAnnotation(PutMapping.class) != null || it.getAnnotation(PatchMapping.class) != null ||
                                it.getAnnotation(DeleteMapping.class) != null)
                                && (it.getAnnotation(Secured.class) == null
                                || !tipoSeguridad.contains(((Secured) it.getAnnotation(Secured.class)).value()[0])
                        )
                )

                for (def it : annotations) {
                    def mapping
                    String metodo = "GET"

                    if (it.getAnnotations()[0] instanceof GetMapping) {
                        mapping = it.getAnnotation(GetMapping.class)
                    } else if (it.getAnnotations()[0] instanceof PostMapping) {
                        mapping = it.getAnnotation(PostMapping.class)
                        metodo = "POST"
                    } else if (it.getAnnotations()[0] instanceof PutMapping) {
                        mapping = it.getAnnotation(PutMapping.class)
                        metodo = "PUT"
                    } else if (it.getAnnotations()[0] instanceof PatchMapping) {
                        mapping = it.getAnnotation(PatchMapping.class)
                        metodo = "PATCH"
                    } else if (it.getAnnotations()[0] instanceof DeleteMapping) {
                        mapping = it.getAnnotation(DeleteMapping.class)
                        metodo = "DELETE"
                    }

                    String url = pathPrincipal + mapping?.value()[0]
                    String controller = classNew.getSimpleName().toString()
                    String accion = mapping?.value()[0]

                    RequestMap requestMap = requestMapRepository.findByUrlAndControllerAndAccionAndMetodoAndModuloRequestAndActivo(url, controller, accion, metodo, ModuloRequest.MODULO_MONITOREO_API, 1).orElse(new RequestMap())
                    if (requestMap.getId() == null) {
                        requestMap.url = url
                        requestMap.controller = controller
                        requestMap.accion = accion
                        requestMap.metodo = metodo
                        requestMap.activo = 1
                        requestMap.moduloRequest = ModuloRequest.MODULO_MONITOREO_API
                        requestMapRepository.save(requestMap)
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    static void main(String[] args) {
        SpringApplication.run(MmsApplication, args)
    }
}
