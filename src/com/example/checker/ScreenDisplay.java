package com.example.checker;

public class ScreenDisplay {
	int width;
	int height;

	public void setData(int x, int y) {
		width = x;
		height = y;
	}

	public String getData() {
		return ("" + width + " " + height);
	}
}
