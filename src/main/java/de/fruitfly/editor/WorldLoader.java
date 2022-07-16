package de.fruitfly.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.sun.javafx.geom.Vec2d;

import de.fruitfly.editor.world.Sector;
import de.fruitfly.editor.world.Vertex;
import de.fruitfly.editor.world.WorldDef;

public class WorldLoader {

	private WorldDef world;
	
	public WorldDef load(Path path) throws IOException {

		world = new WorldDef();
				
		Files
			.lines(path)
			.forEachOrdered(s -> parseLine(s));
		
		if (world.player == null) {
			BaseModules.Log.writeln("World has no player-spawn defined.");
		}
		
		return world;
	}
	
	private void parseLine(String s) {
		if (s.startsWith("v")) {
			try (Scanner sc = new Scanner(s)) {
				sc.useDelimiter("[ \\t]");
				sc.next();
				
				Vertex v = new Vertex(sc.nextFloat(), sc.nextFloat());
				world.vertices.add(v);
			}
		}
		else if (s.startsWith("s")) {
			try (Scanner s_ = new Scanner(s)) {
				s_.useDelimiter("[\\t]");
				s_.next();
				
				Sector sector = new Sector();
				String s1 = s_.next();
				System.out.println(s1);
				try (Scanner vertexScanner = new Scanner(s1)) {
					vertexScanner.useDelimiter(" ");
					
					while (vertexScanner.hasNext()) {
						sector.vertices.add(world.vertices.get(vertexScanner.nextInt()));
					}
				}
				
				try (Scanner neighborScanner = new Scanner(s_.next())) {
					neighborScanner.useDelimiter(" ");
				
					while (neighborScanner.hasNext()) {
						neighborScanner.next();
					}
				}
				
				world.sectors.add(sector);
			}
		}
		else if (s.startsWith("p")) {
			try (Scanner sc = new Scanner(s)) {
				sc.useDelimiter("[ \\t]");
				sc.next();
				
				Vec2d p = new Vec2d(sc.nextDouble(), sc.nextDouble());
				world.player = p;
			}
		}
	}

	public static void main(String[] args) {
		WorldLoader loader = new WorldLoader();
		try {
			Path path = Paths.get( "./simple.txt");
			loader.load(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
