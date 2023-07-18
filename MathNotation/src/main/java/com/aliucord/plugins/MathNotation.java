package com.github.Xolozop;

import android.content.Context;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;

import java.util.Arrays;

// Aliucord Plugin annotation. Must be present on the main class of your plugin
@AliucordPlugin(requiresRestart = false /* Whether your plugin requires a restart after being installed/updated */)
// Plugin class. Must extend Plugin and override start and stop
// Learn more: https://github.com/Aliucord/documentation/blob/main/plugin-dev/1_introduction.md#basic-plugin-structure
public class MathNotation extends Plugin {
    @Override
    public void start(Context context) {
        // A bit more advanced command with arguments
        commands.registerCommand(
                "mathnotation",
                "Math Notation",
                Arrays.asList(
                        Utils.createCommandOption(ApplicationCommandType.STRING, "text", "Which text you want to convert"),
                        Utils.createCommandOption(ApplicationCommandType.BOOLEAN, "help", "Need any help?")
                ),
                ctx -> {
                    // Check if a user argument was passed
                    if (ctx.containsArg("help") && ctx.getBoolOrDefault("help", false)) {
                        return new CommandsAPI.CommandResult("Help");
                    } else {
                        // Returns either the argument value if present, or the defaultValue ("World" in this case)
                        var text = ctx.getString("text");
                        return new CommandsAPI.CommandResult("Hello! ¹");
                    }
                }
        );
    }

    @Override
    public void stop(Context context) {
        // Unregister all commands
        commands.unregisterAll();
    }
}
