package de.fruitfly.editor;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.script.ScriptException;

import de.fruitfly.editor.world.Vertex;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class EditorController implements Initializable {
	@FXML
	private View2dControl worldView2d;
	@FXML
	private TextField consoleInput;
	@FXML
	private Canvas texturesCanvas;
	
	public static List<Vertex> vertexLoop = new LinkedList<>();
	public Application app;
	private View3dControl worldView3d;

	
	public Object exitApplication() {
		System.out.println(this);
		if (worldView3d != null) {
			System.out.println("close");
			worldView3d.setCloseRequested();
		}
		try {
			app.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@FXML
	public void onMenuViewZoomInPressed() {
		worldView2d.zoomIn();
	}

	@FXML
	public void onMenuViewZoomOutPressed() {
		worldView2d.zoomOut();
	}

	@FXML
	public void onToolbarView3dPressed() {
		System.out.println(this);
		worldView3d = new View3dControl();
		new Thread(worldView3d).start();
	}

	@FXML
	public void onConsoleInputKeyTyped(KeyEvent e) {
	}

	@FXML
	public void onConsoleInputAction() {
		try {
			Object result = BaseModules.Script.eval(consoleInput.getText());
			if (result != null) {
				BaseModules.Log.writeln("= " + result);
			}
		} catch (ScriptException e) {
			BaseModules.Log.writeln("[ERROR] " + e.getMessage());
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		System.out.println(arg0 + ", " + arg1);
		System.out.println(consoleInput);
		//texturesCombo.getItems().add("abc");
	}
}
