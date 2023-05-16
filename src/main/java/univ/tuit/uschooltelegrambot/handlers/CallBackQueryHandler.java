package univ.tuit.uschooltelegrambot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import univ.tuit.uschooltelegrambot.BotMain;
import univ.tuit.uschooltelegrambot.constants.ButtonState;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.services.SendMessageService;
import univ.tuit.uschooltelegrambot.store.BotUserStore;

import java.util.List;

import static univ.tuit.uschooltelegrambot.handlers.MessageHandler.log;

@Component
public class CallBackQueryHandler implements Handler<CallbackQuery> {

    private final BotMain botMain;

    private long user_id;
    @Autowired
    private final BotUserStore<BotUser> botUserStore;

    @Autowired
    private SendMessageService sendMessageService;


    public CallBackQueryHandler(BotMain botMain, BotUserStore<BotUser> botUserStore) {
        this.botMain = botMain;
        this.botUserStore = botUserStore;
    }

    @Override
    public void choose(CallbackQuery callbackQuery, boolean channelCheck) {
        Message message = callbackQuery.getMessage();
        String user_first_name = message.getChat().getFirstName();
        String user_last_name = message.getChat().getLastName();
        user_id = message.getChat().getId();
        String message_text = message.getText();

        if (message.hasText()) {
            String callbackQueryId = callbackQuery.getId();

            if (!channelCheck) {
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                answer.setCallbackQueryId(callbackQueryId);
                answer.setShowAlert(true);
                answer.setText("Kanalga a'zo bo'lmagansiz!");

                try {
                    botMain.execute(answer);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                log(user_first_name, user_last_name, Long.toString(user_id), message_text);
                List<BotUser> all = botUserStore.getAll();
                BotUser lastUser = new BotUser();
                for (BotUser botUser : all) {
                    if (botUser.getUserId().equals(user_id)) {
                        lastUser = botUser;
                        break;
                    }
                }
                if (lastUser.getState().equals(ButtonState.START)) {
                    sendMessageService.start(message, channelCheck);
                }
            }


        }
    }
}
