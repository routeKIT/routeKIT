package edu.kit.pse.ws2013.routekit;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class RobotUI {

	private static Robot tester;

	private static void waitForColor() {
		delay(2000);
		while (Color.white.equals(tester.getPixelColor(450, 428))) {
			delay(500);
		}
	}

	public static void main(String[] args) throws AWTException {
		tester = new Robot();
		System.out.println(MouseInfo.getPointerInfo().getLocation());
		delay(1000);
		focus();
		testAbout();
		testRoute("49.010628 8.409197", "49.008965 8.412416");
		focus();
		testRoute2();
		exportGPX();
		exportHTML();
		swap();
		swap();
		switchOSM();
		testOpenAdminMenu();
		testZoom();

		testImportPrecalcMix();
		testZoom2();// for one-way arrows
		testProfile();
		testHistory();
		deleteMap();
		deleteProfile();
		delay(5000);
		System.out.println(MouseInfo.getPointerInfo().getLocation());
	}

	private static void testHistory() {
		openMenu('r', 'v');
		clickAt(535, 573); // Abbrechen
		clickAt(135, 141); // Verlauf Button
		clickAt(441, 567); // OK ohne selection
		clickAt(377, 147); // Selection
		clickAt(441, 567); // OK
		openMenu('r', 'v');
		clickAt(377, 147); // Selection
		clickAt(377, 147); // Selection

	}

	private static void testProfile() {
		openMenu('v', 'p');
		delay(1000);
		clickAt(721, 193);
		type("testprofile1");
		type(KeyEvent.VK_ENTER);
		type(KeyEvent.VK_TAB);
		type(KeyEvent.VK_TAB);
		type(KeyEvent.VK_TAB);
		type(KeyEvent.VK_TAB);
		type(KeyEvent.VK_TAB);
		selectAll();
		type("400");
		type(KeyEvent.VK_TAB);
		selectAll();
		type("300");
		type(KeyEvent.VK_TAB);
		selectAll();
		type("50000");
		type(KeyEvent.VK_TAB);
		selectAll();
		type("1");
		type(KeyEvent.VK_TAB);
		selectAll();
		type("1");
		clickAt(672, 530);
		openMenu('v', 'k');

		clickAt(717, 314); // hinzufügen
		delay(500);
		type(KeyEvent.VK_ENTER);
		delay(500);
		clickAt(665, 527);// ok
		delay(6000);
		clickAt(449, 396);// yes, I am sure
		delay(3000);
	}

	private static void testImportPrecalcMix() {
		clickAt(479, 74);
		clickAt(263, 234);// Imporieren
		delay(300);
		type(KeyEvent.VK_ESCAPE);
		delay(300);
		clickAt(263, 234);// Imporieren
		delay(300);
		type(KeyEvent.VK_TAB);
		delay(300);
		type(KeyEvent.VK_TAB);
		delay(300);
		type(KeyEvent.VK_TAB);
		delay(300);
		type("C:\\Users\\Felix\\git\\pse\\karlsruhe_big.osm");
		type(KeyEvent.VK_TAB);
		type("testkarte");
		type(KeyEvent.VK_ENTER);
		delay(300);
		clickAt(664, 529);// ok
		delay(6000);
		clickAt(449, 396);// yes, I am sure
		delay(4000);
		precalc();
		openMenu('v', 'p');
		delay(500);
		type(KeyEvent.VK_DOWN);
		type(KeyEvent.VK_UP);
		type(KeyEvent.VK_ENTER);
		type(KeyEvent.VK_ENTER);
		delay(500);
		precalc();
	}

	private static void precalc() {
		assertColor(589, 378, Color.RED);
		clickAt(589, 378);// precalc
		waitForColor();
	}

	private static void deleteProfile() {
		openMenu('v', 'p');
		delay(500);
		clickAt(665, 196);
		delay(300);
		clickAt(664, 529);// ok
	}

	private static void deleteMap() {
		openMenu('v', 'k');
		delay(500);
		clickAt(453, 235);
		delay(300);
		clickAt(664, 529);// ok
		delay(6000);
		clickAt(449, 442);// yes, I am sure
	}

	private static void assertColor(int i, int j, Color toTest) {
		Color c = tester.getPixelColor(i, j);
		if (!c.equals(toTest)) {
			System.out.println(c);
			System.out.println("Error");
		}

	}

	private static void testZoom2() {
		tester.mouseMove(700, 300);
		tester.mouseWheel(-1);
		delay(400);
		tester.mouseWheel(-1);
		delay(400);
		testRoute("49.008312 8.407717", "49.009594 8.406794");
		delay(1000);
		swap();
		tester.mouseWheel(1);
		delay(400);
		tester.mouseWheel(1);
		delay(400);
	}

	private static void testZoom() {
		clickAt(958, 118);
		clickAt(958, 148);
		tester.mouseMove(700, 300);
		tester.mouseWheel(1);
		delay(200);
		tester.mouseWheel(-1);
		delay(200);
		tester.mouseWheel(1);
		delay(200);
		tester.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		delay(10);
		tester.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		tester.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		delay(10);
		tester.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		delay(300);
	}

	private static void switchOSM() {
		openMenu('v', 'o');
		delay(2000);
		openMenu('v', 'o');
	}

	private static void testOpenAdminMenu() {
		openMenu('v', 'k');
		// type(KeyEvent.VK_ESCAPE);
		clickAt(762, 529); // TODO?
		delay(500);
		openMenu('v', 'p');
		type(KeyEvent.VK_ESCAPE);
		delay(500);
	}

	private static void focus() {
		clickAt(100, 200);
	}

	private static void swap() {
		clickAt(36, 81);
	}

	private static void testRoute(String a, String b) {
		clickAt(188, 71);
		selectAll();
		type(a);
		clickAt(188, 98);
		selectAll();
		type(b);
		type(KeyEvent.VK_ENTER);
	}

	private static void testRoute2() {
		tester.mouseMove(347, 490);
		tester.mousePress(MouseEvent.BUTTON3_DOWN_MASK);
		delay(100);
		tester.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK);
		delay(100);
		clickAt(360, 500);

		delay(100);
		tester.mouseMove(976, 453);
		tester.mousePress(MouseEvent.BUTTON3_DOWN_MASK);
		delay(100);
		tester.mouseRelease(MouseEvent.BUTTON3_DOWN_MASK);
		delay(100);
		clickAt(980, 490);
	}

	private static void selectAll() {
		// type(KeyEvent.VK_END);
		// delay(500);
		// tester.keyPress(KeyEvent.VK_SHIFT);
		// System.out.println("HIFT");
		// delay(1000);
		type(KeyEvent.VK_HOME);
		// delay(1000);
		// System.out.println("EHS");
		// tester.keyRelease(KeyEvent.VK_SHIFT);
		// for (int i = 0; i < 30; i++) {
		// type(KeyEvent.VK_DELETE);
		// }
		tester.keyPress(KeyEvent.VK_CONTROL);
		delay(100);
		type(KeyEvent.VK_A);
		delay(100);
		tester.keyRelease(KeyEvent.VK_CONTROL);
	}

	private static void exportHTML() {
		openMenu('e', 'h');
		delay(1000);
		type(KeyEvent.VK_ESCAPE);// CANCEL
		delay(1000);

		openMenu('e', 'h');
		delay(1000);
		type("test.html");
		type(KeyEvent.VK_ENTER);
		delay(1000);
	}

	private static void openMenu(char menu, char sub) {
		tester.keyPress(KeyEvent.VK_ALT);
		type(KeyEvent.VK_A + menu - 'a');
		tester.keyRelease(KeyEvent.VK_ALT);
		type(KeyEvent.VK_A + sub - 'a');
		delay(500);
	}

	private static void exportGPX() {
		openMenu('e', 'g');
		delay(1000);
		type(KeyEvent.VK_ESCAPE);// CANCEL
		delay(1000);

		openMenu('e', 'g');
		delay(1000);
		type("test.gpx");
		type(KeyEvent.VK_ENTER);
		delay(1000);
	}

	private static void testAbout() {
		tester.keyPress(KeyEvent.VK_ALT);
		type(KeyEvent.VK_R);
		tester.keyRelease(KeyEvent.VK_ALT);
		type(KeyEvent.VK_E);
		delay(500);
		tester.keyPress(KeyEvent.VK_ALT);
		type(KeyEvent.VK_F4);
		tester.keyRelease(KeyEvent.VK_ALT);
	}

	private static void type(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				type(KeyEvent.VK_0 + c - '0');
			} else if (c == ' ') {
				type(KeyEvent.VK_SPACE);
			} else if (c == '.') {
				type(KeyEvent.VK_PERIOD);
			} else if (c == '\\' || c == '/') { // do as a /
				tester.keyPress(KeyEvent.VK_SHIFT);
				type(KeyEvent.VK_7);
				tester.keyRelease(KeyEvent.VK_SHIFT);
			} else if (c == ':') {
				tester.keyPress(KeyEvent.VK_SHIFT);
				type(KeyEvent.VK_PERIOD);
				tester.keyRelease(KeyEvent.VK_SHIFT);
			} else if (c == '_') {
				tester.keyPress(KeyEvent.VK_SHIFT);
				type(KeyEvent.VK_MINUS);
				tester.keyRelease(KeyEvent.VK_SHIFT);
			} else if (c >= 'A' && c <= 'Z') {
				tester.keyPress(KeyEvent.VK_SHIFT);
				type(KeyEvent.VK_A + c - 'A');
				tester.keyRelease(KeyEvent.VK_SHIFT);
			} else if (c >= 'a' && c <= 'z') {
				type(KeyEvent.VK_A + c - 'a');
			}
		}
	}

	private static void type(int vkR) {
		tester.keyPress(vkR);
		delay(50);
		tester.keyRelease(vkR);
	}

	private static void clickAt(int i, int j) {
		tester.mouseMove(i, j);
		tester.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		delay(100);
		tester.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		delay(300);
	}

	private static void delay(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
