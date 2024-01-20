package com.github.limbocingo.authentication.commands;

import com.github.limbocingo.authentication.Plugin;
import com.github.limbocingo.authentication.api.Authentication;
import com.github.limbocingo.authentication.api.UserManager;
import com.github.limbocingo.authentication.cache.Session;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * Register command, add players to the database via a command.
 */
public class Register implements CommandExecutor {

    private final Authentication authAPI;
    private final Plugin main;

    /**
     * Command constructor.
     *
     * @param main The main plugin class.
     */
    public Register(Plugin main) {
        this.authAPI = main.authAPI;
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
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You have to give the password.");
            return false;
        }

        if (!Session.QueuePlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You're already in a valid session.");
            return true;
        }

        player.sendMessage(ChatColor.BLUE + "Registering you in...");

        Bukkit.getScheduler().runTaskAsynchronously(this.main, new Runnable() {
            @Override
            public void run() {
                try {
                    UserManager userManager = authAPI.getUser(player.getName());

                    if (userManager.information() != null) {
                        player.sendMessage(ChatColor.RED + "You already have a account.");
                        return;
                    }

                    Session.QueuePlayers.remove(player.getUniqueId());
                    userManager.register(args[0]);
                    
                    player.sendMessage(ChatColor.GREEN + "Welcome newbie! Have a nice day playing.");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return true;
    }
}
