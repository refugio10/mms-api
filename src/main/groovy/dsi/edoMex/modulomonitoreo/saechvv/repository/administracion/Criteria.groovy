package dsi.edoMex.modulomonitoreo.saechvv.repository.administracion

import groovyjarjarantlr4.v4.runtime.misc.NotNull
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

/**
 * Contiene funciones genéricos para realizar consultas a la base de datos mediante el uso de Criterias
 *
 * @author Felipe Ocampo Araujo
 * @version 1.0  19 / 11 / 2024
 */
@Repository
class Criteria {

    @Autowired
    private EntityManager entityManager

    /**
     * Construye una lista de objetos según se haya definido en la CLASE y parametrizado y el estereotipo indicado en los parámetros
     *
     * @param type Clase sobre la cual se gerará la consulta
     * @param closure Representa los sentencias que se ejecutarán dentro del WHERE
     * @param closureSorting Representa los sentencias que se ejecutarán posterior al where
     * (Se mantiene separado para poder realizar un tipo de ordenamiento)
     * @return Objeto de clase PageList
     */
    def <T> PagedList<T> list(Class<T> type, Map parametros = [:], Closure closure, @NotNull Closure closureSorting) {
        return new PagedList<T>(entityManager, closure, closureSorting, type, parametros)
    }

    def <T> PagedList<T> list(Class<T> type, Map parametros = [:], @DelegatesTo(LightPredicate) Closure closure) {
        return new PagedList<T>(entityManager, closure, null, type, parametros)
    }
}
