package net.pistonmaster.pistoninvite;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public final class PistonInvite extends JavaPlugin implements Listener, CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        int max = getConfig().getInt("max-invites");
        int senderInvites = getConfig().getInt("invites." + sender.getName());

        if (senderInvites >= max) {
            sender.sendMessage("You have reached the maximum number of invites.");
            return true;
        }

        List<String> invited = getConfig().getStringList("invited");

        if (invited.contains(args[0])) {
            sender.sendMessage("Person is already invited.");
            return true;
        }

        invited.add(args[0]);

        getConfig().set("invited", invited);
        getConfig().set("invites." + sender.getName(), senderInvites + 1);
        saveConfig();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("invite").setExecutor(this);
        getCommand("invite").setTabCompleter(this);
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent event) {
        if (!isInvited(event.getPlayer())) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You must be invited to join this server.");
        }
    }

    private boolean isInvited(Player player) {
        return player.hasPermission("pistoninvite.bypass")
                || getConfig().getStringList("invited").contains(player.getName());
    }
}
