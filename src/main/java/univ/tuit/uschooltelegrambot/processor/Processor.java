package univ.tuit.uschooltelegrambot.processor;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Processor {

    void executeQuery(Message message);

    void executeCallBackQuery(CallbackQuery callbackQuery);

    default void processor(Update update) {
        if (update.hasMessage()) {
            executeQuery(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            executeCallBackQuery(update.getCallbackQuery());
        }
    }
}
