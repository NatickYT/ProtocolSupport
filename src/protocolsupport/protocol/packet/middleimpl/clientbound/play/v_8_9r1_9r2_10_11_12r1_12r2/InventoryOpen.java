package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_8_9r1_9r2_10_11_12r1_12r2;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.chat.ChatAPI;
import protocolsupport.api.chat.components.TextComponent;
import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.PacketType;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleInventoryOpen;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.packet.middleimpl.IPacketData;
import protocolsupport.protocol.serializer.StringSerializer;
import protocolsupport.protocol.typeremapper.legacy.LegacyWindowType;
import protocolsupport.protocol.typeremapper.legacy.LegacyWindowType.LegacyWindowData;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public class InventoryOpen extends MiddleInventoryOpen {

	public InventoryOpen(ConnectionImpl connection) {
		super(connection);
	}

	@Override
	public RecyclableCollection<? extends IPacketData> toData0() {
		LegacyWindowData wdata = LegacyWindowType.getData(windowRemapper.toClientWindowType(type));
		return RecyclableSingletonList.create(writeData(
			ClientBoundPacketData.create(PacketType.CLIENTBOUND_PLAY_WINDOW_OPEN),
			version, windowId, wdata.getStringId(), title.toLegacyText(cache.getAttributesCache().getLocale()), windowRemapper.toClientSlots(0)
		));
	}

	public static ClientBoundPacketData writeData(ClientBoundPacketData to, ProtocolVersion version, int windowId, String type, String title, int slots) {
		to.writeByte(windowId);
		StringSerializer.writeString(to, version, type);
		StringSerializer.writeString(to, version, ChatAPI.toJSON(new TextComponent(title)));
		to.writeByte(slots);
		return to;
	}

}
