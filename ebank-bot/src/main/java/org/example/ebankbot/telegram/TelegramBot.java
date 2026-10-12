
package org.example.ebankbot.telegram;

import jakarta.annotation.PostConstruct;

import org.example.ebankbot.agents.EbankAIAgent;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.bot.username}")
    private String telegramBotUsername;

    private final EbankAIAgent aiAgent;

    public TelegramBot(EbankAIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    @Override
    public String getBotUsername() {
        return telegramBotUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        var telegramMessage = update.getMessage();

        if (!telegramMessage.hasText() && !telegramMessage.hasPhoto()) {
            return;
        }

        long chatId = telegramMessage.getChatId();

        try {
            sendTypingQuestion(chatId);

            String messageText = telegramMessage.hasText()
                    ? telegramMessage.getText()
                    : telegramMessage.getCaption();

            if (messageText == null || messageText.isBlank()) {
                messageText = "Décris cette image.";
            }

            List<Media> mediaList = new ArrayList<>();

            if (telegramMessage.hasPhoto()) {
                List<PhotoSize> photos = telegramMessage.getPhoto();

                // La dernière photo correspond généralement à la plus grande taille.
                PhotoSize photo = photos.get(photos.size() - 1);

                GetFile getFile = new GetFile();
                getFile.setFileId(photo.getFileId());

                org.telegram.telegrambots.meta.api.objects.File telegramFile =
                        execute(getFile);

                String fileUrl =
                        "https://api.telegram.org/file/bot"
                                + getBotToken()
                                + "/"
                                + telegramFile.getFilePath();

                URL url = new URL(fileUrl);

                Media media = new Media(
                        MimeTypeUtils.IMAGE_JPEG,
                        new UrlResource(url)
                );

                mediaList.add(media);
            }

            UserMessage userMessage;

            if (mediaList.isEmpty()) {
                userMessage = UserMessage.builder()
                        .text(messageText)
                        .build();
            } else {
                userMessage = UserMessage.builder()
                        .text(messageText)
                        .media(mediaList.toArray(new Media[0]))
                        .build();
            }

            Prompt prompt = new Prompt(userMessage);

            // Chaque conversation Telegram possède son propre identifiant.
            String conversationId = "telegram-" + chatId;

            String answer = aiAgent.chat(prompt, conversationId);

            if (answer == null || answer.isBlank()) {
                answer = "Désolé, je n'ai pas pu générer de réponse.";
            }

            sendTextMessage(chatId, answer);

        } catch (Exception e) {
            System.err.println("Erreur lors du traitement Telegram :");
            e.printStackTrace();

            try {
                sendTextMessage(
                        chatId,
                        "Une erreur est survenue lors du traitement de votre message."
                );
            } catch (TelegramApiException sendException) {
                sendException.printStackTrace();
            }
        }
    }

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api =
                    new TelegramBotsApi(DefaultBotSession.class);

            api.registerBot(this);

            System.out.println("Bot Telegram enregistré avec succès !");

        } catch (TelegramApiException e) {
            throw new IllegalStateException(
                    "Impossible d'enregistrer le bot Telegram",
                    e
            );
        }
    }

    private void sendTextMessage(long chatId, String text)
            throws TelegramApiException {

        // Limite Telegram : 4096 caractères par message.
        int maxLength = 4000;

        for (int i = 0; i < text.length(); i += maxLength) {
            String part = text.substring(
                    i,
                    Math.min(i + maxLength, text.length())
            );

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(part);

            execute(message);
        }
    }

    private void sendTypingQuestion(long chatId)
            throws TelegramApiException {

        SendChatAction action = new SendChatAction();
        action.setChatId(String.valueOf(chatId));
        action.setAction(ActionType.TYPING);

        execute(action);
    }
}
