import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        update.getUpdateId();
        String chat_id = String.valueOf(update.getMessage().getChatId());
        SendMessage sendMessage = new SendMessage().setChatId(chat_id);

        // Message Info
        Integer messageId = update.getMessage().getMessageId();

        // User Info
        String userName = update.getMessage().getChat().getUserName();
        String lastName = update.getMessage().getChat().getLastName();
        String firstName = update.getMessage().getChat().getFirstName();
        String description = update.getMessage().getChat().getDescription();
        Long userId = update.getMessage().getChat().getId();

        if (update.getMessage().getText().equals("Привет")) {
            sendMessage.setReplyToMessageId(messageId);
            sendMessage.setText("Привет " + firstName);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            sendMessage.setText("Я тебя не понимаю :( ");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "@chifek_covid_bot";
    }

    public String getBotToken() {
        return "1432048482:AAFFRxpC8e_JFrVat0Cxl4HvYspnprFB41Q";
    }
}