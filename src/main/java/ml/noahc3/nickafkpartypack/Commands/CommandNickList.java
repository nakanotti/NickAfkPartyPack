package ml.noahc3.nickafkpartypack.Commands;

import ml.noahc3.nickafkpartypack.Util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNickList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equals("uuids")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                sender.sendMessage(p.getUniqueId().toString() + " " + p.getName() + " " + (Tasks.isPlayerNicked(p) ? Tasks.getPlayerDisplayName(p) : ""));
            }
            return true;
        }

        sender.sendMessage("Nicked Players ([real name] > [nickname])");

        for(Player p : Bukkit.getOnlinePlayers()) {
            if (!Tasks.isPlayerNicked(p)) {
                sender.sendMessage("  " + p.getName() + " > [no nickname]");
            } else {
                sender.sendMessage("  " + p.getName() + " > " + Tasks.getPlayerDisplayName(p));
            }
        }

        return true;
    }
}