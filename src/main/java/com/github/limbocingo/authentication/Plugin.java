package com.github.limbocingo.authentication;

import com.github.limbocingo.authentication.api.Authentication;
import com.github.limbocingo.authentication.commands.Login;
import com.github.limbocingo.authentication.commands.Recover;
import com.github.limbocingo.authentication.commands.Register;
import com.github.limbocingo.authentication.commands.TwoFactorAuth;
import com.github.limbocingo.authentication.events.DisableActions;
import com.github.limbocingo.authentication.events.OnPlayerJoinQuit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

/**
 * Plugin base.
 */
public final class Plugin extends JavaPlugin implements CommandExecutor, Listener {
    public Authentication authAPI;
    public JDA bot;

    public void initBot() throws InterruptedException {
        String botToken = this.getConfig().getString("bot");
        this.bot = JDABuilder.createDefault(botToken).setChunkingFilter(ChunkingFilter.ALL).setMemberCachePolicy(MemberCachePolicy.ALL).enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT).build().awaitReady();
    }

    public void initAPI() throws SQLException {
        ConfigurationSection mysql = this.getConfig().getConfigurationSection("mysql");
        this.authAPI = new Authentication(
                mysql.getString("hostname"),
                mysql.getString("username"),
                mysql.getString("password"),
                mysql.getString("database")
        );
    }

    @Override
    public void onEnable() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) this.saveDefaultConfig();

        try {
            this.initAPI();
            this.initBot();
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*
          Events
         */
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoinQuit(this), this);
        this.getServer().getPluginManager().registerEvents(new DisableActions(), this);

        /*
          Commands
         */
        this.getCommand("login").setExecutor(new Login(this));
        this.getCommand("register").setExecutor(new Register(this));
        this.getCommand("recover").setExecutor(new Recover(this));
        this.getCommand("2fa").setExecutor(new TwoFactorAuth(this));
    }

    @Override
    public void onDisable() {
        try {
            this.authAPI.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
