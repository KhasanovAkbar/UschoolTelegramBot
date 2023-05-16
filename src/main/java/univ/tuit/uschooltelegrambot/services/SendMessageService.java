package univ.tuit.uschooltelegrambot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import univ.tuit.uschooltelegrambot.BotMain;
import univ.tuit.uschooltelegrambot.constants.ButtonState;
import univ.tuit.uschooltelegrambot.constants.UserStateLayer;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.store.BotUserStore;
import univ.tuit.uschooltelegrambot.constants.Status;
import univ.tuit.uschooltelegrambot.messageSender.MessageSender;

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
    private final BotUserStore<BotUser> botUserStore;

    private Long userId;
    private final BotUser user = new BotUser();

    @Value("${channel.id}")
    private String channelId;

    public SendMessageService(BotMain botMain, MessageSender messageSender, BotUserStore<BotUser> botUserStore) {
        this.botMain = botMain;
        this.messageSender = messageSender;
        this.botUserStore = botUserStore;
    }

    ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
    ArrayList<KeyboardRow> keyboardRow = new ArrayList<>();


    @Override
    public void start(Message message, boolean channelCheck) {

        userId = message.getChatId();
        SendMessage sendMessage = new SendMessage();

        keyboardRow.clear();

        user.setUserId(userId);
        String username = message.getFrom().getUserName();
        user.setUsername(username);
        user.setState(ButtonState.START);
        botUserStore.add(user);
        sendMessage.setReplyMarkup(null);

        if (user.getName() == null && channelCheck) {
            sendMessage.setText("Hello " + message.getFrom().getFirstName() +
                    "\nWelcome Uschool learning center official bot");
        }

        if (channelCheck) {

            DeleteMessage deleteMessage = new DeleteMessage(userId.toString(), message.getMessageId());
            try {
                botMain.execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            sendMessage.setReplyMarkup(buttons());


        } else {
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button1.setText("Subscribe");
            button1.setUrl("https://t.me/JavohirsNotes");
            button1.setCallbackData("/Subscribe");

            button2.setText("Check subscription✅");
            button2.setCallbackData("/check");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button1);
            row.add(button2);
            markup.setKeyboard(Collections.singletonList(row));
            keyboardRow.clear();

            sendMessage.setText("Botdan foydalanish uchun kanalga a'zo bo'ling");
            sendMessage.setReplyMarkup(markup);

        }
        sendMessage.setChatId(userId.toString());
        messageSender.sendMessage(sendMessage);

    }

    @Override
    public void register(Message message) {
        userId = message.getChatId();
        BotUser byUserId = botUserStore.findBy(userId);
        if (message.getText().equals(ButtonState.REGISTER)) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String format = dateFormat.format(date);

            byUserId.setRegistrationTime(format);
            sendMsg("Name", userId.toString());

            byUserId.setName(message.getText());
            byUserId.setState(ButtonState.REGISTER);
            byUserId.setUserStateLayer(UserStateLayer.NAME);
            botUserStore.add(byUserId);

        } else if (byUserId.getUserStateLayer().equals(UserStateLayer.NAME)) {

            sendMsg("Surname", userId.toString());
            byUserId.setName(message.getText());
            byUserId.setSurname(message.getText());
            byUserId.setUserStateLayer(UserStateLayer.SURNAME);
            botUserStore.add(byUserId);
        } else if (byUserId.getUserStateLayer().equals(UserStateLayer.SURNAME)) {

            sendMsg("Age", userId.toString());
            byUserId.setSurname(message.getText());
            byUserId.setAge(message.getText());
            byUserId.setUserStateLayer(UserStateLayer.AGE);
            botUserStore.add(byUserId);

        } else if (byUserId.getUserStateLayer().equals(UserStateLayer.AGE)) {

            sendMsg("Phone number", userId.toString());
            byUserId.setAge(message.getText());
            byUserId.setPhoneNumber(message.getText());
            byUserId.setUserStateLayer(UserStateLayer.PHONE_NUMBER);
            botUserStore.add(byUserId);

        } else if (byUserId.getUserStateLayer().equals(UserStateLayer.PHONE_NUMBER)) {

            byUserId.setPhoneNumber(message.getText());

            markup = new ReplyKeyboardMarkup();
            keyboardRow = new ArrayList<>();
            String username;
            username = (byUserId.getUsername() == null) ? "" : "\n<b>username: </b>" + "@" + byUserId.getUsername();
            messageSender.sendMessage(SendMessage.builder().text("<b>FISH: </b>" + byUserId.getName() + " " + byUserId.getSurname() +
                            username +
                            "\n<b>Phone number: </b>" + byUserId.getPhoneNumber() +
                            "\n<b>Age: </b>" + byUserId.getAge())
                    .parseMode("HTML")
                    .chatId(userId.toString())
                    .build());


            SendMessage sm = new SendMessage();
            KeyboardRow row1 = new KeyboardRow();
            row1.add("Yes");
            row1.add(KeyboardButton.builder().text("No").build());
            keyboardRow.add(row1);
            markup.setKeyboard(keyboardRow);
            markup.setOneTimeKeyboard(true);
            markup.setResizeKeyboard(true);

            sm.setText("Is information correct?");
            sm.setChatId(String.valueOf(message.getChatId()));
            sm.setReplyMarkup(markup);
            messageSender.sendMessage(sm);
            byUserId.setStatus(Status.CHECKED.name());
            byUserId.setUserStateLayer(UserStateLayer.NONE);
            botUserStore.add(byUserId);


        } else if (message.getText().equals("Yes") &&
                byUserId.getStatus().equals(Status.CHECKED.toString()) &&
                byUserId.getUserStateLayer().equals(UserStateLayer.NONE)) {
            SendMessage sm = new SendMessage();
            sm.setText("Your data sent to admin" +
                    "\n<b>Thank you for registration</b>");
            sm.setParseMode("HTML");

            sm.setChatId(userId.toString());
            byUserId.setStatus(Status.COMPLETED.name());

            keyboardRow.clear();
            sm.setReplyMarkup(buttons());
            messageSender.sendMessage(sm);
            botUserStore.add(byUserId);
        } else if (message.getText().equals("No") &&
                byUserId.getStatus().equals(Status.CHECKED.toString()) &&
                byUserId.getUserStateLayer().equals(UserStateLayer.NONE)) {
            SendMessage sm = new SendMessage();
            sm.setText("Your data denied " +
                    "\nClick /start. The announcement will start again");
            sm.setChatId(userId.toString());
            byUserId.setState(Status.DENIED.name());
            keyboardRow.clear();
            sm.setReplyMarkup(buttons());
            messageSender.sendMessage(sm);
            botUserStore.add(byUserId);

        } else others(message);
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
        start(message, false);

    }

    @Override
    public void priceList(Message message) {

    }

    @Override
    public void contactUs(Message message) {
        long chat_id = message.getChatId();

        messageSender.sendMessage(SendMessage.builder().text(
                        "<b>Contact numbers - (71)2074777, (97)7180852\n" +
                                "Telegram - @uschoolofficial\n" +
                                "Facebook - uschoolofficial\n" +
                                "Instagram - uschoolofficial</b>")
                .parseMode("HTML")
                .chatId(String.valueOf(chat_id)).build());

    }

    @Override
    public void location(Message message) {

        long chat_id = message.getChatId();

        SendLocation sl = new SendLocation();
        sl.setLatitude(41.322377813539795);
        sl.setLongitude(69.29450675229528);
        sl.setChatId(String.valueOf(chat_id));
        messageSender.sendLocation(sl);

        messageSender.sendMessage(SendMessage.builder()
                .text("Tashkent, Mirzo Ulugbek district, Kary Niyazov st., 39." +
                        "\nInstitute of Irrigation, 11th building, 8th floor.")
                .chatId(String.valueOf(chat_id))
                .build());


    }

    @Override
    public void others(Message message) {
        SendMessage sm = new SendMessage();
        sm.setText("Click /start. The announcement will start");
        sm.setChatId(String.valueOf(message.getChatId()));
        messageSender.sendMessage(sm);
    }

    @Override
    public void listUser(Message message) {

        List<BotUser> all = botUserStore.getAll();
        for (BotUser byUserId : all) {
            if (byUserId.getStatus().equals(Status.COMPLETED.name())) {
                String username = (byUserId.getUsername() == null) ? "" : "\n<b>username: </b>" + "@" + byUserId.getUsername();
                messageSender.sendMessage(SendMessage.builder().text("<b>FISH: </b>" + byUserId.getName() + " " + byUserId.getSurname() +
                                username +
                                "\n<b>Phone number: </b>" + byUserId.getPhoneNumber() +
                                "\n<b>Age: </b>" + byUserId.getAge() +
                                "\n<b>Registered: </b>" + byUserId.getRegistrationTime())
                        .parseMode("HTML")
                        .chatId(String.valueOf(message.getChatId()))
                        .build());
            }
        }
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
        KeyboardRow row3 = new KeyboardRow();

        row1.add("Register");

        row2.add("Price List");
        row3.add("Location");
        row2.add(KeyboardButton.builder().text("Contact us").build());

        row3.add(KeyboardButton.builder().text("About us").build());

        keyboardRow.add(row1);
        keyboardRow.add(row2);
        keyboardRow.add(row3);

        markup.setKeyboard(keyboardRow);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        return markup;
    }
}
