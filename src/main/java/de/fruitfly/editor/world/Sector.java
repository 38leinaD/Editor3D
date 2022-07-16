package de.fruitfly.editor.world;

import java.util.LinkedList;
import java.util.List;

import com.sun.javafx.geom.Vec2d;

public class Sector {
	public List<Vertex> vertices = new LinkedList<>();
	public List<Sector> neighbors = new LinkedList<>();
	
	public Sector() {
		
	}
}
