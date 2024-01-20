package com.github.limbocingo.authentication.events;

import com.github.limbocingo.authentication.Plugin;
import com.github.limbocingo.authentication.api.Authentication;
import com.github.limbocingo.authentication.api.UserManager;
import com.github.limbocingo.authentication.cache.Session;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class OnPlayerJoinQuit implements Listener {

    Authentication authAPI;
    Plugin plugin;

    public OnPlayerJoinQuit(Plugin plugin) {
        this.authAPI = plugin.authAPI;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Session.QueuePlayers.add(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + "30" + ChatColor.YELLOW + " seconds to register in the server.");

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
                if (Session.QueuePlayers.contains(event.getPlayer().getUniqueId()))
                    event.getPlayer().kickPlayer(ChatColor.RED + "Too slow! Try joining the server again.");
            }
        }, (20 * 30));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
        UserManager user = this.authAPI.getUser(event.getPlayer().getName());
        Session.QueuePlayers.remove(event.getPlayer().getUniqueId());

        if (user.information() != null) {
            if (user.information().getDiscord() != null)
                Session.TFAPlayers.remove(event.getPlayer().getUniqueId());
        }
    }
}
