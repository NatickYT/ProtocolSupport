package protocolsupport.protocol.storage.netcache;

import protocolsupport.protocol.typeremapper.window.WindowRemapper;
import protocolsupport.protocol.types.WindowType;
import protocolsupport.utils.Utils;

public class WindowCache {

	protected static final int WINDOW_ID_PLAYER = 0;

	protected WindowType windowType = WindowType.PLAYER;
	protected int windowId = WINDOW_ID_PLAYER;

	protected WindowRemapper playerWindowRemapper;
	protected WindowRemapper windowRemapper;

	public void setPlayerWindow(WindowRemapper playerWindowRemaper) {
		this.playerWindowRemapper = playerWindowRemaper;
		this.windowRemapper = playerWindowRemaper;
	}

	public void setOpenedWindow(int windowId, WindowType windowType, WindowRemapper windowRemapper) {
		this.windowId = windowId;
		this.windowType = windowType;
		this.windowRemapper = windowRemapper;
	}

	public WindowType getOpenedWindowType() {
		return windowType;
	}

	public boolean isValidWindowId(int windowId) {
		return windowId == this.windowId;
	}

	public WindowRemapper getPlayerWindowRemapper() {
		return playerWindowRemapper;
	}

	public WindowRemapper getOpenedWindowRemapper() {
		return windowRemapper;
	}

	public void closeWindow() {
		this.windowId = WINDOW_ID_PLAYER;
		this.windowType = WindowType.PLAYER;
		this.windowRemapper = playerWindowRemapper;
	}

	@Override
	public String toString() {
		return Utils.toStringAllFields(this);
	}

}
