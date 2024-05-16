package univ.tuit.uschooltelegrambot.services;

public interface SendMessageAdminImpl<T> {
    void addCinema(T t) throws Exception;
    void statistics(T t) throws Exception;
    void start(T t);
    void removeCinema(T t);
    void countOfCinema(T t) throws Exception;

}
