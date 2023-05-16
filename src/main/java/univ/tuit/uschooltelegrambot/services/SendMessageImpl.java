package univ.tuit.uschooltelegrambot.services;

public interface SendMessageImpl<T> {

    void start(T t, boolean check);

    void register(T t);

    void aboutUs(T t);

    void restart(T t);

    void priceList(T t);

    void contactUs(T t);

    void location(T t);

    void others(T t);

    void listUser(T t);
}
