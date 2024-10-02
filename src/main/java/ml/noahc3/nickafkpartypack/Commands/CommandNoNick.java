package ml.noahc3.nickafkpartypack.Commands;

import ml.noahc3.nickafkpartypack.Util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNoNick implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Tasks.removePlayerNick(sender, player, player.getName());
            return true;
        }

        return false;
    }
}
