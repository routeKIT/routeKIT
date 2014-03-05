package edu.kit.pse.ws2013.routekit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Resources {
	public static String getKarlsruheBigLocation() {
		File file = new File("karlsruhe_big.osm").getAbsoluteFile();
		if (!file.exists()) {
			try {
				URL website = new URL(
						"http://algo2.iti.kit.edu/documents/PSE_WS1314/karlsruhe_big.osm");
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("pse_ws1314",
								"pse_student_ws1314".toCharArray());
					}
				});
				try (ReadableByteChannel rbc = Channels.newChannel(website
						.openStream());
						FileOutputStream fos = new FileOutputStream(file);) {
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} catch (MalformedURLException e) {
				// won’t happen, hardcoded URL
				e.printStackTrace();
			}
		}
		return file.getPath();
	}
}
