
package org.example.ebankbot.discord;

import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.example.ebankbot.agents.EbankAIAgent;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@DiscordController

public class DiscordBot {

    private final EbankAIAgent ebankAIAgent;

    public DiscordBot(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }

    @DiscordMapping
    public void perform(MessageReceivedEvent event) {
        System.out.println(">>> MESSAGE DISCORD RECU <<<");

        if (event.getAuthor().isBot()) {
            return;
        }

        String query = event.getMessage().getContentRaw();
        System.out.println("Message reçu : " + query);

        try {
            String response = ebankAIAgent.chat(new Prompt(query));

            System.out.println("Réponse générée : " + response);

            if (response == null || response.isBlank()) {
                response = "Désolé, je n'ai pas pu générer de réponse.";
            }

            int maxLength = 1900;

            for (int i = 0; i < response.length(); i += maxLength) {
                String part = response.substring(
                        i,
                        Math.min(i + maxLength, response.length())
                );

                event.getChannel().sendMessage(part).queue(
                        success -> System.out.println("Réponse envoyée sur Discord"),
                        error -> System.err.println(
                                "Erreur lors de l'envoi : " + error.getMessage()
                        )
                );
            }

        } catch (Exception e) {
            System.err.println("Erreur pendant le traitement du message :");
            e.printStackTrace();

            event.getChannel()
                    .sendMessage("Désolé, une erreur est survenue lors du traitement.")
                    .queue();
        }
    }
}
