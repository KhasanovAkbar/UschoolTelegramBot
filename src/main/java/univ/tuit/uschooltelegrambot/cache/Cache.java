package univ.tuit.uschooltelegrambot.cache;

import java.util.List;

public interface Cache<T> {

    T add(T t);

    void remove(T t);

    T update(T t);

    T findBy(Long id, Integer sequence);

    T findBy(Long id);

    List<T> getAll();

}
