package tk.valoeghese.brie;

import static tk.valoeghese.brie.BrieImpl.doDepth;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ImplBrieRenderApplication extends Application {
	public ImplBrieRenderApplication() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BrieRenderer renderer = BrieRenderer.boundInstanceForLaunch;

		Pane pane = new Pane();
		pane.getChildren().add(renderer.canvas);
		GraphicsContext gc = renderer.canvas.getGraphicsContext2D();
		renderer.pwr = gc.getPixelWriter();
		renderer.scene = new Scene(pane, renderer.canvas.getWidth(), renderer.canvas.getHeight(), Color.BLACK);
		renderer.stage = primaryStage;
		renderer.stage.setScene(renderer.scene);
		renderer.stage.setTitle(renderer.title);
		renderer.stage.show();
		renderer.notStarted = false;
		renderer.w = (int) renderer.canvas.getWidth();
		renderer.h = (int) renderer.canvas.getHeight();
	}

	static class BrieRenderer {
		private static BrieRenderer boundInstanceForLaunch;

		private BrieRenderFunction[] currentBuffer = new BrieRenderFunction[0];
		private float[] currentDepthBuffer = new float[100 * 100];
		private boolean dirtyCDepthBuffer = false;
		private List<BrieRenderFunction> currentBufferList = new ArrayList<>();

		List<BrieRenderFunction> writeBuffer = new ArrayList<>();
		boolean dirtyDepthbuffer = false;
		float[] writeDepthBuffer = new float[100 * 100];

		private Canvas canvas = new Canvas(100, 100);
		private Scene scene = null;
		private Stage stage = null;
		private String title = "";
		private PixelWriter pwr = null;
		boolean notStarted = true;
		int w, h = 100;

		private void render(PixelWriter pwr, int width, int height) {
			Platform.runLater(() -> {
				float[] depthBuffer = this.writeDepthBuffer;
				for (BrieRenderFunction function : this.currentBuffer) {
					function.render(depthBuffer, pwr, width, height);
				}
			});
		}

		void setWidth(int width) {
			this.w = width;
			this.canvas.setWidth(width);
			this.writeDepthBuffer = new float[w * h];
			this.currentDepthBuffer = new float[w * h];

			if (this.stage != null) {
				this.stage.setWidth(width);
			}
		}

		void setHeight(int height) {
			this.h = height;
			this.canvas.setHeight(height);
			this.writeDepthBuffer = new float[w * h];
			this.currentDepthBuffer = new float[w * h];

			if (this.stage != null) {
				this.stage.setHeight(height);
			}
		}

		void drawTriangle(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, Color colour) {
			this.dirtyDepthbuffer = true;
			this.writeBuffer.add(BrieRenderFunction.triangle(drawX(x0), drawY(y0), drawX(x1), drawY(y1), drawX(x2), drawY(y2), z0, z1, z2, colour));
		}

		// percentage of width, but {-1,1}
		private float drawX(float brie) {
			return 0.5f * this.w * (1.0f + brie);
		}

		// percentage of height, but {-1-1} but also in reverse so bottom = -1, top = 1
		private float drawY(float brie) {
			return 0.5f * this.w * (1.0f - brie);
		}

		void setTitle(String title) {
			this.title = title;

			if (this.stage != null) {
				Platform.runLater(() -> this.stage.setTitle(this.title));
			}
		}

		void swapBuffers() {
			this.currentBuffer = this.writeBuffer.toArray(new BrieRenderFunction[this.writeBuffer.size()]);

			// swap render buffers
			List<BrieRenderFunction> temp = this.writeBuffer;
			this.writeBuffer = this.currentBufferList;
			this.currentBufferList = temp;

			// swap depth buffers
			float[] tempDepth = this.writeDepthBuffer;
			this.writeDepthBuffer = this.currentDepthBuffer;
			this.currentDepthBuffer = tempDepth;

			// swap dirty depth buffer flags
			boolean tempDirty = this.dirtyDepthbuffer;
			this.dirtyDepthbuffer = this.dirtyCDepthBuffer;
			this.dirtyCDepthBuffer = tempDirty;

			// if the stage is showing, render stuff
			if (this.stage.isShowing()) {
				this.render(this.pwr, (int) this.canvas.getWidth(), (int) this.canvas.getHeight());
			}
		}

		void bindRendererForLaunch() {
			boundInstanceForLaunch = this;
		}

		void clearWriteBuffers() {
			this.writeBuffer.clear();

			// check whether to clear the depth buffer
			if (this.dirtyDepthbuffer) {
				this.dirtyDepthbuffer = false;
				this.writeDepthBuffer = new float[w * h];
			}
		}

		boolean pollWindowClosing() {
			boolean result = this.stage.isShowing();

			if (!result) {
				Platform.exit();
			}
			return result;
		}

		void hide() {
			Platform.runLater(() -> this.stage.hide());
		}

		void exit() {
			try {
				Platform.exit();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	static void begin() {
		ImplBrieRenderApplication.
		launch(new String[0]);
	}
}

@FunctionalInterface
interface BrieRenderFunction {
	void render(float[] depthbuffer, PixelWriter pwr, int width, int height);

	static BrieRenderFunction triangle(float x0, float y0, float x1, float y1, float x2, float y2, float z0, float z1, float z2, Color colour) {
		// see if we can use a simple triangle
		if (y0 == y1) {
			if (y2 > y0) { // lower triangle
				return lowerTriangle(x0, x1, (int) y0, x2, (int) y2, z0, z1, z2, colour);
			} else { // upper triangle
				return upperTriangle(x2, (int) y2, x0, x1, (int) y0, z2, z0, z1, colour);
			}
		} else if (y0 == y2) {
			if (y1 > y0) { // lower triangle
				return lowerTriangle(x0, x2, (int) y0, x1, (int) y1, z0, z2, z1, colour);
			} else {
				return upperTriangle(x1, (int) y1, x0, x2, (int) y0, z1, z0, z2, colour);
			}
		} else if (y1 == y2) {
			if (y0 > y1) { // lower triangle
				return lowerTriangle(x1, x2, (int) y1, x0, (int) y0, z1, z2, z0, colour);
			} else {
				return upperTriangle(x0, (int) y0, x1, x2, (int) y1, z0, z1, z2, colour);
			}
		}

		// compound triangle

		// sort y values
		float[] order = null;

		if (y0 > y1) { // 102 120 210
			if (y1 > y2) { // 210
				order = new float[] {x2, y2, x1, y1, x0, y0, z2, z1, z0};
			} else if (y2 > y0) { // 102
				order = new float[] {x1, y1, x0, y0, x2, y2, z1, z0, z2};
			} else { // 120
				order = new float[] {x1, y1, x2, y2, x0, y0, z1, z2, z0};
			}
		} else if (y0 > y2) { // 201
			order = new float[] {x2, y2, x0, y0, x1, y1, z2, z0, z1};
		} else if (y2 > y1) { // 012
			order = new float[] {x0, y0, x1, y1, x2, y2, z0, z1, z2};
		} else { // 021
			order = new float[] {x0, y0, x2, y2, x1, y1, z0, z2, z1};
		}

		// 0	1	2	3	4	5	6	7	8
		// x0	y0	x1	y1	x2	y2	z0	z1	z2

		float midprog = (order[3] - order[1]) / (order[5] - order[1]); // progress of the y at mid
		// lerp to get mid x
		float midXraw = order[0] + midprog * (order[4] - order[0]);
		float midZraw = order[6] + midprog * (order[8] - order[6]);

		// order midX < order[2] so that it renders properly in the draw calls
		if (midXraw > order[2]) {
			float temp = order[2];
			order[2] = midXraw;
			midXraw = temp;
			
			float tempZ = order[7];
			order[7] = midZraw;
			midZraw = tempZ;
		}

		final float[] coords = order; // "local variable must be final or effectively final"
		final float midX = midXraw;
		final float midZ = midZraw;

		return (dbf, pwr, w, h) -> {
			// draw triangles
			upperTriangle(dbf, pwr, w, h, coords[0], (int) coords[1], midX, coords[2], (int) coords[3], coords[6], midZ, coords[7], colour);
			lowerTriangle(dbf, pwr, w, h, midX, coords[2], (int) coords[3], coords[4], (int) coords[5], midZ, coords[7], coords[8], colour);
		};
	}

	static BrieRenderFunction upperTriangle(float lowestx, int lowesty, float highestx0, float highestx1, int highesty, float lowz, float highz0, float highz1, Color colour) {
		// 1 > 0
		if (highestx0 > highestx1) {
			return (dbf, pwr, w, h) -> upperTriangle(dbf, pwr, w, h, lowestx, lowesty, highestx1, highestx0, highesty, lowz, highz1, highz0, colour);
		} else {
			return (dbf, pwr, w, h) -> upperTriangle(dbf, pwr, w, h, lowestx, lowesty, highestx0, highestx1, highesty, lowz, highz0, highz1, colour);
		}
	}

	static BrieRenderFunction lowerTriangle(float lowestx0, float lowestx1, int lowesty, float highestx, int highesty, float lowz0, float lowz1, float highz, Color colour) {
		if (lowestx0 > lowestx1) {
			return (dbf, pwr, w, h) -> lowerTriangle(dbf, pwr, w, h, lowestx1, lowestx0, lowesty, highestx, highesty, lowz1, lowz0, highz, colour);
		} else {
			return (dbf, pwr, w, h) -> lowerTriangle(dbf, pwr, w, h, lowestx0, lowestx1, lowesty, highestx, highesty, lowz0, lowz1, highz, colour);
		}
	}

	// triangle with flat bottom
	static void upperTriangle(float[] depthbuffer, PixelWriter pwr, int w, int h, float lowestx, int lowesty, float highestx0, float highestx1, int highesty, float lowz, float highz0, float highz1, Color colour) {
		float prog = 1.0f / (float) (highesty - lowesty);
		float lerpPart0 = prog * (highestx0 - lowestx);
		float lerpPart1 = prog * (highestx1 - lowestx);
		float lerp0 = lowestx; // current lerp for line low
		float lerp1 = lowestx; // current lerp for line high

		float zLerpPart0 = prog * (highz0 - lowz);
		float zLerpPart1 = prog * (highz1 - lowz);
		float zlerp0 = lowz;
		float zlerp1 = lowz;

		// lerp along x while ascending y to draw the triangle
		for (int y = lowesty; y <= highesty; ++y) {
			if (y < 0) continue;
			if (y >= h) break;

			int xlow = (int) lerp0;
			int xhigh = (int) lerp1;

			// lerp z
			float zlerpPart = (zlerp1 - zlerp0) / (float) (xhigh - xlow);
			float zlerp = zlerp0;

			for (int x = xlow; x <= xhigh; ++x) {
				if (x < 0) continue;
				if (x >= w) break;

				if (zlerp >= -1.0f && zlerp <= 1.0f) {
					if (doDepth()) {
						int index = x * h + y;
						
						if (index >= depthbuffer.length) {
							System.exit(-1);
						}

						float depth = zlerp + 1.0f;
						// positive z points towards the camera
						if (depth >= depthbuffer[index]) {
							depthbuffer[index] = depth;
							pwr.setColor(x, y, colour);
						}
					} else {
						pwr.setColor(x, y, colour);
					}
				}

				zlerp += zlerpPart;
			}

			lerp0 += lerpPart0;
			lerp1 += lerpPart1;
			zlerp0 += zLerpPart0;
			zlerp1 += zLerpPart1;
		}
	}

	// triangle with flat top
	static void lowerTriangle(float[] depthbuffer, PixelWriter pwr, int w, int h, float lowestx0, float lowestx1, int lowesty, float highestx, int highesty, float lowz0, float lowz1, float highz, Color colour) {
		float prog = 1.0f / (float) (highesty - lowesty);
		float lerpPart0 = prog * (highestx - lowestx0);
		float lerpPart1 = prog * (highestx - lowestx1);
		float lerp0 = lowestx0; // current lerp for line low
		float lerp1 = lowestx1; // current lerp for line high

		float zLerpPart0 = prog * (highz - lowz0);
		float zLerpPart1 = prog * (highz - lowz1);
		float zlerp0 = lowz0;
		float zlerp1 = lowz1;

		// lerp along x while ascending y to draw the triangle
		for (int y = lowesty; y <= highesty; ++y) {
			if (y < 0) continue;
			if (y >= h) break;

			int xlow = (int) lerp0;
			int xhigh = (int) lerp1;

			// lerp z
			float zlerpPart = (zlerp1 - zlerp0) / (float) (xhigh - xlow);
			float zlerp = zlerp0;

			for (int x = xlow; x <= xhigh; ++x) {
				if (x < 0) continue;
				if (x >= w) break;

				if (zlerp >= -1.0f && zlerp <= 1.0f) {
					if (doDepth()) {
						int index = x * h + y;

						float depth = zlerp + 1.0f;
						// positive z points towards the camera
						if (depth >= depthbuffer[index]) {
							depthbuffer[index] = depth;
							pwr.setColor(x, y, colour);
						}
					} else {
						pwr.setColor(x, y, colour);
					}
				}

				zlerp += zlerpPart;
			}

			lerp0 += lerpPart0;
			lerp1 += lerpPart1;
			zlerp0 += zLerpPart0;
			zlerp1 += zLerpPart1;
		}
	}
}