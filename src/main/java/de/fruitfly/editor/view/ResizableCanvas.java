package de.fruitfly.editor.view;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {
	@Override
	public boolean isResizable() {
		return true;
	}
	
	@Override
	public void resize(double w, double h) {
		setWidth(w);
		setHeight(h);
		super.resize(w, h);
	}
}
