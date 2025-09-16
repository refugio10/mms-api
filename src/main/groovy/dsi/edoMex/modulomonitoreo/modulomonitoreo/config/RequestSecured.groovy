package dsi.edoMex.modulomonitoreo.modulomonitoreo.config

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Contiene la inicialización de urls que pueden ser ocupados con una sesión iniciada
 *
 * @author lorenav
 * @version 1.0 15/11/2024
 */
@Component
class RequestSecured {
    def requestPermit = [:]

    /**
     * Lectura de los endpoints que tiene la etiqueta Secured ["permitAll", "isAuthenticated()"]
     */
    RequestSecured() {
        try {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false)
            scanner.addIncludeFilter(new AnnotationTypeFilter(RequestMapping.class))

            def tipoSeguridad = ["permitAll", "isAuthenticated()"]

            for (BeanDefinition beanDefinition : scanner.findCandidateComponents("dsi.edoMex.mms.mia.controller")) {
                Class classNew = Class.forName(beanDefinition.getBeanClassName())

                String pathPrincipal = (classNew.getAnnotation(RequestMapping.class) != null) ?
                        ((RequestMapping) classNew.getAnnotation(RequestMapping.class)).value()[0].toString() : ""

                def annotations = classNew.getMethods().findAll(it ->
                        (it.getAnnotation(GetMapping.class) != null || it.getAnnotation(PostMapping.class) != null ||
                                it.getAnnotation(PutMapping.class) != null || it.getAnnotation(PatchMapping.class) != null ||
                                it.getAnnotation(DeleteMapping.class) != null)
                                && it.getAnnotation(Secured.class) != null
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

                    if (it.getAnnotations()[1] instanceof Secured) {
                        Secured secured = it.getAnnotation(Secured.class)
                        if (tipoSeguridad.contains(secured.value()[0])) {
                            String url = pathPrincipal + mapping?.value()[0]
                            requestPermit.put(url, metodo)
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}