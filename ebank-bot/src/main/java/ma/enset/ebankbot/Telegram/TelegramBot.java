package ma.enset.ebankbot.Telegram;

import jakarta.annotation.PostConstruct;
import java.util.List;

import ma.enset.ebankbot.agents.EbankAIAgent;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String token;
    private final EbankAIAgent agent;

    public TelegramBot(
            @Value("${telegram.token}") String token,
            EbankAIAgent agent) {
        this.token = token;
        this.agent = agent;
    }

    @PostConstruct
    public void register() throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return "USERNAME_TELEGRAM_DU_BOT";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        var message = update.getMessage();
        String chatId = message.getChatId().toString();

        try {
            String text = message.hasText()
                    ? message.getText()
                    : message.getCaption();

            if (text == null) {
                text = "";
            }

            var userMessageBuilder = UserMessage.builder().text(text);

            if (message.hasPhoto()) {
                List<PhotoSize> photos = message.getPhoto();
                PhotoSize photo = photos.get(photos.size() - 1);

                GetFile getFile = new GetFile();
                getFile.setFileId(photo.getFileId());

                var telegramFile = execute(getFile);
                Media media = new Media(
                        MimeTypeUtils.IMAGE_JPEG,
                        new UrlResource(telegramFile.getFileUrl(token)));
                userMessageBuilder.media(List.of(media));
            }

            UserMessage userMessage = userMessageBuilder.build();
            Prompt prompt = new Prompt(userMessage);

            sendTypingAction(chatId);
            String response = agent.chat(prompt);

            SendMessage reply = new SendMessage();
            reply.setChatId(chatId);
            reply.setText(response);
            execute(reply);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void sendTypingAction(String chatId) throws Exception {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId);
        action.setAction(ActionType.TYPING);
        execute(action);
    }
}