package dev.blackgate.questsystem.quest.creation.conversations;

import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.quest.creation.QuestCreationManager;
import dev.blackgate.questsystem.quest.creation.QuestCreator;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandConversation {
    private final List<String> commands;
    private final Prompt askForCommandPrompt;
    private final QuestSystem questSystem;
    private final Player player;
    public CommandConversation(QuestSystem questSystem, Player player) {
        this.questSystem = questSystem;
        this.commands = new ArrayList<>();
        this.askForCommandPrompt = createPrompt();
        this.player = player;
    }

    private Prompt createPrompt() {
        return new StringPrompt() {
            @NotNull
            @Override
            public String getPromptText(@NotNull ConversationContext conversationContext) {
                return ChatColor.GREEN + "Type command in chat without the slash. Type quit to stop adding commands";
            }

            @Nullable
            @Override
            public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                commands.add(s);
                start();
                return END_OF_CONVERSATION;
            }
        };
    }

    public void start() {
        ConversationFactory factory = new ConversationFactory(questSystem)
            .withEscapeSequence("quit")
            .withFirstPrompt(askForCommandPrompt)
            .withLocalEcho(false);
        Conversation conversation = factory.buildConversation(player);
        conversation.addConversationAbandonedListener(new ConversationAbandonedListener() {
            @Override
            public void conversationAbandoned(@NotNull ConversationAbandonedEvent conversationAbandonedEvent) {
                if(getCommands().isEmpty()) {
                    player.sendMessage(questSystem.getConfigHelper().getQuestCreationMessage("quit-quest-creation"));
                }
            }
        });
        conversation.begin();
    }

    public List<String> getCommands() {
        return commands;
    }
}
