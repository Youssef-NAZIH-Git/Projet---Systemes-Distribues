package ma.enset.ebankbot.controllers;


import ma.enset.ebankbot.agents.EbankAIAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class EbankChatBotController {
    private final EbankAIAgent ebankAgent;

    public EbankChatBotController(EbankAIAgent ebankAgent) {
        this.ebankAgent = ebankAgent;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "query", defaultValue = "Bonjour") String query) {
        return ebankAgent.chat(query);
    }
}