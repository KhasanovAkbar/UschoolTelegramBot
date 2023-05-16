package univ.tuit.uschooltelegrambot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import univ.tuit.uschooltelegrambot.domain.BotUser;
import univ.tuit.uschooltelegrambot.store.BotUserStore;
import univ.tuit.uschooltelegrambot.constants.ButtonState;
import univ.tuit.uschooltelegrambot.services.SendMessageService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Component
public class MessageHandler implements Handler<Message> {

    @Autowired
    private final BotUserStore<BotUser> botUserStore;

    @Autowired
    private SendMessageService sendMessageService;

    public MessageHandler(BotUserStore<BotUser> botUserStore) {
        this.botUserStore = botUserStore;
    }


    @Override
    public void choose(Message message, boolean channelCheck) {
        String user_first_name = message.getChat().getFirstName();
        String user_last_name = message.getChat().getLastName();
        long user_id = message.getChat().getId();
        String message_text = message.getText();

        if (message.hasText()) {
            log(user_first_name, user_last_name, Long.toString(user_id), message_text);
            if (channelCheck) {
                switch (message.getText()) {
                    case ButtonState.START:
                        sendMessageService.start(message, channelCheck);
                        break;

                    case ButtonState.RESTART:
                        sendMessageService.restart(message);
                        break;

                    case ButtonState.REGISTER:
                        sendMessageService.register(message);
                        break;

                    case ButtonState.ABOUT_US:
                        sendMessageService.aboutUs(message);
                        break;

                    case ButtonState.PRICE_LIST:
                        sendMessageService.priceList(message);
                        break;

                    case ButtonState.CONTACT_US:
                        sendMessageService.contactUs(message);
                        break;

                    case ButtonState.LOCATION:
                        sendMessageService.location(message);
                        break;

                    case ButtonState.LIST:
                        sendMessageService.listUser(message);
                        break;

                    default:
                        List<BotUser> all = botUserStore.getAll();
                        BotUser lastUser = new BotUser();
                        for (BotUser botUser : all) {
                            if (botUser.getUserId().equals(user_id)) {
                                lastUser = botUser;
                                break;
                            }
                        }

                        if (lastUser.getState().equals(ButtonState.REGISTER)) {
                            sendMessageService.register(message);
                        } else if (lastUser.getState().equals(ButtonState.START)) {
                            sendMessageService.start(message, channelCheck);
                        } else {
                            sendMessageService.others(message);
                        }
                }

            } else {
                sendMessageService.start(message, channelCheck);

            }
        }
    }

    public static void log(String first_name, String last_name, String user_id, String txt) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + last_name + ". (id = " + user_id + ") \n Text - " + txt);
    }

}
