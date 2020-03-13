package tk.valoeghese.brie;

public interface Brie10 {
	// ================== Constants and init operation ================== //
	int
	BRIE_WINDOW_WIDTH = 0,
	BRIE_WINDOW_HEIGHT = 1;

	int
	BRIE_DEFAULT = -1,
	BRIE_FALSE = 0,
	BRIE_TRUE = 1;

	int
	BRIE_DEPTH_BUFFER = 0;

	int
	BRIE_RENDER_OBJECT_COLOUR = 0,
	BRIE_RENDER_OBJECT_VERTICES = 1;

	int BRIE_NULL = 0;
	float[] BRIE_NO_DATA_FV = new float[0];

	int
	BRIE_VERTEX_SHADER = 0,
	BRIE_COLOUR_SHADER = 1;

	static boolean brieInit() {
		return BrieImpl.init();
	}

	// ================ Window operations ================ //

	static int brieCreateWindow() {
		return BrieImpl.createWindow();
	}

	static void brieWindowPropertyi(int window, int property, int value) {
		BrieImpl.setWindowPropertyInt(window, property, value);
	}

	static void brieWindowTitle(int window, String value) {
		BrieImpl.setWindowTitle(window, value);
	}

	/**
	 * Binds a window object for draw operations.
	 * @param window the id of the brie window object to bind for drawing
	 */
	static void brieBindWindow(int window) {
		BrieImpl.bindWindow(window);
	}

	static void brieShowWindow(int window) {
		BrieImpl.showWindow(window);
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

	static boolean isWindowHidden(int window) {
		return BrieImpl.isWindowHidden(window);
	}

	// ================== Global Property operations ================== //

	static void brieGlobalPropertyi(int property, int value) {
		BrieImpl.setPropertyInt(property, value);
	}

	// ================== Draw operations ================== //

	/**
	 * Draw triangles on the currently bound window.
	 * @param vertices an array consisting of 3 x/y/z float[3] vertices for each triangle, each coordinate normalised between [-1.0, 1.0] <br/>
	 * X is defined as going left to right on the screen, <br/>
	 * Y is defined as going bottom to top on the screen, <br/>
	 * Z is defined as going far to near on the screen.
	 */
	static void brieDrawTriangles(float[] vertices) {
		BrieImpl.drawTriangles(vertices);
	}

	/**
	 * Draw triangles as specified by the data on the specified render object.
	 * @param renderObject the id of the brie render object which contains the triangle draw data.
	 */
	static void brieDrawRenderObjectTriangles(int renderObject) {
		BrieImpl.drawRenderObjectTriangles(renderObject);
	}

	// ================== Render Object operations ================== //

	static int brieCreateRenderObject() {
		return BrieImpl.createRenderObject();
	}

	static void brieRenderObjectDatafv(int renderObject, int dataType, float[] data) {
		BrieImpl.renderObjectDataFV(renderObject, dataType, data);
	}

	static void brieSetColour(float red, float green, float blue) {
		BrieImpl.setColour(red, green, blue);
	}
}
