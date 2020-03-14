package tk.valoeghese.brie;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import tk.valoeghese.brie.ImplBrieRenderApplication.BrieRenderer;

class BrieImpl implements Brie10 {
	private static BrieObjectList<BrieRenderer> renderers = new BrieObjectList<>();
	private static BrieObjectList<BrieRenderObject> renderObjects = new BrieObjectList<>();
	private static BrieObjectList<BrieShaderObject> shaderObjects = new BrieObjectList<>();

	private static Color boundColour = Color.WHITE;
	private static BrieRenderer boundWindow = null;
	private static int[] propertyDefaults = {0};
	private static int[] globalProperties;

	static boolean init() {
		try {
			renderers.init();
			renderObjects.init();
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	static int createWindow() {
		return renderers.newObject(new BrieRenderer());
	}

	static int createRenderObject() {
		return renderObjects.newObject(new BrieRenderObject());
	}

	static int createShaderObject() {
		return shaderObjects.newObject(new BrieShaderObject());
	}

	static void compileShader(int soId, int shaderType, String shaderSource) {
		BrieShaderObject shaderObject = shaderObjects.get(soId);

		switch (shaderType) {
		case BRIE_VERTEX_SHADER:
			break;
		case BRIE_COLOUR_SHADER:
			break;
		}
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

	static void setWindowTitle(int window, String title) {
		renderers.get(window).setTitle(title);
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

	static boolean isWindowHidden(int window) {
		return !renderers.get(window).isShowing();
	}

	static void closeWindow(int window) {
		renderers.get(window).exit();
	}

	static void hideWindow(int window) {
		renderers.get(window).hide();
	}

	static void bindWindow(int window) {
		boundWindow = renderers.get(window);
	}

	static void drawTriangles(float[] vertices) {
		int triangles = vertices.length / 9;

		int offset = 0;
		while (triangles --> 0) {
			boundWindow.drawTriangle(vertices[offset + 0], vertices[offset + 1], vertices[offset + 2], vertices[offset + 3], vertices[offset + 4], vertices[offset + 5], vertices[offset + 6], vertices[offset + 7], vertices[offset + 8], boundColour);
			offset += 9;
		}
	}

	static void drawRenderObjectTriangles(int roId) {
		BrieRenderObject renderObject = renderObjects.get(roId);

		if (renderObject.colour != null) {
			boundColour = renderObject.colour;
		}

		drawTriangles(renderObject.vertices);
	}

	static void setColour(float r, float g, float b) {
		boundColour = Color.rgb((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}

	static void setPropertyInt(int property, int value) {
		globalProperties[property] = value;
	}

	static boolean doDepth() {
		return globalProperties[BRIE_DEPTH_BUFFER] == 1;
	}

	static void renderObjectDataFV(int renderObject, int dataType, float[] data) {
		BrieRenderObject object = renderObjects.get(renderObject);

		switch (dataType) {
		case BRIE_RENDER_OBJECT_COLOUR:
			object.colour = data == BRIE_NO_DATA_FV ? null : Color.rgb((int) (data[0] * 255), (int) (data[1] * 255), (int) (data[2] * 255));
			break;
		case BRIE_RENDER_OBJECT_VERTICES:
			object.vertices = copyFV(data);
			break;
		}
	}

	private static float[] copyFV(float[] in) {
		float[] result = new float[in.length];
		System.arraycopy(in, 0, result, 0, in.length);
		return result;
	}

	private static int[] copyIV(int[] in) {
		int[] result = new int[in.length];
		System.arraycopy(in, 0, result, 0, in.length);
		return result;
	}

	static {
		globalProperties = copyIV(propertyDefaults);
	}
}

class BrieObjectList<T> {
	private ArrayList<T> list = null;

	void init() {
		this.list = new ArrayList<>();
		this.list.add(null); // null first object
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

class BrieRenderObject {
	Color colour = null;
	float[] vertices;
}

class BrieShaderObject {
	BrieCompiledVertex vertexShader;
	BrieCompiledColour colourShader;
}

@FunctionalInterface
interface BrieCompiledVertex {
	float[] position(float[] raw);
}

@FunctionalInterface
interface BrieCompiledColour {
	float[] colour(float[] raw, int x, int y);
}