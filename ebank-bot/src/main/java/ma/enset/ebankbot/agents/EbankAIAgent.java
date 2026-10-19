package ma.enset.ebankbot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EbankAIAgent {
    private final ChatClient chatClient;

    public EbankAIAgent(
            ChatClient.Builder builder,
            ChatMemory chatMemory,
            ToolCallbackProvider tools) {
        this.chatClient = builder
                .defaultSystem("You are an AI assistant that responds within the given context of bank customers. If not context is given, respond with I do not know.")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(tools)
                .build();
    }

    private final String defaultConversationId = java.util.UUID.randomUUID().toString();

    public String chat(Prompt prompt) {
        return chat(prompt, defaultConversationId);
    }

    public String chat(Prompt prompt, String conversationId) {
        return chatClient.prompt(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
