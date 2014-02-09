package edu.kit.pse.ws2013.routekit.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A {@link CLI} that pretty-prints a progress bar using terminal escape
 * characters. Falls back to {@link CLI} if the terminal doesn’t support moving
 * the cursor around.
 */
public class TerminalCLI extends CLI {

	private final String cuu;
	private final String el;
	private final String home;
	private final int cols;

	public TerminalCLI(String[] args) {
		super(args);
		if (actions == ManagementActions.noActions) {
			// error in the options, don’t bother setting up the terminal
			cuu = el = home = null;
			cols = 0;
			return;
		}
		cuu = processOutput("tput", "cuu", "1");
		el = processOutput("tput", "el");
		home = processOutput("tput", "hpa", "0");
		int _cols;
		try {
			_cols = Integer.parseInt(processOutput("tput", "cols"));
		} catch (NumberFormatException f) {
			_cols = 0;
		}
		cols = _cols;
		System.out.println(); // move the cursor down
		System.out.println(); // we move it up each time we print something
	}

	private String task = "";
	private float progress = 0f;

	@Override
	public void startRoot(String name) {
		this.task = name;
		if (cuu == null || el == null || cols == 0) {
			super.startRoot(name);
		} else {
			print();
		}
	}

	@Override
	public void beginTask(String name) {
		this.task = name;
		if (cuu == null || el == null || cols == 0) {
			super.beginTask(name);
		} else {
			print();
		}
	}

	@Override
	public void progress(float progress, String name) {
		this.task = name;
		this.progress = progress;
		if (cuu == null || el == null || cols == 0) {
			super.progress(progress, name);
		} else {
			print();
		}
	}

	@Override
	public void endTask(String name) {
		this.task = name;
		if (cuu == null || el == null || cols == 0) {
			super.endTask(name);
		} else {
			print();
		}
	}

	@Override
	public void finishRoot(String name) {
		this.task = name;
		if (cuu == null || el == null || cols == 0) {
			super.finishRoot(name);
		} else {
			print();
		}
	}

	float lastPrintedProgress = -1f;

	private void print() {
		if (progress - lastPrintedProgress < .001f) {
			return;
		}
		lastPrintedProgress = progress;
		System.out.print(home);
		System.out.print(cuu);
		System.out.print(el);
		System.out.print(cuu);
		System.out.print(el);
		System.out.println(task);
		System.out.print(el);
		// print progress bar
		System.out.print(String.format("%3d%% [", (int) (progress * 100)));
		final int width = cols - 7;
		final int done = Math.round(progress * width);
		final int remaining = width - done;
		for (int i = 0; i < done - 1; i++) {
			System.out.print("=");
		}
		if (done > 0) {
			System.out.print(progress == 1f ? "=" : ">");
		}
		for (int i = 0; i < remaining; i++) {
			System.out.print(" ");
		}
		System.out.println("]");
	}

	private static String processOutput(String... command) {
		ProcessBuilder pb = new ProcessBuilder(command);
		try {
			Process p = pb.start();
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				// sb.append(System.getProperty("line.separator"));
			}
			if (p.waitFor() != 0) {
				return null;
			}
			return sb.toString();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
