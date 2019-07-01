package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_4_5_6_7_8_9r1_9r2_10_11_12r1_12r2_13;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.middle.ClientBoundMiddlePacket;
import protocolsupport.protocol.serializer.ArraySerializer;
import protocolsupport.protocol.serializer.ItemStackSerializer;
import protocolsupport.protocol.serializer.PositionSerializer;
import protocolsupport.protocol.serializer.VarNumberSerializer;
import protocolsupport.protocol.storage.netcache.chunk.CachedChunk;
import protocolsupport.protocol.storage.netcache.chunk.CachedChunkSectionBlockStorage;
import protocolsupport.protocol.storage.netcache.chunk.ChunkCache;
import protocolsupport.protocol.typeremapper.tile.TileEntityRemapper;
import protocolsupport.protocol.types.ChunkCoord;
import protocolsupport.protocol.types.Position;
import protocolsupport.protocol.types.TileEntity;
import protocolsupport.protocol.types.chunk.ChunkConstants;
import protocolsupport.protocol.types.chunk.ChunkSectonBlockData;
import protocolsupport.protocol.types.nbt.NBTCompound;
import protocolsupport.utils.Utils;

//TODO: just inherit MiddleChunk and do 2 additional loops
public abstract class AbstractChunkLegacy extends ClientBoundMiddlePacket {

	protected final ChunkCache chunkCache = cache.getChunkCache();
	protected final TileEntityRemapper tileRemapper = TileEntityRemapper.getRemapper(version);

	public AbstractChunkLegacy(ConnectionImpl connection) {
		super(connection);
	}

	protected ChunkCoord coord;
	protected boolean full;
	protected int blockMask;
	protected NBTCompound heightmaps;
	protected final int[] biomeData = new int[256];

	protected CachedChunk cachedChunk;

	@Override
	public void readFromServerData(ByteBuf serverdata) {
		coord = PositionSerializer.readIntChunkCoord(serverdata);
		full = serverdata.readBoolean();
		cachedChunk = chunkCache.get(coord);
		if (cachedChunk == null) {
			cachedChunk = chunkCache.add(coord);
		}
		blockMask = VarNumberSerializer.readVarInt(serverdata);
		heightmaps = ItemStackSerializer.readTag(serverdata);

		{
			ByteBuf chunkdata = ArraySerializer.readVarIntByteArraySlice(serverdata);
			for (int sectionNumber = 0; sectionNumber < ChunkConstants.SECTION_COUNT_BLOCKS; sectionNumber++) {
				if (Utils.isBitSet(blockMask, sectionNumber)) {
					ChunkSectonBlockData sectiondata = ChunkSectonBlockData.readFromStream(chunkdata);

					cachedChunk.setBlocksSection(sectionNumber, new CachedChunkSectionBlockStorage(sectiondata));

					//TODO: move to middleimpls
					Map<Position, TileEntity> directTiles = cachedChunk.getTiles(sectionNumber);
					for (int blockIndex = 0; blockIndex < ChunkConstants.BLOCKS_IN_SECTION; blockIndex++) {
						int blockdata = sectiondata.getBlockData(blockIndex);
						if (tileRemapper.usedToBeTile(blockdata)) {
							Position position = new Position(
								(coord.getX() << 4) + (blockIndex & 0xF),
								(sectionNumber << 4) + ((blockIndex >> 8) & 0xF),
								(coord.getZ() << 4) + ((blockIndex >> 4) & 0xF)
							);
							directTiles.put(position, tileRemapper.getLegacyTileFromBlock(position, blockdata));
						}
					}

				}
			}
			if (full) {
				for (int i = 0; i < biomeData.length; i++) {
					biomeData[i] = chunkdata.readInt();
				}
			}
		}

		int tileCount = VarNumberSerializer.readVarInt(serverdata);
		for (int i = 0; i < tileCount; i++) {
			TileEntity tile = new TileEntity(ItemStackSerializer.readTag(serverdata));
			Position position = tile.getPosition();
			int y = position.getY();
			int sectionNumber = y >> 4;
			if (tileRemapper.tileThatNeedsBlockData(tile.getType())) {
				tile = tileRemapper.remap(tile, cachedChunk.getBlock(sectionNumber, CachedChunk.getBlockIndex(position.getX() & 0xF, y & 0xF, position.getZ() & 0xF)));
			} else {
				tile = tileRemapper.remap(tile);
			}
			cachedChunk.getTiles(sectionNumber).put(tile.getPosition(), tile);
		}
	}

}