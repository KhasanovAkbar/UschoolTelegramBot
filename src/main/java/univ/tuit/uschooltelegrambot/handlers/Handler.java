package univ.tuit.uschooltelegrambot.handlers;

public interface Handler<T> {

    void choose(T t, boolean check);
}
