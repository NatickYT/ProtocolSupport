package protocolsupport.protocol.packet.middle.base.clientbound.play;

import io.netty.buffer.ByteBuf;
import protocolsupport.protocol.packet.middle.base.clientbound.ClientBoundMiddlePacket;

public abstract class MiddleVehicleMove extends ClientBoundMiddlePacket {

	protected MiddleVehicleMove(IMiddlePacketInit init) {
		super(init);
	}

	protected double x;
	protected double y;
	protected double z;
	protected float yaw;
	protected float pitch;

	@Override
	protected void decode(ByteBuf serverdata) {
		x = serverdata.readDouble();
		y = serverdata.readDouble();
		z = serverdata.readDouble();
		yaw = serverdata.readFloat();
		pitch = serverdata.readFloat();
	}

}