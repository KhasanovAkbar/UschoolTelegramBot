package univ.tuit.uschooltelegrambot.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import univ.tuit.uschooltelegrambot.constants.ButtonState;
import univ.tuit.uschooltelegrambot.services.SendMessageAdminService;
import univ.tuit.uschooltelegrambot.services.SendMessageService;
import univ.tuit.uschooltelegrambot.store.BotUserStore;
import univ.tuit.uschooltelegrambot.store.CinemaStore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class MessageHandler implements Handler<Message> {

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    private final CinemaStore cinemaStore;

    @Autowired
    private SendMessageAdminService sendMessageAdminService;

    public MessageHandler(BotUserStore botUserStore, CinemaStore cinemaStore) {
        this.cinemaStore = cinemaStore;
    }

    @Override
    public void choose(Message message, String status) throws Exception {
        //
        String user_first_name = message.getChat().getFirstName();
        long user_id = message.getChat().getId();
        String message_text = message.getText();

        if (message.hasText()) {
            log(user_first_name, Long.toString(user_id), message_text);
            if (status.equals("member")) {
                switch (message.getText()) {
                    case ButtonState.START:
                        sendMessageService.start(message, status);
                        break;
                    case ButtonState.RESTART:
                        sendMessageService.restart(message);
                        break;
                    case ButtonState.ABOUT_US:
                        sendMessageService.aboutUs(message);
                        break;
                    case ButtonState.CONTACT_US:
                        sendMessageService.contactUs(message);
                        break;
                    default:
                        sendMessageService.others(message);
                }
            } else if (status.equals("administrator") || status.equals("creator")) {

                if (message.hasVideo()) {
                    sendMessageAdminService.others(message);
                }
                switch (message.getText()) {
                    case ButtonState.START:
                        sendMessageAdminService.start(message);
                        break;
                    case ButtonState.ADD_CINEMA:
                        sendMessageAdminService.addCinema(message);
                        break;
                    case ButtonState.STATISTICS:
                        sendMessageAdminService.statistics(message);
                        break;
                    case ButtonState.RESTART:
                        sendMessageAdminService.restart(message);
                        break;
                    case ButtonState.REMOVE_CINEMA:
                        sendMessageAdminService.removeCinema(message);
                        break;
                    case ButtonState.COUNT_OF_CINEMA:
                        sendMessageAdminService.countOfCinema(message);
                        break;
                    default:
                        sendMessageAdminService.others(message);
                }
            } else {
                sendMessageService.notSubscribed(message);
            }
        }
    }

    public static void log(String first_name, String user_id, String txt) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + ", Id = " + user_id + " \n Text - " + txt);
    }

}
