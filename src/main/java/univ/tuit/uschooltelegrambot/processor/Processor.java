package univ.tuit.uschooltelegrambot.processor;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Processor {

    void executeQuery(Message message, boolean channelCheck);

    void executeCallBackQuery(CallbackQuery callbackQuery, boolean channelCheck);

    default void processor(Update update, boolean channelCheck) {
        if (update.hasMessage()) {
            executeQuery(update.getMessage(), channelCheck);
        } else if (update.hasCallbackQuery()) {
            executeCallBackQuery(update.getCallbackQuery(), channelCheck);
        }
    }
}
