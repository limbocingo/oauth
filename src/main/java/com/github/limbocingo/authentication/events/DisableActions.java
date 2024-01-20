package com.github.limbocingo.authentication.events;

import com.github.limbocingo.authentication.cache.Session;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

/**
 * Disables any action possible that the player can do.
 */
public class DisableActions implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Session.QueuePlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        if (Session.QueuePlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (Session.QueuePlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (Session.QueuePlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            if (Session.QueuePlayers.contains(event.getEntity().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMessage(PlayerChatEvent event) {
        if (Session.TFAPlayers.containsKey(event.getPlayer().getUniqueId())) {
            String TFACode = Session.TFAPlayers.get(event.getPlayer().getUniqueId()).toString();
            if (event.getMessage().equals(TFACode)) {
                Session.QueuePlayers.remove(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(ChatColor.GREEN + "Welcome back!");
            }
            else {
                event.getPlayer().kickPlayer(ChatColor.RED + "Bad 2FA code.");
            }
            event.setCancelled(true);
        }

        if (Session.QueuePlayers.contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0];

        if (Session.TFAPlayers.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (Session.QueuePlayers.contains(event.getPlayer().getUniqueId())
                && !command.equals("/register")
                && !command.equals("/login")
                && !command.equals("/recover")) {
            event.setCancelled(true);
        }
    }

}
