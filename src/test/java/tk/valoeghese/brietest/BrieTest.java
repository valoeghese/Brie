package tk.valoeghese.brietest;

import static tk.valoeghese.brie.Brie10.*;

public final class BrieTest {
	public static void main(String[] args) {
		brieInit();

		int window = brieCreateWindow();

		brieWindowPropertyi(window, BRIE_WINDOW_HEIGHT, 300);
		brieWindowPropertyi(window, BRIE_WINDOW_WIDTH, 500);
		brieWindowPropertys(window, BRIE_WINDOW_TITLE, "My Application");

		brieShowWindow(window);

		float[] triangleVerts = {
				0.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				0.5f, 1.0f, 0.0f
		};

		float[] triangleVerts2 = {
				-0.2f, 0.0f, -1.0f,
				0.8f, 0.0f, -1.0f,
				0.3f, 1.0f, -1.0f,
				-0.6f, 0.2f, -1.0f,
				0.4f, 0.0f, -1.0f,
				0.1f, 1.0f, -1.0f
		};

		brieGlobalPropertyi(BRIE_DEPTH_BUFFER, BRIE_TRUE);

		long time = System.currentTimeMillis();
		int frames = 0;
		while (briePollWindowClose(window)) {
			brieClearBuffers(window);

			brieBindColour(1.0f, 1.0f, 0.0f);
			brieDrawTriangles(window, triangleVerts);

			brieBindColour(0.0f, 1.0f, 1.0f);
			brieDrawTriangles(window, triangleVerts2);

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
