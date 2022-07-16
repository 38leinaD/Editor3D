package de.fruitfly.editor.world;

import com.sun.javafx.geom.Vec2d;

public class Vertex {
	public Vec2d position;
	
	public Vertex(double x, double y) {
		this.position = new Vec2d(x, y);
	}

	@Override
	public String toString() {
		return "Vertex [position=" + position + "]";
	}
}
