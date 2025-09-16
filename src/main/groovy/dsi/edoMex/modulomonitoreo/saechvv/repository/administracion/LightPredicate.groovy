package dsi.edoMex.modulomonitoreo.saechvv.repository.administracion

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Order
import jakarta.persistence.criteria.Predicate

/**
 * Genera los predicados del apartado where de una consulta select, reducidamente
 *
 * @author Felipe Ocampo Araujo
 * @version 1.0  19 / 11 / 2024
 */
class LightPredicate {

    private List<Predicate> predicates
    protected List<Order> orders
    private CriteriaBuilder builder
    private From root
    private CriteriaQuery query
    private final LogicOperator actualLogicOperator

    private LightPredicate(CriteriaBuilder builder, From root, CriteriaQuery criteriaQuery,
                           LogicOperator actualLogicOperator) {
        this.predicates = new ArrayList<Predicate>()
        this.orders = orders = new ArrayList<Order>()
        this.builder = builder
        this.root = root
        this.query = criteriaQuery
        this.actualLogicOperator = actualLogicOperator
    }

    LightPredicate(CriteriaBuilder builder, From root, CriteriaQuery criteriaQuery) {
        this(builder, root, criteriaQuery, LogicOperator.AND)
    }

    List<Predicate> getPredicates() {
        return predicates
    }

    CriteriaBuilder getBuilder() {
        return builder
    }

    From getRoot() {
        return root
    }

    /**
     * inner join
     * @param model
     * @param closure
     */
    void join(String model, @DelegatesTo(LightPredicate) Closure closure) {
        root.join(model).with { joined ->
            def light = new LightPredicate(this.builder, joined, this.query, this.actualLogicOperator).with {
                it.with closure
                return it
            }
            this.predicates.addAll(light.predicates)
            this.orders.addAll(light.orders)
        }
    }

    /**
     * Executes an exists subquery
     *
     * @param subquery The subquery
     * @return this criteria
     */
    //void exists(QueryableCriteria<?> subquery);

    /**
     * Executes a not exists subquery
     *
     * @param subquery The subquery
     * @return this criteria
     */
    //void notExists(QueryableCriteria<?> subquery);

    /**
     * Creates a criterion that restricts the id to the given value
     * @param value The value
     * @return The criteria
     */
    //void idEquals(Object value);

    /**
     * Creates a criterion that asserts the given property is empty (such as a blank string)
     *
     * @param propertyName The property name
     * @return The criteria
     */
    //void isEmpty(String propertyName);

    /**
     * Creates a criterion that asserts the given property is not empty
     *
     * @param propertyName The property name
     * @return The criteria
     */
    //void isNotEmpty(String propertyName);

    /**
     * Creates a criterion that asserts the given property is null
     *
     * @param propertyName The property name
     * @return The criteria
     */
    void isNull(String propertyName) {
        predicates << builder.isNull(root.get(propertyName))
    }

    /**
     * Creates a criterion that asserts the given property is not null
     *
     * @param propertyName The property name
     * @return The criteria
     */
    //void isNotNull(String propertyName);

    /**
     * Creates an "equals" Criterion based on the specified property name and value
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return The criteria
     */
    void eq(String propertyName, Object propertyValue) {
        predicates << builder.equal(root.get(propertyName), propertyValue)
    }

    /**
     * Creates an "equals" Criterion based on the specified property name and value
     *
     * @param propertyValue The property value
     *
     * @return The criteria
     */
    //void idEq(Object propertyValue);

    /**
     * Creates a "not equals" Criterion based on the specified property name and value
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return The criteria
     */
    void ne(String propertyName, Object propertyValue) {
        predicates << builder.notEqual(root.get(propertyName), propertyValue)
    }

    /**
     * Restricts the results by the given property value range (inclusive)
     *
     * @param propertyName The property name
     *
     * @param start The start of the range
     * @param finish The end of the range
     * @return The criteria
     */
    void between(String propertyName, Object start, Object finish) {
        predicates << builder.between(root.get(propertyName), start as Comparable, finish as Comparable)
    }

