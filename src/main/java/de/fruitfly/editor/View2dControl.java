package de.fruitfly.editor;

import java.util.LinkedList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.sun.javafx.geom.Vec2d;

import de.fruitfly.editor.view.ResizableCanvas;
import de.fruitfly.editor.world.Sector;
import de.fruitfly.editor.world.Vertex;

public class View2dControl extends ResizableCanvas {
	private static Font sanSerifFont = Font.font("SanSerif", FontWeight.NORMAL, 10);

	private Vec2d dragStart, originStart;
	private Vec2d origin_pixelSpace = new Vec2d(20, 20);
	private Vec2d cursor = new Vec2d(0, 0);
	private int scale = 1;
	private int gridSize = 1;
	private static final int DEFAULT_UNIT_IN_PIXELS = 20;
	
	private List<Vec2d> vertexLoop = null;
	
	public View2dControl() {
		super();

		this.setFocusTraversable(true);
		
		this.addEventFilter(MouseEvent.ANY, (e) -> this.requestFocus());
		
		this.setOnMousePressed(e -> onMousePressed(e));
		this.setOnMouseDragged(e -> onMouseDragged(e));
		this.setOnMouseMoved(e -> onMouseMoved(e));

		this.setOnKeyPressed(e -> onKeyPressed(e));
		
		//this.widthProperty().addListener(o -> draw());
		//this.heightProperty().addListener(o -> draw());
		
		AnimationTimer animationTimer = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				draw();
			}
		};
		animationTimer.start();
	}

	private Object onKeyPressed(KeyEvent e) {
		if (e.getCode() == KeyCode.SPACE) {
			if (vertexLoop == null) {
				vertexLoop = new LinkedList<>();
			}
			vertexLoop.add(new Vec2d(cursor));
		}
		else if (e.getCode() == KeyCode.ESCAPE) {
			vertexLoop = null;
		}
		
		return null;
	}

	private Object onMousePressed(MouseEvent e) {
		dragStart = new Vec2d(e.getX(), e.getY());
		originStart = new Vec2d(origin_pixelSpace);

		//Vector3f dragStart_wc = new Vector3f();
		//editor2World(dragStart_sc, dragStart_wc);

		return null;
	}

	private Object onMouseDragged(MouseEvent e) {
		updateCursorPosition(e);
		
		Vec2d t = new Vec2d(e.getX(), e.getY());
		t.x -= dragStart.x;
		t.y -= dragStart.y;
		
		origin_pixelSpace.x = originStart.x + t.x;
		origin_pixelSpace.y = originStart.y + t.y;
    	//Editor.getInstance().getWindow().repaint();
		
		//draw();
		
		return null;
	}

	private Object onMouseMoved(MouseEvent e) {
		updateCursorPosition(e);

		//draw();
		
		return null;
	}
	
	private void updateCursorPosition(MouseEvent e) {
		cursor.set(e.getX(), e.getY());
		snapToGrid(cursor);		
	}

	public void draw() {
		GraphicsContext gc = this.getGraphicsContext2D();
		
		gc.setFill(Color.WHITE);
		gc.setLineWidth(1.0);
		gc.setFill(Color.BLACK);
		
		gc.setFont(sanSerifFont);

        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
        
        gc.setStroke(Color.GRAY);
        
        int width = (int)this.getWidth();
        int height = (int)this.getHeight();
        
        
        
        int unity_pixelSpace = (int)(DEFAULT_UNIT_IN_PIXELS*scale);
        
        
        // Grid
        for (int x=(int)Math.round(origin_pixelSpace.x) % unity_pixelSpace; x<width; x+=unity_pixelSpace) {
            int tileUnit = unity_pixelSpace/gridSize;

        	int xx = (int) (x - origin_pixelSpace.x);
        	if (xx % (unity_pixelSpace * 10) == 0) {
                gc.setStroke(Color.GREY);
        	}
        	else {
                gc.setStroke(Color.LIGHTGREY);
        	}
        	gc.strokeLine(x + 0.5, 0 + 0.5, x + 0.5, height);
        }
        
        for (float y=(int)Math.round(origin_pixelSpace.y) % unity_pixelSpace; y<height; y+=unity_pixelSpace) {
            int tileUnit = unity_pixelSpace/gridSize;

        	int yy = (int) (y - origin_pixelSpace.y);
        	if (yy % (unity_pixelSpace * 10) == 0) {
                gc.setStroke(Color.GREY);
        	}
        	else {
                gc.setStroke(Color.LIGHTGREY);
        	}
        	gc.strokeLine(0 + 0.5, y + 0.5, width, y + 0.5);

        }

        // Text
        for (int x=(int)Math.round(origin_pixelSpace.x) % unity_pixelSpace; x<width; x+=unity_pixelSpace) {
            int tileUnit = unity_pixelSpace/gridSize;

        	int xx = (int) (x - origin_pixelSpace.x);
        	if (xx % (unity_pixelSpace * 10) == 0) {
        		gc.fillText(xx/tileUnit+ "", x + 2, 10);
        	}
        }
        
        for (float y=(int)Math.round(origin_pixelSpace.y) % unity_pixelSpace; y<height; y+=unity_pixelSpace) {
            int tileUnit = unity_pixelSpace/gridSize;

        	int yy = (int) (y - origin_pixelSpace.y);
        	if (yy % (unity_pixelSpace * 10) == 0) {
        		gc.fillText(-yy/tileUnit+ "", 2, y - 2);
        	}
        }
        
        /*
        g.setColor(Color.black);
        
        
        
        
        g.setStroke(DashedStroke);
        g.drawLine(0, (int)origin.y, size.width, (int)origin.y);
        g.drawLine((int)origin.x, 0, (int)origin.x, size.height);
        g.setStroke(DefaultStroke);
        drawFrameReference(g);
        */
        
        gc.setStroke(Color.RED);
        gc.strokeLine(cursor.x-5.5, cursor.y+0.5, cursor.x+5.5, cursor.y+0.5);
        gc.strokeLine(cursor.x+0.5, cursor.y-5.5, cursor.x+0.5, cursor.y+5.5);

        if (vertexLoop != null) {
        	gc.setStroke(Color.ORANGE);
        	for (int i=1; i<vertexLoop.size(); i++) {
        		Vec2d v0 = vertexLoop.get(i-1);
        		Vec2d v1 = vertexLoop.get(i);
        		gc.strokeLine(v0.x, v0.y, v1.x, v1.y);
        	}
        }
        
        if (Context.world != null) {
        	gc.setStroke(Color.BLUE);
        	gc.setLineWidth(2.0);
        	for (Sector s : Context.world.sectors) {
        		for (int i=1; i<s.vertices.size()+1; i++) {
	        		Vertex v0 = s.vertices.get(i-1);
	        		Vertex v1 = s.vertices.get(i%s.vertices.size());
	        		double x1 =v0.position.x * unity_pixelSpace + origin_pixelSpace.x + 0.5;
	        		double y1 = v0.position.y * unity_pixelSpace + origin_pixelSpace.y + 0.5;
        			double x2 = v1.position.x * unity_pixelSpace + origin_pixelSpace.x + 0.5;
        			double y2 = v1.position.y * unity_pixelSpace + origin_pixelSpace.y + 0.5;
	        		gc.strokeLine(x1, y1, x2, y2);
	        	}
        	}
        	
        	if (Context.world.player != null) {
        		double x = Context.world.player.x * unity_pixelSpace + origin_pixelSpace.x + 0.5;
        		double y = Context.world.player.y * unity_pixelSpace + origin_pixelSpace.y + 0.5;
        		gc.setStroke(Color.RED);
        		gc.setLineWidth(1.0);
        		gc.strokeOval(x-0.25*unity_pixelSpace, y-0.25*unity_pixelSpace, 0.5*unity_pixelSpace, 0.5*unity_pixelSpace);
        	}
        }
	}
	
	public void zoomIn() {
		scale *= 2;
		//draw();
	}
	
	public void zoomOut() {
		if (scale == 1) return;
		scale /= 2;
		//draw();
	}
	
	private void snapToGrid(Vec2d p) {
		p.set(Math.round((p.x - origin_pixelSpace.x) / (gridSize * scale)) * gridSize * scale + origin_pixelSpace.x, Math.round((p.y - origin_pixelSpace.y) / (gridSize * scale)) * gridSize * scale + origin_pixelSpace.y);

	}
}
