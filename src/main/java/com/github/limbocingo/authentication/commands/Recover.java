package com.github.limbocingo.authentication.commands;

import com.github.limbocingo.authentication.Plugin;
import com.github.limbocingo.authentication.api.Authentication;
import com.github.limbocingo.authentication.api.User;
import com.github.limbocingo.authentication.api.UserManager;
import com.github.limbocingo.authentication.cache.Session;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Random;

/**
 * Command that is used by the user to reset his password.
 */
public class Recover implements CommandExecutor {
    private final Authentication authAPI;
    private final Plugin main;
    private final JDA bot;

    /**
     * Command constructor.
     *
     * @param main The main plugin class.
     */
    public Recover(Plugin main) {
        this.authAPI = main.authAPI;
        this.bot = main.bot;
        this.main = main;
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
        Bukkit.getScheduler().runTaskAsynchronously(this.main, new Runnable() {
            @Override
            public void run() {
                try {
                    Player player = (Player) sender;
                    UserManager userManager = authAPI.getUser(sender.getName());
                    User user = userManager.information();

                    if (user == null) sender.sendMessage(ChatColor.RED + "You're not registered in the database.");

                    else if (user.getDiscord() == null)
                        sender.sendMessage(ChatColor.RED + "You need a discord account linked.");

                    else if (bot.getUserById(args[0]) == null)
                        sender.sendMessage(ChatColor.RED + "The discord account doesn't exists.");

                    else if (Session.TFAPlayers.containsKey(player.getUniqueId()))
                        player.sendMessage(ChatColor.RED + "You already have a recovery pending.");

                    else {
                        Session.TFAPlayers.put(player.getUniqueId(), new Random().nextInt(100000));

                        sender.sendMessage(ChatColor.BLUE + "A message to your MD has been sent.");
                        bot.getUserById(user.getDiscord()).openPrivateChannel().queue(privateChannel -> {
                            privateChannel.sendMessage(
                                    "Your code for restoring the password is here."
                                            + "\n**Code**: `" + Session.TFAPlayers.get(player.getUniqueId()) + "`."
                                            + "\n\n`If you thing this is an error contact us.`").queue();
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
