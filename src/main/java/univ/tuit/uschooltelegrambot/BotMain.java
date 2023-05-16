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

    private String channelId = "@JavohirsNotes";

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
        boolean isSubscribed = isUserSubscribed(userId, channelId);
        processor.processor(update, isSubscribed);


     /*   SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(isSubscribed ? "You are subscribed to the channel!" : "You are not subscribed to the channel.");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }*/

    }

    public boolean isUserSubscribed(Long userId, String channelId) {
        try {
            GetChatMember getChatMember = new GetChatMember(channelId, userId);
            ChatMember chatMember = execute(getChatMember);
            String status = chatMember.getStatus();
            return status.equals("member") || status.equals("administrator") || status.equals("creator");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }
}



