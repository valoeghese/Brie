package tk.valoeghese.brietest;

import static tk.valoeghese.brie.Brie10.*;

public final class BrieTest {
	public static void main(String[] args) {
		brieInit();

		int window = brieCreateWindow();

		brieWindowPropertyi(window, BRIE_WINDOW_HEIGHT, 300);
		brieWindowPropertyi(window, BRIE_WINDOW_WIDTH, 500);
		brieWindowTitle(window, "My Application");
		
		brieShowWindow(window);

		float[] vertices1 = {
				0.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				0.5f, 1.0f, 0.0f
		};

		float[] vertices2 = {
				-0.2f, 0.0f, -1.0f,
				0.8f, 0.0f, -1.0f,
				0.3f, 1.0f, -1.0f,
				-0.6f, 0.2f, -1.0f,
				0.4f, 0.0f, -1.0f,
				0.1f, 1.0f, -1.0f
		};

		int object1 = brieCreateRenderObject();
		brieRenderObjectDatafv(object1, BRIE_RENDER_OBJECT_COLOUR, new float[] {1.0f, 1.0f, 0.0f});
		brieRenderObjectDatafv(object1, BRIE_RENDER_OBJECT_VERTICES, vertices1);

		int object2 = brieCreateRenderObject();
		brieRenderObjectDatafv(object2, BRIE_RENDER_OBJECT_COLOUR, new float[] {0.0f, 1.0f, 1.0f});
		brieRenderObjectDatafv(object2, BRIE_RENDER_OBJECT_VERTICES, vertices2);

		brieGlobalPropertyi(BRIE_DEPTH_BUFFER, BRIE_TRUE);
		brieBindWindow(window);

		long time = System.currentTimeMillis();
		int frames = 0;
		while (briePollWindowClose(window)) {
			brieClearBuffers(window);

			brieDrawRenderObjectTriangles(object1);
			brieDrawRenderObjectTriangles(object2);

			brieSwapBuffers(window);

			long next = System.currentTimeMillis();

			if (next - time >= 1000) {
				System.out.println(frames + "fps");
				time = next;
				frames = 0;
			} else {
				++frames;
			}
		}
		
		System.out.println("Done!");
	}
}
