package dsi.edoMex.modulomonitoreo.saechvv.repository.administracion

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Selection

/**
 * Clase para consulta parametrizadas de los modelos, adjuntando a ella la consulta del total de registros obtenidos
 *
 *
 * @param <E>  Clase a la que se le hará la consulta
 */
class PagedList<E> implements Serializable, List<E> {

    private final EntityManager entityManager
    private Class<E> type
    private List<E> resultList
    private Closure closure
    private Long totalCount = Long.MIN_VALUE
    private boolean isSorting = false

    /**
     * Constructor de la clase. Realiza la consulta de los modelos
     *
     * @param entityManager Objeto de clase EntityManager
     * @param closure Objeto de clase Closure para la definición de sentencias aplicadas al WHERE
     * @param closureSorting Objeto de clase Closure para la definición de sentencias aplicadas posteriores al WHERE
     * @param type Clase a la cual se le hará la consulta
     * @param parametros Objeto de clase Map para obtener los datos referentes a la paginación
     */
    PagedList(EntityManager entityManager, Closure closure, Closure closureSorting, Class<E> type, Map parametros) {
        this.type = type
        this.entityManager = entityManager
        this.closure = closure
        this.isSorting = closureSorting != null

        resultList = entityManager.with {
            getCriteriaBuilder().with { criteriaBuilder ->
                createQuery(createQuery(type).with { criteriaQuery ->
                    from(type).with { root ->
                        if (!this.isSorting) {
                            def litgh = new LightPredicate(criteriaBuilder, root, criteriaQuery).with {
                                it.with this.closure
                                return it
                            }

                            orderBy(litgh.orders)
                            return where(litgh.predicates as Predicate[])
                        }

                        List<Predicate> predicates = new ArrayList<Predicate>()
                        this.closure(predicates, root, criteriaBuilder, criteriaQuery)
                        closureSorting(root, criteriaBuilder, criteriaQuery)
                        return where(predicates as Predicate[])
                    }
                }).with {
                    if (parametros.max)
                        setMaxResults( (parametros.max ?: 10) as int )
                                .setFirstResult( (parametros.offset ?: 0) as int )
                    return resultList
                }
            }
        }
    }

    /**
     * Realiza la consulta tipo COUNT con las mismas condiciones de la lista
     */
    int getTotalCount() {
        if (totalCount != Long.MIN_VALUE) {
            return totalCount
        }

        totalCount = entityManager.with {
            getCriteriaBuilder().with { criteriaBuilder ->
                createQuery(createQuery(Long).with { criteriaQuery ->
                    from(type).with { root ->
                        select(criteriaBuilder.countDistinct(root) as Selection<Long>)

                        if (!this.isSorting) {
                            def litgh = new LightPredicate(criteriaBuilder, root, criteriaQuery).with {
                                it.with this.closure
                                return it
                            }

                            return where(litgh.predicates as Predicate[])
                        }

                        List<Predicate> predicates = new ArrayList<Predicate>()
                        this.closure(predicates, root, criteriaBuilder, criteriaQuery)
                        return where(predicates as Predicate[])
                    }
                }).singleResult
            }
        }

        return totalCount
    }

    /**
     * Sobreescribe el metodo #super.get
     */
    @Override
    E get(int i) {
        return resultList.get(i)
    }

    /**
     * Sobreescribe el metodo #super.set
     */
    @Override
    E set(int i, E o) {
        return resultList.set(i, o)
    }

    /**
     * Sobreescribe el metodo #super.remove
     */
    @Override
    E remove(int i) {
        return resultList.remove(i)
    }

    /**
     * Sobreescribe el metodo #super.indexOf
     */
    @Override
    int indexOf(Object o) {
        return resultList.indexOf(o)
    }

    /**
     * Sobreescribe el metodo #super.lastIndexOf
     */
    @Override
    int lastIndexOf(Object o) {
        return resultList.lastIndexOf(o)
    }

    /**
     * Sobreescribe el metodo #super.listIterator
     */
    @Override
    ListIterator<E> listIterator() {
        return resultList.listIterator()
    }

    /**
     * Sobreescribe el metodo #super.listIterator
     */
    @Override
    ListIterator<E> listIterator(int index) {
        return resultList.listIterator(index)
    }

    /**
     * Sobreescribe el metodo #super.subList
     */
    @Override
    List<E> subList(int fromIndex, int toIndex) {
        return resultList.subList(fromIndex, toIndex)
    }

    /**
     * Sobreescribe el metodo #super.add
     */
    @Override
    void add(int i, E o) {
        resultList.add(i, o)
    }

    /**
     * Sobreescribe el metodo #super.size
     */
    @Override
    int size() {
        return resultList.size()
    }

    /**
     * Sobreescribe el metodo #super.isEmpty
     */
    @Override
    boolean isEmpty() {
        return size() == 0
    }

    /**
     * Sobreescribe el metodo #super.contains
     */
    @Override
    boolean contains(Object o) {
        return resultList.contains(o)
    }

    /**
     * Sobreescribe el metodo #super.iterator
     */
    @Override
    Iterator<E> iterator() {
        return resultList.iterator()
    }

    /**
     * Sobreescribe el metodo #super.toArray
     */
    @Override
    Object[] toArray() {
        return resultList.toArray()
    }

    /**
     * Sobreescribe el metodo #super.>
     */
    @Override
    <T> T[] toArray(T[] a) {
        return resultList.toArray(a)
    }

    /**
     * Sobreescribe el metodo #super.add
     */
    @Override
    boolean add(E e) {
        return resultList.add(e)
    }

    /**
     * Sobreescribe el metodo #super.remove
     */
    @Override
    boolean remove(Object o) {
        return resultList.remove(o)
    }

    /**
     * Sobreescribe el metodo #super.containsAll
     */
    @Override
    boolean containsAll(Collection<?> c) {
        return resultList.containsAll(c)
    }

    /**
     * Sobreescribe el metodo #super.addAll
     */
    @Override
    boolean addAll(Collection<? extends E> c) {
        return resultList.addAll(c)
    }

    /**
     * Sobreescribe el metodo #super.addAll
     */
    @Override
    boolean addAll(int index, Collection<? extends E> c) {
        return resultList.addAll(index, c)
    }

    /**
     * Sobreescribe el metodo #super.removeAll
     */
    @Override
    boolean removeAll(Collection<?> c) {
        return resultList.removeAll(c)
    }

    /**
     * Sobreescribe el metodo #super.retainAll
     */
    @Override
    boolean retainAll(Collection<?> c) {
        return resultList.retainAll(c)
    }

    /**
     * Sobreescribe el metodo #super.clear
     */
    @Override
    void clear() {
        resultList.clear()
    }

    /**
     * Sobreescribe el metodo #super.equals
     */
    @Override
    boolean equals(Object o) {
        return resultList.equals(o)
    }

    /**
     * Sobreescribe el metodo #super.hashCode
     */
    @Override
    int hashCode() {
        return resultList.hashCode()
    }
}