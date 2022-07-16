package de.fruitfly.editor;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TexturesCanvas extends Canvas {
	public TexturesCanvas() {
		
		Platform.runLater(() -> {
			draw();
		});
	}

	private void draw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, 100, 100);
		gc.setStroke(Color.RED);
		gc.strokeLine(0, 0, 400, 300);			
		System.out.println(".");	
		
	}	
}
