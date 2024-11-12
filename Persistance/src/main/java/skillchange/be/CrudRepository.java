package skillchange.be;

import java.util.List;

public interface CrudRepository<ID, E> {
    void save(E e);

    void update(E e);

    void delete(ID id);

    void findById(ID id);

    List<E> getAll();
}
