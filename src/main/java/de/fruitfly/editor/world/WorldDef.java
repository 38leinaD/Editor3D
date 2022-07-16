package de.fruitfly.editor.world;

import java.util.LinkedList;
import java.util.List;

import com.sun.javafx.geom.Vec2d;

public class WorldDef {
	public List<Vertex> vertices = new LinkedList<>();
	public List<Sector> sectors = new LinkedList<>();
	
	public Vec2d player;
}
