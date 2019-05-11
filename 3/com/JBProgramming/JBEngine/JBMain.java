package com.JBProgramming.JBEngine;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

import javax.swing.JFrame;

public abstract class JBMain extends Canvas {

	private static final long serialVersionUID = -3676059178027806607L;

	private double targetFPS = 1D / 60D;

	private Thread mainThread = null;

	private boolean running = true;

	public static final int recordedAmt = 1000;

	private double[] recorded = new double[recordedAmt];

	private long startTime = System.nanoTime(), cycleStart = System.nanoTime(), cycleEnd, cycleTime, tickStart,
			tickEnd, tickTime, renderStart, renderEnd, renderTime;

	private JFrame frame;

	public JBMain(String title, int width, int height, boolean fullscreen, boolean resizable, Image logo) {
		Arrays.fill(recorded, 0);
		frame = new JFrame(title);
		if (logo != null)
			frame.setIconImage(logo);
		if (fullscreen) {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);
		}
		frame.setSize(width, height);
		frame.setResizable(resizable);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		final JBMain fMain = this;
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				fMain.end();
			}

		});
		frame.setVisible(true);
		start();
	}

	public double[] getRecordedFPS(double seconds) {
		int count = 0;
		while (seconds > 0 && count <= 999) {
			seconds -= recorded[count];
			if (seconds >= 0)
				count++;
		}
		return getRecordedFPS(count);
	}

	public double[] getRecordedFPS(int frames) {
		if (frames > recordedAmt)
			throw new ArrayIndexOutOfBoundsException("You are asking for more frames than are recorded");
		double[] frameValues = new double[frames];
		for (int i = 0; i < frames; i++)
			frameValues[i] = recorded[i];
		return frameValues;
	}

	public double getAverageFPS(double seconds) {
		int count = 0;
		double secondsLeft = seconds;
		while (secondsLeft >= 0 && count <= 999) {
			secondsLeft -= recorded[count];
			if (secondsLeft >= 0)
				count++;
		}
		return (double) count / seconds;
	}
	
	public double getAverageSeconds(double seconds) {
		int count = 0;
		double secondsLeft = seconds;
		while (secondsLeft > 0 && count <= 999) {
			secondsLeft -= recorded[count];
			if (secondsLeft >= 0)
				count++;
		}
		return seconds / count;
	}

	public double getAverageFPS(int frames) {
		if (frames > recordedAmt)
			throw new ArrayIndexOutOfBoundsException("You are asking for more frames than are recorded");
		double total = 0;
		for (int i = 0; i < frames; i++)
			total += recorded[i];
		return (double) frames / total;
	}
	
	public double getAverageSeconds(int frames) {
		if (frames > recordedAmt)
			throw new ArrayIndexOutOfBoundsException("You are asking for more frames than are recorded");
		double total = 0;
		for (int i = 0; i < frames; i++)
			total += recorded[i];
		return total / frames;
	}

	public double getFPS() {
		return 1e9 / (double) getCycleTime();
	}
	
	public void setTargetFPS(double newTarget) {
		targetFPS = 1 / newTarget;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getCycleStart() {
		return cycleStart;
	}

	public long getCycleEnd() {
		return cycleEnd;
	}

	public long getCycleTime() {
		return cycleTime;
	}

	public long getTickStart() {
		return tickStart;
	}

	public long getTickEnd() {
		return tickEnd;
	}

	public long getTickTime() {
		return tickTime;
	}

	public long getRenderStart() {
		return renderStart;
	}

	public long getRenderEnd() {
		return renderEnd;
	}

	public long getRenderTime() {
		return renderTime;
	}

	private void start() {
		Runnable target = () -> {
			
			while (running) {
				tickStart = System.nanoTime();
				tick();
				tickEnd = System.nanoTime();
				tickTime = tickEnd - tickStart;
				renderStart = System.nanoTime();
				render();
				renderEnd = System.nanoTime();
				renderTime = renderEnd - renderStart;
				cycleEnd = System.nanoTime();
				cycleTime = cycleEnd - cycleStart;
				long temp = cycleStart;
				cycleStart = System.nanoTime();
				double seconds = (double) cycleTime / 1e9;
				while (seconds < targetFPS) {
					try {
						if (targetFPS - seconds <= 0.001D) {
							cycleEnd = System.nanoTime();
							cycleTime = cycleEnd - temp;
							seconds = (double) cycleTime / 1e9;
							break;
						} else
							Thread.sleep(1);
					} catch (Exception e) {

					}
					cycleEnd = System.nanoTime();
					cycleTime = cycleEnd - cycleStart;
					seconds = (double) cycleTime / 1e9;
					for (int i = 998; i >= 0; i--)
						recorded[i + 1] = recorded[i];
					recorded[0] = seconds;
				}
			}

		};
		mainThread = new Thread(target);
		mainThread.start();
	}

	public abstract void tick();

	public abstract void render();

	public void end() {
		running = false;
		try {
			if (mainThread != null)
				mainThread.join();
		} catch (Exception e) {
			end();
		}
	}

	public void setTitle(String title) {
		frame.setTitle(title);
	}

	public void setSize(int width, int height) {
		frame.setSize(width, height);
	}

	public void addWindowListener(WindowListener wl) {
		frame.addWindowListener(wl);
	}

	public void setUndecorated(boolean undecorated) {
		frame.setUndecorated(undecorated);
	}

	public void executeEvent(AWTEvent e) {
		frame.dispatchEvent(e);
	}

	public JFrame getWindow() {
		return frame;
	}

	public void setFullscreen(boolean fullscreen) {
		frame.dispose();
		if (fullscreen)
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(fullscreen);
		frame.setVisible(true);
	}

	public void setVisible(boolean visible) {
		if (visible)
			frame.setVisible(true);
		else
			frame.dispose();
	}

}
