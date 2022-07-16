package de.fruitfly.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import de.fruitfly.editor.world.WorldDef;

public class View3dControl implements Runnable {
	/*
	static {
		String osDll = null;
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("windows") >= 0) {
			osDll = "lib/lwjgl/windows";
		}
		else if (os.indexOf("mac") >= 0) {
			osDll = "lib/lwjgl/osx";
		}
		System.setProperty("org.lwjgl.librarypath", new File(osDll).getAbsolutePath());
		System.setProperty("org.lwjgl.util.Debug", "true");
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");


	}
	*/
	
	private boolean closeRequested = false;
	private WorldDef world;
//	private static int WIDTH = 1024;
//	private static int HEIGHT = 768;

	private static int WIDTH = 1920;
	private static int HEIGHT = 1080;

	private AngelCodeFont font;

	private Camera cam;
	
	
	@Override
	public void run() {
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setFullscreen(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		Display.setVSyncEnabled(true);

		/*
		GraphicsDevice[] gDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice gDevice : gDevices) {
			System.out.println(gDevice.getDisplayMode().getWidth() + ", " + gDevice.getDisplayMode().getHeight());
		}
		*/
		
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (DisplayMode mode : modes) {
				System.out.println(mode);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		
		Display.setLocation(1920, 0);
		init();

	    lastFPS = getTime(); //set lastFPS to current Time

		while (!Display.isCloseRequested() && !isCloseRequested()) {
			updateFPS();
			
			update();
			
			setupOpenglBase();
			render3d();
			render2d();

			Display.update();
			Display.sync(60);
		}

		Display.destroy();
	}

	private void setupOpenglBase() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClearDepth(1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);		
	}

	long delta;
	private void update() {
		Vector3f forward = new Vector3f((float)(-Math.sin(cam.getYaw())), (float)(Math.cos(cam.getYaw())), (float)Math.sin(cam.getPitch()));
		forward.normalise();
		
		Vector3f side = Vector3f.cross(forward, new Vector3f(0.0f, 0.0f, 1.0f), null);
		side.normalise();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
//			cam.getPos().x = (cam.getPos().x + 0.1f * Math.cos(cam.getYaw()) * Math.cos(cam.getPitch()));
//			cam.getPos().y = (cam.getPos().y + 0.1f * Math.sin(cam.getYaw()) * Math.cos(cam.getPitch()));
//			cam.getPos().z = (cam.getPos().z + 0.1f * Math.sin(cam.getPitch()));
		
			forward.scale(0.1f);
			Vector3f.add(cam.getPos(), forward, cam.getPos());
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
//			cam.getPos().x = (cam.getPos().x - 0.1f * Math.cos(cam.getYaw()) * Math.cos(cam.getPitch()));
//			cam.getPos().y = (cam.getPos().y - 0.1f * Math.sin(cam.getYaw()) * Math.cos(cam.getPitch()));
//			cam.getPos().z = (cam.getPos().z - 0.1f * Math.sin(cam.getPitch()));
			
			forward.scale(0.1f);
			Vector3f.sub(cam.getPos(), forward, cam.getPos());

		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
//			cam.getPos().x = (cam.getPos().x - 0.1f * Math.sin(cam.getYaw()));
//			cam.getPos().y = (cam.getPos().y + 0.1f * Math.cos(cam.getYaw()));
			side.scale(0.1f);
			Vector3f.sub(cam.getPos(), side, cam.getPos());
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
//			cam.getPos().x = (cam.getPos().x + 0.1f * Math.sin(cam.getYaw()));
//			cam.getPos().y = (cam.getPos().y - 0.1f * Math.cos(cam.getYaw()));
			
			side.scale(0.1f);
			Vector3f.add(cam.getPos(), side, cam.getPos());
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			//System.exit(0);
			Mouse.setGrabbed(false);
		}
		
		if (Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
		}
		
		if (Mouse.isGrabbed()) {
			float dx = Mouse.getDX() / 500.0f;
			float dy = Mouse.getDY() / 500.0f;
			
			cam.setYaw(cam.getYaw() - dx);
			cam.setPitch(cam.getPitch() + dy);
		}
		
		delta = getDelta();
		animTimer +=delta;
	}

