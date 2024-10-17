package ml.noahc3.nickafkpartypack.Packets;

import java.util.List;
import java.util.Set;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;

public class WrapperPlayServerPlayerInfo extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.PLAYER_INFO;

    public WrapperPlayServerPlayerInfo() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerPlayerInfo(PacketContainer packet) {
        super(packet, TYPE);
    }

    public Set<PlayerInfoAction> getActions() {
        return handle.getPlayerInfoActions().read(0);
    }

    public void setActions(Set<PlayerInfoAction> value) {
        handle.getPlayerInfoActions().write(0, value);
    }

    public List<PlayerInfoData> getData() {
        return handle.getPlayerInfoDataLists().read(1);
    }

    public void setData(List<PlayerInfoData> value) {
        handle.getPlayerInfoDataLists().write(1, value);
    }

    // 1.19.2 以前
    @Deprecated
    public void setActionOrg(PlayerInfoAction value) {
        handle.getPlayerInfoAction().write(0, value);
    }

    // 1.19.2 以前
    @Deprecated
    public void setDataOrg(List<PlayerInfoData> value) {
        handle.getPlayerInfoDataLists().write(0, value);
    }
}
