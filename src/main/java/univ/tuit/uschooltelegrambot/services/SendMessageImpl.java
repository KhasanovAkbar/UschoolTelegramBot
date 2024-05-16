package univ.tuit.uschooltelegrambot.services;

public interface SendMessageImpl<T> {

    void start(T t,String status);
    void aboutUs(T t);
    void restart(T t);
    void contactUs(T t);
    void others(T t) throws Exception;

    void notSubscribed(T t);

}
