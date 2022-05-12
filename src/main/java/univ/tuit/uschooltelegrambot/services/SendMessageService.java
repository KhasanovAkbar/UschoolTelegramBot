package univ.tuit.uschooltelegrambot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import univ.tuit.uschooltelegrambot.cache.Cache;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.domain.CompletedUser;
import univ.tuit.uschooltelegrambot.domain.State;
import univ.tuit.uschooltelegrambot.domain.Status;
import univ.tuit.uschooltelegrambot.messageSender.MessageSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class SendMessageService implements SendMessageImpl<Message> {

    private final MessageSender messageSender;
    private final Cache<BotUser> cache;
    private final Cache<CompletedUser> completedUserCache;

    static BotUser user = new BotUser();

    public static void info(Message message, long user_id) {
        String username = message.getFrom().getUserName();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String format = dateFormat.format(date);

        user.setUserId(user_id);
        user.setUsername(username);
        user.setState(State.NONE.toString());
        user.setRegistrationTime(format);
    }


    public SendMessageService(MessageSender messageSender, Cache<BotUser> cache, Cache<CompletedUser> completedUserCache) {
        this.messageSender = messageSender;
        this.cache = cache;
        this.completedUserCache = completedUserCache;
    }

    ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
    ArrayList<KeyboardRow> keyboardRow = new ArrayList<>();

    @Override
    public void start(Message message) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Hello " + message.getFrom().getFirstName() + "\nWelcome Uschool learning center official bot");
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        keyboardRow.clear();
        sendMessage.setReplyMarkup(buttons());
        messageSender.sendMessage(sendMessage);
    }


    @Override
    public void register(Message message) {
        long chat_id = message.getChatId();

        info(message, chat_id);
        BotUser add = cache.add(user);
        BotUser byUserId = cache.findBy(chat_id, add.getId());

        messageSender.sendMessage(SendMessage
                .builder().text("Name")
                .chatId(String.valueOf(chat_id))
                .build());
        byUserId.setName(message.getText());
        byUserId.setIsName(true);
        cache.update(byUserId);
    }

    @Override
    public void registerUser(Message message, Integer sequence) {

        long chat_id = message.getChatId();
        BotUser byUserId = cache.findBy(chat_id, sequence);
        if (byUserId.getSurname().equals("Register") && byUserId.isName()) {
            messageSender.sendMessage(SendMessage
                    .builder().text("Surname")
                    .chatId(String.valueOf(chat_id))
                    .build());
            byUserId.setName(message.getText());
            byUserId.setSurname(message.getText());
            byUserId.setIsSurname(true);

        } else if (byUserId.getAge().equals("Register") && byUserId.isSurname()) {
            messageSender.sendMessage(SendMessage.builder()
                    .text("Age")
                    .chatId(String.valueOf(chat_id))
                    .build());
            byUserId.setSurname(message.getText());
            byUserId.setAge(message.getText());
            byUserId.setIsAge(true);

        } else if (byUserId.getPhoneNumber().equals("Register") && byUserId.isAge()) {
            messageSender.sendMessage(SendMessage.builder()
                    .text("Phone number")
                    .chatId(String.valueOf(chat_id)
                    ).build());
            byUserId.setAge(message.getText());
            byUserId.setPhoneNumber(message.getText());
        } else if (byUserId.getState().equals("NONE") && byUserId.isAge()) {

            markup = new ReplyKeyboardMarkup();
            keyboardRow = new ArrayList<>();
            byUserId.setPhoneNumber(message.getText());
            messageSender.sendMessage(SendMessage.builder().text("<b>FISH: </b>" + byUserId.getName() + " " + byUserId.getSurname() +
                            "\n<b>username: </b>" + "@" + byUserId.getUsername() +
                            "\n<b>Phone number: </b>" + byUserId.getPhoneNumber() +
                            "\n<b>Age: </b>" + byUserId.getAge())
                    .parseMode("HTML")
                    .chatId(String.valueOf(message.getChatId()))
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
            byUserId.setState(State.CHECKED.toString());

        } else if (message.getText().equals("Yes") && byUserId.getState().equals(State.CHECKED.toString())) {
            SendMessage sm = new SendMessage();
            sm.setText("Your data sent to admin" +
                    "\n<b>Thank you for registration</b>");
            sm.setParseMode("HTML");
            sm.setChatId(String.valueOf(message.getChatId()));
            byUserId.setState(State.COMPLETED.toString());
            byUserId.setIsPhoneNumber(true);
            keyboardRow.clear();
            sm.setReplyMarkup(buttons());
            messageSender.sendMessage(sm);

            completedUserCache.add(copyProperties(byUserId));

        } else if (message.getText().equals("No") && byUserId.getState().equals(State.CHECKED.toString())) {
            SendMessage sm = new SendMessage();
            sm.setText("Your data denied " +
                    "\nClick /start. The announcement will start again");
            sm.setChatId(String.valueOf(message.getChatId()));
            byUserId.setState(State.DENIED.toString());
            byUserId.setIsName(false);
            byUserId.setIsSurname(false);
            byUserId.setIsAge(false);
            byUserId.setIsPhoneNumber(false);
            keyboardRow.clear();
            sm.setReplyMarkup(buttons());
            messageSender.sendMessage(sm);
        } else others(message);
        cache.update(byUserId);


    }


    @Override
    public void aboutUs(Message message) {
        long chat_id = message.getChatId();

        messageSender.sendMessage(SendMessage.builder()
                .text("About")
                .chatId(String.valueOf(chat_id))
                .build());
       /* SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText("O‘t to‘la nigohim boqqanlarimni, \n" +
                "O‘zimga aytolmas yoqqanlarimni, \n" +
                "Yodidan chiqarmas raqamlarimni, \n" +
                "Qalaysiz men sevsam sevmagan qizlar.");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Yangi She'r")
                        .callbackData("next_poem")
                        .build()));
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        messageSender.sendMessage(sendMessage);*/
    }

    @Override
    public void restart(Message message) {
        //delete from db
        start(message);

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

    private CompletedUser copyProperties(BotUser botUser) {
        CompletedUser cu = new CompletedUser();

        cu.setUserId(botUser.getUserId());
        cu.setUsername(botUser.getUsername());
        cu.setAge(botUser.getAge());
        cu.setName(botUser.getName());
        cu.setSurname(botUser.getSurname());
        cu.setPhoneNumber(botUser.getPhoneNumber());
        cu.setRegistrationTime(botUser.getRegistrationTime());
        cu.setStatus(Status.NEW.name());
        return cu;
    }
}