    /**
     * Used to restrict a value to be greater than or equal to the given value
     * @param property The property
     * @param value The value
     * @return The Criterion instance
     */
    void gte(String property, Object value) {
        predicates << builder.greaterThanOrEqualTo(root.get(property), value as Comparable)
    }

    /**
     * Used to restrict a value to be greater than or equal to the given value
     * @param property The property
     * @param value The value
     * @return The Criterion instance
     */
    void ge(String property, Object value) {
        gte(property, value)
    }

    /**
     * Used to restrict a value to be greater than or equal to the given value
     * @param property The property
     * @param value The value
     * @return The Criterion instance
     */
    void gt(String property, Object value) {
        predicates << builder.greaterThan(root.get(property), value as Comparable)
    }

    /**
     * Used to restrict a value to be less than or equal to the given value
     * @param property The property
     * @param value The value
     * @return The Criterion instance
     */
    void lte(String property, Object value) {
        predicates << builder.lessThanOrEqualTo(root.get(property), value as Comparable)
    }

    /**
     * Used to restrict a value to be less than or equal to the given value
     * @param property The property
     * @param value The value
     * @return The Criterion instance
     */
    void le(String property, Object value) {
        lte(property, value)
    }

    /**
     * Used to restrict a value to be less than or equal to the given value
     * @param property The property
     * @param value The value
     * @return The Criterion instance
     */
    void lt(String property, Object value) {
        predicates << builder.lessThan(root.get(property), value as Comparable)
    }

    /**
     * Creates a like Criterion based on the specified property name and value
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return The criteria
     */
    void like(String propertyName, String propertyValue) {
        predicates << builder.like(root.get(propertyName), propertyValue)
    }

    /**
     * Creates an ilike Criterion based on the specified property name and value. Unlike a like condition, ilike is case insensitive
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return The criteria
     */
    void ilike(String propertyName, String propertyValue) {
        predicates << builder.like(builder.upper(root.get(propertyName)), propertyValue.toUpperCase())
    }

    /**
     * Creates an rlike Criterion based on the specified property name and value
     * SqlServer not suported
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return The criteria
     */
    //void rlike(String propertyName, Object propertyValue);

    /**
     * Creates a logical conjunction
     * @param callable The closure
     *
     * @return This criteria
     */
    void and(@DelegatesTo(LightPredicate) Closure callable) {
        def light = new LightPredicate(this.builder, root, this.query, this.actualLogicOperator).with {
            it.with callable
            return it
        }

        this.orders.addAll(light.orders)
        predicates << builder.and(light.predicates as Predicate[])
    }

    /**
     * Creates a logical disjunction
     * @param callable The closure
     * @return This criteria
     */
    void or(@DelegatesTo(LightPredicate) Closure callable) {
        def light = new LightPredicate(this.builder, root, this.query, this.actualLogicOperator).with {
            it.with callable
            return it
        }

        this.orders.addAll(light.orders)
        predicates << builder.or(light.predicates as Predicate[])
    }

    /**
     * Creates a logical negation
     * @param callable The closure
     * @return This criteria
     */
    void not(@DelegatesTo(LightPredicate) Closure callable) {
        def light = new LightPredicate(this.builder, root, this.query, this.actualLogicOperator).with {
            it.with callable
            return it
        }

        if (actualLogicOperator == LogicOperator.AND)
            predicates << builder.not(builder.and(light.predicates as Predicate[]))
        else
            predicates << builder.not(builder.or(light.predicates as Predicate[]))
    }


    /**
     * Creates an "in" Criterion using a subquery
     *
     * @param propertyName The property name
     * @param subquery The subquery
     *
     * @return The criteria
     */
    //void inList(String propertyName, QueryableCriteria<?> subquery);


    /**
     * Creates an "in" Criterion using a subquery
     *
     * @param propertyName The property name
     * @param subquery The subquery
     *
     * @return The criteria
     */
    //void inList(String propertyName, Closure<?> subquery);

