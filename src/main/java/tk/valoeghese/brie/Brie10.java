package tk.valoeghese.brie;

public interface Brie10 {
	int
	BRIE_WINDOW_WIDTH = 0,
	BRIE_WINDOW_HEIGHT = 1,
	BRIE_WINDOW_TITLE = 2;

	int
	BRIE_DEFAULT = -1,
	BRIE_FALSE = 0,
	BRIE_TRUE = 1;

	int
	BRIE_DEPTH_BUFFER = 0;

	static boolean brieInit() {
		return BrieImpl.init();
	}

	static int brieCreateWindow() {
		return BrieImpl.createWindow();
	}

	static void brieWindowPropertyi(int window, int property, int value) {
		BrieImpl.setWindowPropertyInt(window, property, value);
	}

	static void brieGlobalPropertyi(int property, int value) {
		BrieImpl.setPropertyInt(property, value);
	}

	static void brieWindowPropertys(int window, int property, String value) {
		BrieImpl.setWindowPropertyStr(window, property, value);
	}

	static void brieShowWindow(int window) {
		BrieImpl.showWindow(window);
	}

	static void brieSwapBuffers(int window) {
		BrieImpl.swapBuffers(window);
		try {
			Thread.sleep(5); // allow it to render. 5 appears to be the lowest value which makes it render almost instantly
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static void brieClearBuffers(int window) {
		BrieImpl.clearBuffer(window);
	}

	/**
	 * Checks if the window is hidden. If it is, close the window.
	 * @param window the id of the brie window object
	 * @return whether the window is showing
	 */
	static boolean briePollWindowClose(int window) {
		return BrieImpl.pollClose(window);
	}

	static void brieCloseWindow(int window) {
		BrieImpl.closeWindow(window);
	}

	static void brieHideWindow(int window) {
		BrieImpl.hideWindow(window);
	}

	static void brieDrawTriangle(int window, float[] vertices) {
		BrieImpl.drawTriangle(window, vertices);
	}

	static void brieBindColour(float red, float green, float blue) {
		BrieImpl.setColour(red, green, blue);
	}
}
