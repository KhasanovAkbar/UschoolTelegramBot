package univ.tuit.uschooltelegrambot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import univ.tuit.uschooltelegrambot.constants.Status;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.domain.Cinema;
import univ.tuit.uschooltelegrambot.messageSender.MessageSender;
import univ.tuit.uschooltelegrambot.store.BotUserStore;
import univ.tuit.uschooltelegrambot.store.CinemaStore;
import univ.tuit.uschooltelegrambot.store.dto.BotUserDto;
import univ.tuit.uschooltelegrambot.store.dto.CinemaDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class SendMessageAdminService implements SendMessageAdminImpl<Message> {

    private Long userId;
    ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
    ArrayList<KeyboardRow> keyboardRow = new ArrayList<>();
    private final MessageSender messageSender;

    @Autowired
    private CinemaStore cinemaStore;

    @Autowired
    private BotUserStore userStore;

    public SendMessageAdminService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void start(Message message) {
        //
        userId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Welcome to Admin Panel");
        BotUser user = new BotUser();
        Chat chat = message.getChat();
        user.setUsername(chat.getUserName());
        user.setName(chat.getFirstName());
        user.setUserId(chat.getId());

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String format = dateFormat.format(date);
        user.setRegistrationTime(format);
        user.setStatus(Status.NONE.toString());
        userStore.add(user);

        keyboardRow.clear();
        sendMessage.setReplyMarkup(buttons());
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);
    }

    @Override
    public void removeCinema(Message message) {
        //
        userId = message.getChatId();
        BotUserDto by = userStore.findBy(userId);
        by.setStatus(Status.REMOVE.toString());
        userStore.add(by.toDomain());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Kino id ni yuboring:");
        keyboardRow.clear();
        sendMessage.setReplyMarkup(buttons());
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);
    }

    @Override
    public void countOfCinema(Message message) throws Exception {
        //
        userId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Hozirda " + cinemaStore.getAll().size() + " ta kino bor.");
        keyboardRow.clear();
        sendMessage.setReplyMarkup(buttons());
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);
    }

    @Override
    public void addCinema(Message message) throws Exception {
        //
        userId = message.getChatId();
        BotUserDto by = userStore.findBy(message.getChatId());
        by.setStatus(Status.ADD.toString());
        userStore.add(by.toDomain());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Kinoni yuboring:");
        keyboardRow.clear();
        sendMessage.setReplyMarkup(buttons());
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);
    }

    @Override
    public void statistics(Message message) throws Exception {
        //
        userId = message.getChatId();

        SendMessage sendMessage = new SendMessage();

        String sb = "\n" +
                "Foydalanuvchilar soni: " + userStore.getAll().size() + " ta";
        sendMessage.setText(sb);
        keyboardRow.clear();
        sendMessage.setReplyMarkup(buttons());
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);
    }


    public void restart(Message message) {

    }

    public void others(Message message) throws Exception {
        //
        userId = message.getChatId();
        BotUserDto userBy = userStore.findBy(userId);
        SendMessage sendMessage = new SendMessage();

        if (userBy.getStatus().equals(Status.ADD.toString()) && message.hasVideo()) {
            //
            Cinema cinema = new Cinema();
            String caption = message.getCaption();
            cinema.setCinemaId(message.getVideo().getFileId());
            cinema.setVideoInfo(caption);
            userBy.setHasVideo(true);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String format = dateFormat.format(date);
            cinema.setAddedTime(format);
            cinemaStore.add(cinema);
            userBy.setCinemaId(message.getVideo().getFileId());
            userBy.setStatus(Status.NONE.toString());
            userStore.add(userBy.toDomain());
            sendMessage.setText("Kino kodini jo'nating: ");
        } else
            if (userBy.getStatus().equals(Status.REMOVE.toString())) {
            //
            String result = cinemaStore.removeCinema(message.getText().trim());
            userBy.setStatus(Status.NONE.toString());
            userStore.add(userBy.toDomain());
            sendMessage.setText(result);
        } else if (userBy.isHasVideo() && message.hasText()) {
            String cinemaId = userBy.getCinemaId();
            CinemaDto byCode = cinemaStore.findByCinemaId(cinemaId);
            byCode.setCode(message.getText());
            cinemaStore.add(byCode.toDomain());
            userBy.setHasVideo(false);
            userStore.add(userBy.toDomain());
            sendMessage.setText("Cinema added successfully: ");
        } else {
            sendMessage.setText("Bunday buyruq mavjud emas.");
        }
        keyboardRow.clear();
        sendMessage.setReplyMarkup(buttons());
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);

    }

    private static String extractTextUntilNextLine(String text, String tag) {
        int startIndex = text.indexOf(tag);
        if (startIndex != -1) {
            int valueStartIndex = startIndex + tag.length();
            int valueEndIndex = text.indexOf('\n', valueStartIndex);
            if (valueEndIndex == -1) {
                valueEndIndex = text.length();
            }
            return text.substring(valueStartIndex, valueEndIndex).trim();
        } else {
            return "Not found.";
        }
    }


    private ReplyKeyboardMarkup buttons() {
        //
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();


        row1.add(KeyboardButton.builder().text("Add").build());
        row1.add(KeyboardButton.builder().text("Remove").build());

        row2.add(KeyboardButton.builder().text("Statistics").build());
        row2.add(KeyboardButton.builder().text("Cinema Count").build());

        keyboardRow.add(row1);
        keyboardRow.add(row2);

        markup.setKeyboard(keyboardRow);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
}
