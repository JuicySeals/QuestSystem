package dev.blackgate.questsystem.quest.creation.signs;

import org.bukkit.entity.Player;

import java.util.Collections;

public class SignPrompt {
    private final SignHandler signHandler;
    private de.rapha149.signgui.SignGUI signGUI;

    public SignPrompt(SignHandler handler) {
        this.signHandler = handler;
        create();
    }

    private void create() {
        signGUI = de.rapha149.signgui.SignGUI.builder()
                .setHandler((p, result) -> {
                    signHandler.onFinish(p, result);
                    return Collections.emptyList();
                }).build();
    }

    public void open(Player player) {
        signGUI.open(player);
    }
}
