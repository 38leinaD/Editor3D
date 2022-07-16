package de.fruitfly.editor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.fruitfly.editor.world.WorldDef;

public class Editor extends Application {

	@Override
	public void start(Stage stage) {
		
		Parent root = null;
		final EditorController controller;
		FXMLLoader fxmlLoader = null;
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("/editor.fxml"));
			root = fxmlLoader.load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		controller = fxmlLoader.getController();
		controller.app = this;
        Scene scene = new Scene(root, 800, 600, false);

        stage.setTitle("RetroEd");
        stage.setScene(scene);
        
        stage.setOnCloseRequest(e -> controller.exitApplication());
        //stage.setX(2000);
        stage.show();
     
        BaseModules.Log = new Console((TextArea) scene.lookup("#console"));
        
        WorldLoader loader = new WorldLoader();
		try {
			Path path = Paths.get("./src/main/resources/simple.txt");
			WorldDef w = loader.load(path);
			Context.world = w;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i=0; i<10; i++) {			
			BaseModules.Log.writeln("test1");
		}
		
		Commands cmds = new Commands();
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		BaseModules.Script = engine;
		try {
			
			engine.getContext().getBindings(ScriptContext.ENGINE_SCOPE).put("$", cmds);
			engine.eval("$.println('hello world!')");
		} catch (ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
