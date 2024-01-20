package com.github.limbocingo.authentication.commands;

import com.github.limbocingo.authentication.Plugin;
import com.github.limbocingo.authentication.api.User;
import com.github.limbocingo.authentication.api.UserManager;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Command that enables the 2FA for the player via DISCORD.
 */
public class TwoFactorAuth implements CommandExecutor {
    private final com.github.limbocingo.authentication.api.Authentication API;
    private final Plugin main;
    private final JDA bot;

    /**
     * Command constructor.
     *
     * @param main The main plugin class.
     */
    public TwoFactorAuth(Plugin main) {
        this.API = main.authAPI;
        this.main = main;
        this.bot = main.bot;
    }

    /**
     * Command logic.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return boolean
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Discord parameter not given.");
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.main, new Runnable() {
            @Override
            public void run() {
                try {
                    UserManager userManager = API.getUser(sender.getName());
                    User user = userManager.information();

                    if (user == null) {
                        sender.sendMessage(ChatColor.RED + "You're not registered in the database.");
                    } else if (user.getDiscord() != null)
                        sender.sendMessage(ChatColor.RED + "You already have a account linked with discord.");

                    else if (bot.getUserById(args[0]) == null)
                        sender.sendMessage(ChatColor.RED + "The discord account doesn't exists.");

                    else {
                        userManager.discord(args[0]);

                        sender.sendMessage(ChatColor.BLUE + "A message to your MD has been sent.");

                        bot.getUserById(args[0]).openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessage(
                                    "Thanks for enabling 2FA."
                                            + "\nThe minecraft account is: `" + sender.getName() + "`."
                                            + "\n\n*If you didn't made this, contact us.*").queue();
                        });
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return true;
    }
}
