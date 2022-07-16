package de.fruitfly.editor;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f position = new Vector3f(0.0f, 0.0f, 1.0f);
	private double yaw = 0.0;
	private double pitch = 0.0;
	
	public Vector3f getPos() {
		return position;
	}
	
	public double getYaw() {
		return yaw;
	}
	
	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	
	public double getPitch() {
		return pitch;
	}
	
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
}
