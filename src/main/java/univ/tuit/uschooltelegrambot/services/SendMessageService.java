package univ.tuit.uschooltelegrambot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import univ.tuit.uschooltelegrambot.BotMain;
import univ.tuit.uschooltelegrambot.constants.ButtonState;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.messageSender.MessageSender;
import univ.tuit.uschooltelegrambot.store.BotUserStore;
import univ.tuit.uschooltelegrambot.store.CinemaStore;
import univ.tuit.uschooltelegrambot.store.dto.CinemaDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SendMessageService implements SendMessageImpl<Message> {

    private final BotMain botMain;

    private final MessageSender messageSender;
    private final BotUserStore botUserStore;

    private final CinemaStore cinemaStore;

    private Long userId;
    private final BotUser user = new BotUser();

    public SendMessageService(BotMain botMain, MessageSender messageSender, BotUserStore botUserStore, CinemaStore cinemaStore) {
        this.botMain = botMain;
        this.messageSender = messageSender;
        this.botUserStore = botUserStore;
        this.cinemaStore = cinemaStore;
    }

    ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
    ArrayList<KeyboardRow> keyboardRow = new ArrayList<>();


    @Override
    public void start(Message message, String status) {

        userId = message.getChatId();
        SendMessage sendMessage = new SendMessage();

        keyboardRow.clear();

        user.setUserId(userId);
        User from = message.getFrom();
        user.setUsername(from.getUserName());
        user.setName(from.getFirstName());

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String format = dateFormat.format(date);
        user.setRegistrationTime(format);
        botUserStore.add(user);

        sendMessage.setReplyMarkup(null);

        if (message.getText().equals(ButtonState.START) && status.equals("member")) {
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


        } else if (status.equals("member")) {

            DeleteMessage deleteMessage = new DeleteMessage(userId.toString(), message.getMessageId());
            userId = message.getChatId();

            Chat chat = message.getChat();
            BotUser byUserId = botUserStore.findBy(userId).toDomain();


            if (byUserId == null) {
                chat.getFirstName();
                byUserId.setUserId(userId);
                byUserId.setName(chat.getFirstName());
                byUserId.setUsername(chat.getUserName());

                byUserId.setRegistrationTime(format);
                byUserId = new BotUser();
                byUserId.setUserId(userId);
                botUserStore.add(byUserId);
            }
            try {
                botMain.execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            sendMessage.setReplyMarkup(buttons());


        } else {
            InlineKeyboardMarkup markup = getInlineKeyboardMarkup();
            keyboardRow.clear();

            sendMessage.setText("Botdan foydalanish uchun kanalga a'zo bo'ling");
            sendMessage.setReplyMarkup(markup);

        }
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);

    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button1.setText("A'zo bo'lish");
        button1.setUrl("https://t.me/kinoman_uzbek");
        button1.setCallbackData("/Subscribe");

        button2.setText("Tasdiqlash✅");
        button2.setCallbackData("/check");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button1);
        row.add(button2);
        markup.setKeyboard(Collections.singletonList(row));
        return markup;
    }


    @Override
    public void aboutUs(Message message) {
        long chat_id = message.getChatId();

        messageSender.sendMessage(SendMessage.builder()
                .text("About")
                .chatId(String.valueOf(chat_id))
                .build());
    }

    @Override
    public void restart(Message message) {
        //delete from db
        start(message, "");

    }

    @Override
    public void contactUs(Message message) {
        long chat_id = message.getChatId();

        messageSender.sendMessage(SendMessage.builder().text(
                                "Bot va kanal egasi\n" +
                                        "Telegram - @Khayitboy_off\n"
                               )
                .parseMode("HTML")
                .chatId(String.valueOf(chat_id)).build());

    }

    @Override
    public void others(Message message) throws Exception {
        //
        CinemaDto byCode = cinemaStore.findByCode(message.getText());
        if (byCode != null) {

            long chat_id = message.getChatId();

            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(String.valueOf(chat_id));
            InputFile inputFile = new InputFile(byCode.getCinemaId());
            sendVideo.setVideo(inputFile);
            sendVideo.setCaption(byCode.getVideoInfo());
            sendVideo.setReplyToMessageId(message.getMessageId());
            messageSender.sendVideo(sendVideo);


        } else {
            long chat_id = message.getChatId();
            messageSender.sendMessage(SendMessage.builder()
                    .text("Bunday kino mavjud emas")
                    .chatId(String.valueOf(chat_id))
                    .build());

        }


    }

    @Override
    public void notSubscribed(Message message) {
        //
        userId = message.getChatId();
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup();
        messageSender.sendMessage(SendMessage.builder()
                .text("Botdan foydalanish uchun kanalga a'zo bo'ling")
                .chatId(String.valueOf(userId))
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }


    private void sendMsg(String text, String id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText(text);
        messageSender.sendMessage(sendMessage);

    }

    private ReplyKeyboardMarkup buttons() {

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();


        row1.add(KeyboardButton.builder().text("Contact us").build());

        row2.add(KeyboardButton.builder().text("About us").build());

        keyboardRow.add(row1);
        keyboardRow.add(row2);

        markup.setKeyboard(keyboardRow);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
}
