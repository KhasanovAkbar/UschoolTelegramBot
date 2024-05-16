package univ.tuit.uschooltelegrambot.processor;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Processor {

    void executeQuery(Message message, String status) throws Exception;

    void executeCallBackQuery(CallbackQuery callbackQuery, String status);

    default void processor(Update update, String status) throws Exception {
        if (update.hasMessage()) {
            executeQuery(update.getMessage(), status);
        } else if (update.hasCallbackQuery()) {
            executeCallBackQuery(update.getCallbackQuery(), status);
        }
    }
}
