package univ.tuit.uschooltelegrambot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import univ.tuit.uschooltelegrambot.processor.Processor;


@Component
public class BotMain extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    private Long userId;

    private String channelId = "@kinoman_uzbek";

    @Autowired
    private Processor processor;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            userId = update.getMessage().getFrom().getId();

        } else if (update.hasCallbackQuery()) {
            userId = update.getCallbackQuery().getFrom().getId();
        }
        String userSubscribed = isUserSubscribed(userId, channelId);

        try {
            processor.processor(update, userSubscribed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }





     /*   SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(isSubscribed ? "You are subscribed to the channel!" : "You are not subscribed to the channel.");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }*/

    }

    public String isUserSubscribed(Long userId, String channelId) {
        String status = "";
        try {
            GetChatMember getChatMember = new GetChatMember(channelId, userId);
            ChatMember chatMember = execute(getChatMember);
            status = chatMember.getStatus();

        } catch (TelegramApiException e) {
            // Log error message and stack trace
            System.err.println("Telegram API request failed: " + e.getMessage());
            e.printStackTrace();
        }
        return status;
    }
}



