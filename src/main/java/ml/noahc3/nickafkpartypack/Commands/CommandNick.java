package ml.noahc3.nickafkpartypack.Commands;

import ml.noahc3.nickafkpartypack.Util.Constants;
import ml.noahc3.nickafkpartypack.Util.Tasks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNick implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && sender instanceof Player) {
            Player player = (Player) sender;
            String nick = String.join(" ", args);

            Tasks.setPlayerNick(sender, player, nick);
            Constants.nicknames.setNickname(player.getName(), nick, true);
            return true;
        }

        return false;
    }
}