    /**
     * Creates an "in" Criterion based on the specified property name and list of values
     *
     * @param propertyName The property name
     * @param values The values
     *
     * @return The criteria
     */
    void inList(String propertyName, Collection values) {
        predicates << root.get(propertyName).in(values)
    }

    /**
     * Creates an "in" Criterion based on the specified property name and list of values
     *
     * @param propertyName The property name
     * @param values The values
     *
     * @return The criteria
     */
    void inList(String propertyName, Object... values) {
        predicates << root.get(propertyName).in(values)
    }

    /**
     * Creates an "in" Criterion based on the specified property name and list of values
     *
     * @param propertyName The property name
     * @param values The values
     *
     * @return The criteria
     */
    //void in(String propertyName, Object[] values);

    /**
     * Creates a negated "in" Criterion using a subquery
     *
     * @param propertyName The property name
     * @param subquery The subquery
     *
     * @return The criteria
     */
    //void notIn(String propertyName, QueryableCriteria<?> subquery);

    /**
     * Creates a negated "in" Criterion using a subquery
     *
     * @param propertyName The property name
     * @param subquery The subquery
     *
     * @return The criteria
     */
    //void notIn(String propertyName, Closure<?> subquery);

    /**
     * Orders by the specified property name (defaults to ascending)
     *
     * @param propertyName The property name to order by
     * @param direction ASC default, or DESC
     * @return This criteria
     */
    void order(String propertyName, String direction = "asc") {
        orders << builder."$direction"(root.get(propertyName))
    }

    /**
     * Orders by the specified property name and direction
     *
     * @param propertyName The property name to order by
     * @param direction Either "asc" for ascending or "desc" for descending
     *
     * @return This criteria
     */
    //void order(String propertyName, String direction);

    /**
     * Creates a Criterion that constrains a collection property by size
     *
     * @param propertyName The property name
     * @param size The size to constrain by
     *
     * @return This criteria
     */
    //void sizeEq(String propertyName, int size) ;

    /**
     * Creates a Criterion that constrains a collection property to be greater than the given size
     *
     * @param propertyName The property name
     * @param size The size to constrain by
     *
     * @return This criteria
     */
    //void sizeGt(String propertyName, int size);

    /**
     * Creates a Criterion that constrains a collection property to be greater than or equal to the given size
     *
     * @param propertyName The property name
     * @param size The size to constrain by
     *
     * @return This criteria
     */
    //void sizeGe(String propertyName, int size);

    /**
     * Creates a Criterion that constrains a collection property to be less than or equal to the given size
     *
     * @param propertyName The property name
     * @param size The size to constrain by
     *
     * @return This criteria
     */
    //void sizeLe(String propertyName, int size);

    /**
     * Creates a Criterion that constrains a collection property to be less than to the given size
     *
     * @param propertyName The property name
     * @param size The size to constrain by
     *
     * @return This criteria
     */
    //void sizeLt(String propertyName, int size);

    /**
     * Creates a Criterion that constrains a collection property to be not equal to the given size
     *
     * @param propertyName The property name
     * @param size The size to constrain by
     *
     * @return This criteria
     */
    //void sizeNe(String propertyName, int size);

    /**
     * Constrains a property to be equal to a specified other property
     *
     * @param propertyName The property
     * @param otherPropertyName The other property
     * @return This criteria
     */
    //void eqProperty(java.lang.String propertyName, java.lang.String otherPropertyName);

    /**
     * Constrains a property to be not equal to a specified other property
     *
     * @param propertyName The property
     * @param otherPropertyName The other property
     * @return This criteria
     */
    //void neProperty(java.lang.String propertyName, java.lang.String otherPropertyName);

    /**
     * Constrains a property to be greater than a specified other property
     *
     * @param propertyName The property
     * @param otherPropertyName The other property
     * @return This criteria
     */
    //void gtProperty(java.lang.String propertyName, java.lang.String otherPropertyName);

