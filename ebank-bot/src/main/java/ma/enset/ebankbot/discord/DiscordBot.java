package ma.enset.ebankbot.discord;

import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import ma.enset.ebankbot.agents.EbankAIAgent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.ai.chat.prompt.Prompt;

@DiscordController
public class DiscordBot {
    private final EbankAIAgent agent;

    public DiscordBot(EbankAIAgent agent) {
        this.agent = agent;
    }

    @DiscordMapping
    public void perform(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String query = event.getMessage().getContentRaw();
        String response = agent.chat(new Prompt(query));
        event.getChannel().sendMessage(response).queue();
    }
}