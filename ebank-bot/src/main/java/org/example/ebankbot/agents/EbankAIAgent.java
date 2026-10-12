
package org.example.ebankbot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import reactor.core.publisher.Flux;

@Component
public class EbankAIAgent {

    private final ChatClient chatClient;

    public EbankAIAgent(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            ToolCallbackProvider tools) {

        this.chatClient = chatClientBuilder
                .defaultSystem(
                        "Tu es un assistant bancaire qui répond aux questions " +
                                "concernant les clients et les comptes bancaires. " +
                                "Utilise les outils disponibles pour rechercher les informations. " +
                                "Si aucune information pertinente n'est disponible, réponds : JE NE SAIS PAS. " +
                                "Ne divulgue jamais les informations bancaires à un utilisateur non autorisé."
                )
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultTools(tools)
                .build();
    }

    public String chat(Prompt prompt, String conversationId) {
        return chatClient.prompt(prompt)
                .advisors(advisorSpec ->
                        advisorSpec.param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        )
                )
                .call()
                .content();
    }

    public Flux<String> chatStram(Prompt prompt) {
        return chatClient.prompt(prompt)
                .advisors(advisorSpec ->
                        advisorSpec.param(
                                ChatMemory.CONVERSATION_ID,
                                "conversation-default"
                        )
                )
                .stream()
                .content();
    }

    public String chat(Prompt prompt) {
        return chat(prompt, "conversation-default");
    }
}
