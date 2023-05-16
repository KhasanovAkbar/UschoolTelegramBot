package univ.tuit.uschooltelegrambot.processor;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import univ.tuit.uschooltelegrambot.handlers.CallBackQueryHandler;
import univ.tuit.uschooltelegrambot.handlers.MessageHandler;

@Component
public class DefaultProcessor implements Processor {

    private final CallBackQueryHandler callBackQueryHandler;
    private final MessageHandler messageHandler;

    public DefaultProcessor(CallBackQueryHandler callBackQueryHandler, MessageHandler messageHandler) {
        this.callBackQueryHandler = callBackQueryHandler;
        this.messageHandler = messageHandler;
    }

    @Override
    public void executeQuery(Message message, boolean channelCheck) {
        messageHandler.choose(message, channelCheck);

    }

    @Override
    public void executeCallBackQuery(CallbackQuery callbackQuery, boolean channelCheck) {
        callBackQueryHandler.choose(callbackQuery, channelCheck);
    }
}
