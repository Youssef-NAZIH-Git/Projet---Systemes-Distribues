package ma.enset.ebankbot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EbankAIAgent {
    private final ChatClient chatClient;
    private final String conversationId = UUID.randomUUID().toString(); // resets every time we restart

    public EbankAIAgent(ChatClient.Builder builder, ChatMemory chatMemory, ToolCallbackProvider tools) {
        this.chatClient = builder
                .defaultSystem("You are an AI assistant that responds within the given context of bank customers. If not context is given, respond with I do not know.")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultTools(tools)
                .build();
    }

    public String chat(String query) {
        return chatClient.prompt(query)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
