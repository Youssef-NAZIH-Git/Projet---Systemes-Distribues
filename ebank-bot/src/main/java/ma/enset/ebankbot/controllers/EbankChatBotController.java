package ma.enset.ebankbot.controllers;


import ma.enset.ebankbot.agents.EbankAIAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class EbankChatBotController {
    private final EbankAIAgent ebankAgent;

    public EbankChatBotController(EbankAIAgent ebankAgent) {
        this.ebankAgent = ebankAgent;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam(name = "query", defaultValue = "Bonjour") String query) {
        return ebankAgent.chat(query);
    }
}