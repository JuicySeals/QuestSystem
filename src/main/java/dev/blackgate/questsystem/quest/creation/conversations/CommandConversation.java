package dev.blackgate.questsystem.quest.creation.conversations;

import dev.blackgate.questsystem.QuestSystem;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandConversation {
    private final List<String> commands;
    private final Prompt askForCommandPrompt;
    private final Prompt continueCommandsPrompt;
    private final QuestSystem questSystem;
    private final Player player;

    public CommandConversation(QuestSystem questSystem, Player player) {
        this.questSystem = questSystem;
        this.commands = new ArrayList<>();
        this.askForCommandPrompt = createFirstPrompt();
        this.continueCommandsPrompt = createSecondPropt();
        this.player = player;
    }

    private Prompt createFirstPrompt() {
        return new StringPrompt() {
            @NotNull
            @Override
            public String getPromptText(@NotNull ConversationContext conversationContext) {
                return ChatColor.GREEN + "Type command in chat without the slash. Type quit to stop adding commands.\n%player% will return username";
            }

            @Nullable
            @Override
            public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                commands.add(s);
                continueConversation();
                return END_OF_CONVERSATION;
            }
        };
    }

    private Prompt createSecondPropt() {
        return new StringPrompt() {
            @NotNull
            @Override
            public String getPromptText(@NotNull ConversationContext conversationContext) {
                return ChatColor.GREEN + "Added command. Type quit to stop adding commands";
            }

            @Nullable
            @Override
            public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
                commands.add(s);
                continueConversation();
                return END_OF_CONVERSATION;
            }
        };
    }

    public void start() {
        ConversationFactory factory = new ConversationFactory(questSystem)
                .withEscapeSequence("quit")
                .withFirstPrompt(askForCommandPrompt)
                .withLocalEcho(false)
                .withModality(false);
        Conversation conversation = factory.buildConversation(player);
        conversation.addConversationAbandonedListener(conversationAbandonedEvent -> {
            if (conversationAbandonedEvent.gracefulExit())
                return; // If graceful exit is false then they used exit sequence\
            String message = questSystem.getConfigHelper().getQuestCreationMessage("added-commands");
            message = message.replace("%value%", "0"); // Since this is the first prompt if they quit it will always be 0
            player.sendMessage(message);
            questSystem.getQuestCreationManager().getQuestCreator(player).setCommands(commands);
        });
        conversation.begin();
    }

    public void continueConversation() {
        ConversationFactory factory = new ConversationFactory(questSystem)
                .withEscapeSequence("quit")
                .withFirstPrompt(continueCommandsPrompt)
                .withLocalEcho(false);
        Conversation conversation = factory.buildConversation(player);
        conversation.addConversationAbandonedListener(conversationAbandonedEvent -> {
            if (conversationAbandonedEvent.gracefulExit())
                return; // If graceful exit is false then they used exit sequence
            questSystem.getQuestCreationManager().getQuestCreator(player).setCommands(commands);
            String message = questSystem.getConfigHelper().getQuestCreationMessage("added-commands");
            message = message.replace("%value%", String.valueOf(commands.size()));
            player.sendMessage(message);
        });
        conversation.begin();
    }

    public List<String> getCommands() {
        return commands;
    }
}