    /**
     * Constrains a property to be greater than or equal to a specified other property
     *
     * @param propertyName The property
     * @param otherPropertyName The other property
     * @return This criteria
     */
    //void geProperty(java.lang.String propertyName, java.lang.String otherPropertyName);

    /**
     * Constrains a property to be less than a specified other property
     *
     * @param propertyName The property
     * @param otherPropertyName The other property
     * @return This criteria
     */
    //void ltProperty(java.lang.String propertyName, java.lang.String otherPropertyName);

    /**
     * Constrains a property to be less than or equal to a specified other property
     *
     * @param propertyName The property
     * @param otherPropertyName The other property
     * @return This criteria
     */
    //void leProperty(java.lang.String propertyName, java.lang.String otherPropertyName);

    /**
     * Apply an "equals" constraint to each property in the key set of a <tt>Map</tt>
     *
     * @param propertyValues a map from property names to values
     *
     * @return Criterion
     *
     * @see {org.grails.datastore.mapping.query.Query.Conjunction}
     */
    //void allEq(Map<String, Object> propertyValues);


    //===== Subquery methods

    /**
     * Creates a subquery criterion that ensures the given property is equals to all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue A closure that is converted to a {org.grails.datastore.mapping.query.api.QueryableCriteria}
     * @return This criterion instance
     */
    //void eqAll(String propertyName, Closure<?> propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is greater than all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue A closure that is converted to a {org.grails.datastore.mapping.query.api.QueryableCriteria}
     * @return This criterion instance
     */
    //void gtAll(String propertyName, Closure<?> propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is less than all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue A closure that is converted to a {org.grails.datastore.mapping.query.api.QueryableCriteria}
     * @return This criterion instance
     */
    //void ltAll(String propertyName, Closure<?> propertyValue);
    /**
     * Creates a subquery criterion that ensures the given property is greater than or equals to all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue A closure that is converted to a {org.grails.datastore.mapping.query.api.QueryableCriteria}
     * @return This criterion instance
     */
    //void geAll(String propertyName, Closure<?> propertyValue);
    /**
     * Creates a subquery criterion that ensures the given property is less than or equal to all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue A closure that is converted to a {org.grails.datastore.mapping.query.api.QueryableCriteria}
     * @return This criterion instance
     */
    //void leAll(String propertyName, Closure<?> propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is equal to all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void eqAll(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is greater than all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void gtAll(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is less than all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void ltAll(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is greater than all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void geAll(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is less than all the given returned values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void leAll(String propertyName, QueryableCriteria propertyValue);


    /**
     * Creates a subquery criterion that ensures the given property is greater than some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void gtSome(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is greater than some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void gtSome(String propertyName, Closure<?> propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is greater than or equal to some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void geSome(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is greater than or equal to some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void geSome(String propertyName, Closure<?> propertyValue);


    /**
     * Creates a subquery criterion that ensures the given property is less than some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void ltSome(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is less than some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void ltSome(String propertyName, Closure<?> propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is less than or equal to some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void leSome(String propertyName, QueryableCriteria propertyValue);

    /**
     * Creates a subquery criterion that ensures the given property is less than or equal to some of the given values
     *
     * @param propertyName The property name
     * @param propertyValue The property value
     *
     * @return This Criteria instance
     */
    //void leSome(String propertyName, Closure<?> propertyValue);

    /**
     * <p>Configures the second-level cache with the default usage of 'read-write' and the default include of 'all' if
     *  the passed argument is true
     *
     * <code> { cache true } </code>
     *
     * @param shouldCache True if the default cache configuration should be applied
     *
     * @return This Criteria instance
     */
    //void cache(boolean shouldCache);

    /**
     * <p>Configures the hibernate readOnly property to avoid checking for changes on the objects if the passed argument is true
     *
     * <code> { readOnly true } </code>
     *
     * @param readOnly True to disable dirty checking
     *
     * @return This Criteria instance
     */
    //void readOnly(boolean readOnly);
}

enum LogicOperator {
    AND, OR
}