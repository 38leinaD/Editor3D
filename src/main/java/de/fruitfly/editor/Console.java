package de.fruitfly.editor;

import javafx.scene.control.TextArea;

public class Console implements ILog {

	private TextArea textArea;
	public Console(TextArea ta) {
		this.textArea = ta;
	}

	@Override
	public void writeln(String line) {
		textArea.appendText(line + "\n");
	}
}
