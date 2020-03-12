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

	/**
	 * Swaps the render and write buffers of the said window.
	 * @param window the id of the window for which to swap the buffers.
	 */
	static void brieSwapBuffers(int window) {
		BrieImpl.swapBuffers(window);
		try {
			Thread.sleep(5); // allow it to render. 5 appears to be the lowest value which makes it render almost instantly
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear the write buffers of the window provided.
	 * @param window the id of the brie window object for which to clear the buffers
	 */
	static void brieClearBuffers(int window) {
		BrieImpl.clearBuffer(window);
	}

	/**
	 * Checks if the window is hidden. If it is, close the window. The window may be hidden due to the actions of your or other programs, or due to the user pressing the close button on the window.
	 * @param window the id of the brie window object
	 * @return whether the window is showing
	 */
	static boolean briePollWindowClose(int window) {
		return BrieImpl.pollClose(window);
	}

	static void brieCloseWindow(int window) {
		BrieImpl.closeWindow(window);
	}

	/**
	 * Hides the specified window. If the window is hidden when {@link #briePollWindowClose(int)} is called, it will cause the window to close.
	 * @param window the id of the brie window object to hide
	 */
	static void brieHideWindow(int window) {
		BrieImpl.hideWindow(window);
	}

	static boolean isWindowHidden(int window) {
		return BrieImpl.isWindowHidden(window);
	}

	/**
	 * Draw triangles on the screen.
	 * @param window the id of the brie window object to which to draw
	 * @param vertices an array consisting of 3 x/y/z float[3] vertices for each triangle, each coordinate normalised between [-1.0, 1.0] <br/>
	 * X is defined as going left to right on the screen, <br/>
	 * Y is defined as going bottom to top on the screen, <br/>
	 * Z is defined as going far to near on the screen.
	 */
	static void brieDrawTriangles(int window, float[] vertices) {
		BrieImpl.drawTriangles(window, vertices);
	}

	static void brieBindColour(float red, float green, float blue) {
		BrieImpl.setColour(red, green, blue);
	}
}
