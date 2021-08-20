import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

public class Bot<update> extends TelegramLongPollingBot {
    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/java";
    static final String USER = "macbook";
    static final String PASS = "";

    public void onUpdateReceived(@NotNull Update update) {
        update.getUpdateId();
        Date date = new Date();
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

        String messageFromUser = update.getMessage().getText().toLowerCase(Locale.ROOT);
        sendMessage.setReplyToMessageId(messageId);
        setLogs(messageFromUser, userName, userId, "history");
        System.out.println(userName + " at " + date + ": " + messageFromUser);
        switch (messageFromUser) {
            case "hi", "hey", "привет", "салам", "хай" -> sendMessage.setText("Куку, " + firstName + " \uD83D\uDC4B");
            case "/help" -> {
                try {
                    help(update);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            default -> sendMessage.setText("Я тебя не понимаю  \uD83E\uDD26\u200D♂️");
        }

        if (sendMessage.getText() != null) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("BOT_NAME");
    }

    public String getBotToken() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("BOT_SECRET");
    }

    public void setLogs(String text, String userName, Long userId, String type) {
        String filePath;
        String fullMessage;
        if (type.equals("history")) {
            filePath = "./logs/" + userId + ".txt";
            Date date = new Date();
            fullMessage = " - " + userName + " at " + date + ": " + text + "\n";
        } else {
            filePath = "./logs/error.txt";
            Date date = new Date();
            fullMessage = " - Exception " + " at " + date + ": " + text + "\n";
        }

        File newFile = new File(filePath);
        try {
            boolean created = newFile.createNewFile();
            if (created)
                System.out.println("File has been created");
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }

        try {
            Files.write(Paths.get(filePath), fullMessage.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("The file has been written");
    }

    public void help(Update update) throws IOException {
        checkConnectionDB();
        update.getUpdateId();
        String chat_id = String.valueOf(update.getMessage().getChatId());
        SendMessage sendMessage = new SendMessage().setChatId(chat_id);

        // Message Info
        Integer messageId = update.getMessage().getMessageId();
        sendMessage.setReplyToMessageId(messageId);
        String text = """
                Что умеет бот:\s
                  - Здороваться
                  - Здороваться
                  - Здороваться
                  - Здороваться
                  - Здороваться
                  """;
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void checkConnectionDB() {
        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }
}