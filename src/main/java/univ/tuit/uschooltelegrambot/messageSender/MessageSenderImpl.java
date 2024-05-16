package univ.tuit.uschooltelegrambot.messageSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import univ.tuit.uschooltelegrambot.BotMain;

@Service
public class MessageSenderImpl implements MessageSender {

    @Autowired
    private BotMain botMain;

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {
            botMain.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEditMessage(EditMessageText editMessageText) {
        try {
            botMain.execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLocation(SendLocation sendLocation) {
        try {
            botMain.execute(sendLocation);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendVideo(SendVideo sendVideo) {
        try {
            botMain.execute(sendVideo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
