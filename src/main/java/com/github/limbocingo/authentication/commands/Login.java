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
 * Creates a session for the player and lets him play in the server.
 */
public class Login implements CommandExecutor {
    private final Authentication authAPI;
    private final Plugin main;
    private final JDA bot;

    /**
     * Command constructor.
     *
     * @param main The main plugin class.
     */
    public Login(Plugin main) {
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You aren't a player.");
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You have to give the password.");
            return false;
        }

        if (!Session.QueuePlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You're already in a valid session.");
            return true;
        }

        player.sendMessage(ChatColor.BLUE + "Logging you in...");
        Bukkit.getScheduler().runTaskAsynchronously(this.main, new Runnable() {
            @Override
            public void run() {
                try {
                    UserManager userManager = authAPI.getUser(sender.getName());
                    User user = userManager.information();

                    if (user == null) {
                        sender.sendMessage(ChatColor.RED + "You're not registered in the database.");
                        return;
                    }

                    else if (!user.getPassword().equals(args[0])) {
                        sender.sendMessage(ChatColor.RED + "Wrong password, try again...");
                        return;
                    }

                    else {
                        if (user.getDiscord() != null) {
                            player.sendMessage(ChatColor.BLUE + "A message with the code has been sent to your discord DM's.");

                            Session.TFAPlayers.put(player.getUniqueId(), new Random().nextInt(9999));

                            bot.getUserById(user.getDiscord()).openPrivateChannel().queue(privateChannel -> {
                                        privateChannel.sendMessage(
                                                "**Hey!** Here you have you're code: `" + Session.TFAPlayers.get(player.getUniqueId()) + "`"
                                        ).queue();
                                    }
                            );
                            return;
                        }

                    }
                    sender.sendMessage(ChatColor.GREEN + "Welcome to the server!");
                    Session.QueuePlayers.remove(player.getUniqueId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return true;
    }
}
