package univ.tuit.uschooltelegrambot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import univ.tuit.uschooltelegrambot.BotMain;
import univ.tuit.uschooltelegrambot.messageSender.MessageSender;
import univ.tuit.uschooltelegrambot.services.SendMessageService;
import univ.tuit.uschooltelegrambot.store.BotUserStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CallBackQueryHandler implements Handler<CallbackQuery> {

    private final BotMain botMain;

    private final MessageSender messageSender;
    private long userId;

    @Autowired
    private final BotUserStore botUserStore;

    @Autowired
    private SendMessageService sendMessageService;


    public CallBackQueryHandler(BotMain botMain, MessageSender messageSender, BotUserStore botUserStore) {
        this.botMain = botMain;
        this.messageSender = messageSender;
        this.botUserStore = botUserStore;
    }

    @Override
    public void choose(CallbackQuery callbackQuery, String status) {
        //
        Message message = (Message) callbackQuery.getMessage();
        String user_first_name = message.getChat().getFirstName();
        userId = message.getChat().getId();
        String message_text = message.getText();

        if (message.hasText()) {
            String callbackQueryId = callbackQuery.getId();

            userId = message.getChatId();

            if (!status.equals("member")) {
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
                //

                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Salom " + message.getChat().getFirstName() +
                        "\nMarhamat kerakli kodni yuboring yoki kanal ichidan qarang");

                InlineKeyboardButton button1 = new InlineKeyboardButton();
                button1.setText("\uD83D\uDD0E Kodlarni qidirish");
                button1.setUrl("https://t.me/kinoman_uzbek");
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(button1);
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(Collections.singletonList(row));
                sendMessage.setReplyMarkup(markup);
                sendMessage.setChatId(userId);
                messageSender.sendMessage(sendMessage);
            }
        }
    }
}