	private static FloatBuffer mat4x4_fb = BufferUtils.createFloatBuffer(16);

	public static Matrix4f modelViewMatrix = new Matrix4f();
	public static Matrix4f projectionMatrix = new Matrix4f();
	public static Matrix4f modelViewProjectionMatrix = new Matrix4f();
	
	private void setup3d() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0f, WIDTH/(1f*HEIGHT), 0.1f, 1000.0f);
		
		mat4x4_fb.rewind();
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, mat4x4_fb);
		projectionMatrix.load(mat4x4_fb);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);	
		GL11.glLoadIdentity();
		
		GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);

		//GL11.glRotated(0.0f, 1.0f, 0.0f, 0.0f);
		GL11.glRotated(-cam.getPitch()/Math.PI*180.0, 1.0f, 0.0f, 0.0f);
		GL11.glRotated(-cam.getYaw()/Math.PI*180.0, 0.0f, 0.0f, 1.0f);
		
		GL11.glTranslated(-cam.getPos().x, -cam.getPos().y, -cam.getPos().z);
		
		mat4x4_fb.rewind();
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mat4x4_fb);
		modelViewMatrix.load(mat4x4_fb);
		
		Matrix4f.mul(projectionMatrix, modelViewMatrix, modelViewProjectionMatrix);
	}
	
	long animTimer = 0;
	int animIndex = 1;
	float animTranslation = 0.0f;
	private void render3d() {
		setup3d();
		
		GL11.glDepthFunc(GL11.GL_ALWAYS);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		for (int i=-10; i<=10; i++) {
			GL11.glVertex3f(i, -10.0f, 0.0f);
			GL11.glVertex3f(i, 10.0f, 0.0f);
			
			GL11.glVertex3f(-10.0f, i, 0.0f);
			GL11.glVertex3f(10.0f, i, 0.0f);
		}
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINES);
			// origin
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			GL11.glVertex3f(0.0f, 0.0f, 0.0f);
			GL11.glVertex3f(1.0f, 0.0f, 0.0f);
			
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			GL11.glVertex3f(0.0f, 0.0f, 0.0f);
			GL11.glVertex3f(0.0f, 1.0f, 0.0f);
			
			GL11.glColor3f(0.0f, 0.0f, 1.0f);
			GL11.glVertex3f(0.0f, 0.0f, 0.0f);
			GL11.glVertex3f(0.0f, 0.0f, 1.0f);
			
		GL11.glEnd();

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		/*
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(1.0f, 0.0f, 0.0f);
			GL11.glVertex3f(-1.0f, 3.0f, -1.0f);
			GL11.glVertex3f(1.0f, 3.0f, -1.0f);
			GL11.glVertex3f(1.0f, 3.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 3.0f, 1.0f);
		GL11.glEnd();
		*/
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		while (animTimer >= 70) {
			animIndex++;
			animIndex=(animIndex-1)%10+1;
			animTimer-=70;
		}
		Texture tex = textures.get("src/main/resources/assets/zombie/walk/go_"+animIndex+".png");
		tex.bind();
		
		animTranslation-=0.01f;
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(-1.0f+animTranslation, 3.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(1.0f+animTranslation, 3.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(1.0f+animTranslation, 3.0f, 2.0f);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(-1.0f+animTranslation, 3.0f, 2.0f);
		GL11.glEnd();
	}

	private void setup2d() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);	
		GL11.glLoadIdentity();
	}
	
	private void render2d() {
		setup2d();
		
		Color.white.bind();

		font.drawString(100.5f, 50.5f, "FPS: " + fps, Color.white);
		font.drawString(100.5f, 60.5f, String.format("Pos: (%.2f, %.2f, %.2f)", cam.getPos().x, cam.getPos().y, cam.getPos().z), Color.white);
		font.drawString(100.5f, 70.5f, String.format("Yaw: %.2f, Pitch: %.2f", cam.getYaw()/Math.PI*180.0, cam.getPitch()/Math.PI*180.0), Color.white);

		
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		
		renderStringAtWorldCoords("x", new Vector3f(1.0f, 0.0f, 0.0f));
		renderStringAtWorldCoords("y", new Vector3f(0.0f, 1.0f, 0.0f));
		renderStringAtWorldCoords("z", new Vector3f(0.0f, 0.0f, 1.0f));
	}
	
	private void renderStringAtWorldCoords(String str, Vector3f worldCoords) {
		Vector3f p = worldCoords;
		Vector3f p_screenSpace = new Vector3f();
		toScreenSpace(p, p_screenSpace);
		Vector3f c = cam.getPos();

		if (p_screenSpace.z < 1.0f) {
			float distance = (float) Math.sqrt((p.x-c.x)*(p.x-c.x)+(p.y-c.y)*(p.y-c.y)+(p.z-c.z)*(p.z-c.z));
			font.drawString(p_screenSpace.x, p_screenSpace.y, str);
		}
	}
	
	private Vector4f _p_clipSpace = new Vector4f();
	private Vector3f _p_ndc = new Vector3f();
	private boolean toScreenSpace(Vector3f p_in, Vector3f p_out) {
		Matrix4f.transform(modelViewProjectionMatrix, new Vector4f(p_in.x, p_in.y, p_in.z, 1.0f), _p_clipSpace);
		if (_p_clipSpace.w == 0.0f) {
			return false;
		}
		_p_ndc.set(_p_clipSpace.x, _p_clipSpace.y, _p_clipSpace.z);
		_p_ndc.scale(1.0f/_p_clipSpace.w);
		
		p_out.set(_p_ndc.x, _p_ndc.y);

		p_out.x += 1.0f;
		p_out.y += 1.0f;
		p_out.x/=2.0f;
		p_out.y/=2.0f;
		p_out.x*=WIDTH;
		p_out.y*=HEIGHT;
		p_out.y=HEIGHT-p_out.y;
		p_out.z=_p_ndc.z;
		
		return true;
	}

	Map<String, Texture> textures = new HashMap<>();
	
	private void init() {
		// load font from file
		try {
			Image fontImage = new Image(
					ResourceLoader
							.getResourceAsStream("charmap-oldschool_white.png"),
					"oldschool", false, Image.FILTER_NEAREST);
			font = new AngelCodeFont("src/main/resources/charmap-oldschool_white.fnt",
					fontImage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Path p = new File("src/main/resources/assets").toPath();
		System.out.println(">>>"+p);
		try {
			Files.walkFileTree(p, new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					System.out.println("preVisitDirectory: " + dir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					System.out.println("visitFile: " + file.toFile().toString());
					
					textures.put(file.toFile().toString(), TextureLoader.getTexture("PNG", new FileInputStream(file.toFile())));
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					System.out.println("visitFileFailed: " + file);

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					System.out.println("postVisitDirectory: " + dir);

					return FileVisitResult.CONTINUE;
				}
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cam = new Camera();
	}

	public void setWorldDef(WorldDef world) {
		this.world = world;
	}

	private boolean isCloseRequested() {
		return closeRequested;
	}

	public void setCloseRequested() {
		closeRequested = true;
	}

	public static void main(String[] argv) {
		BaseModules.Log = new ILog() {
			@Override
			public void writeln(String line) {
				System.out.println(line);
			}
		};

		WorldLoader loader = new WorldLoader();
		try {
			Path path = Paths.get("./simple.txt");
			WorldDef w = loader.load(path);
			Context.world = w;
		} catch (IOException e) {
			e.printStackTrace();
		}

		View3dControl quadExample = new View3dControl();
		quadExample.run();
	}
	
	/**
	 * Get the time in milliseconds
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	private long lastFrame;
	
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
	         
	    return delta;
	}
	
	private long lastFPS;
	private long fps;
	private long fpsCnt;

	 
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
	    if (getTime() - lastFPS > 1000) {
	        //Display.setTitle("FPS: " + fps); 
	    	fps = fpsCnt;
	    	fpsCnt = 0; //reset the FPS counter
	        lastFPS += 1000; //add one second
	    }
	    fpsCnt++;
	}
}
