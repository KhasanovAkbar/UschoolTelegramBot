package univ.tuit.uschooltelegrambot.store;

import java.util.List;

public interface BotUserStore<T> {

    T add(T t);

    T findBy(Long id);

    List<T> getAll();

}
