package ml.noahc3.nickafkpartypack.Commands;

import ml.noahc3.nickafkpartypack.Util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Collection;

public class CommandNickList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (args.length > 0 && args[0].equals("uuids")) {
            sender.sendMessage(String.format("Nicked online players %d/%d:", players.size(), Bukkit.getMaxPlayers()));
            for(Player p : players) {
                final String uuid = p.getUniqueId().toString();
                final String name = p.getName();
                final String nick = Tasks.isPlayerNicked(p) ? Tasks.getPlayerDisplayName(p) : "";
                sender.sendMessage(String.format("%.36s %s %s", uuid, name, nick));
            }
            return true;
        }

        sender.sendMessage("Nicked Players ([real name] > [nickname])");

        for(Player p : players) {
            if (!Tasks.isPlayerNicked(p)) {
                sender.sendMessage("  " + p.getName() + " > [no nickname]");
            } else {
                sender.sendMessage("  " + p.getName() + " > " + Tasks.getPlayerDisplayName(p));
            }
        }

        return true;
    }
}