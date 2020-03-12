package tk.valoeghese.brie;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import tk.valoeghese.brie.ImplBrieRenderApplication.BrieRenderer;

class BrieImpl implements Brie10 {
	private static BrieObjectList<BrieRenderer> renderers = new BrieObjectList<>();
	private static Color colour = Color.WHITE;
	private static int[] propertyDefaults = {0};
	private static int[] globalProperties;

	static boolean init() {
		renderers.init();
		return true;
	}

	static int createWindow() {
		return renderers.newObject(new BrieRenderer());
	}

	static void setWindowPropertyInt(int window, int windowProperty, int value) {
		switch (windowProperty) {
		case BRIE_WINDOW_WIDTH:
			renderers.get(window).setWidth(value);
			break;
		case BRIE_WINDOW_HEIGHT:
			renderers.get(window).setHeight(value);
			break;
		}
	}

	static void setWindowPropertyStr(int window, int windowProperty, String value) {
		switch (windowProperty) {
		case BRIE_WINDOW_TITLE:
			renderers.get(window).setTitle(value);
			break;
		}
	}

	static void showWindow(int window) {
		BrieRenderer object = renderers.get(window);
		object.bindRendererForLaunch();
		Thread t = new Thread(() -> ImplBrieRenderApplication.begin());
		t.setDaemon(true);
		t.start();

		while (object.notStarted) {
			System.out.print("");
		}
	}

	static void swapBuffers(int window) {
		renderers.get(window).swapBuffers();
	}

	static void clearBuffer(int window) {
		renderers.get(window).clearWriteBuffers();
	}

	static boolean pollClose(int window) {
		return renderers.get(window).pollWindowClosing();
	}

	static void closeWindow(int window) {
		renderers.get(window).exit();
	}

	static void hideWindow(int window) {
		renderers.get(window).hide();
	}

	static void drawTriangle(int window, float[] vertices) {
		renderers.get(window).drawTriangle(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5], vertices[6], vertices[7], vertices[8], colour);
	}

	static void setColour(float r, float g, float b) {
		colour = Color.rgb((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}

	static void setPropertyInt(int property, int value) {
		globalProperties[property] = value;
	}

	static boolean doDepth() {
		return globalProperties[BRIE_DEPTH_BUFFER] == 1;
	}

	static {
		globalProperties = new int[propertyDefaults.length];
		System.arraycopy(propertyDefaults, 0, globalProperties, 0, propertyDefaults.length);
	}
}

class BrieObjectList<T> {
	private ArrayList<T> list = null;

	void init() {
		this.list = new ArrayList<>();
	}

	private ArrayList<T> getList() throws BrieNotInitializedException {
		if (this.list == null) {
			throw new BrieNotInitializedException();
		} else {
			return this.list;
		}
	}

	T get(int index) throws BrieNotInitializedException, ArrayIndexOutOfBoundsException {
		return getList().get(index);
	}

	int newObject(T t) throws BrieNotInitializedException {
		int result = this.getList().size();
		this.getList().add(t);
		return result;
	}
}