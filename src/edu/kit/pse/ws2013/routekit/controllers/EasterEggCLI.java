package edu.kit.pse.ws2013.routekit.controllers;

public class EasterEggCLI extends CLI {

	public EasterEggCLI(String[] args) {
		super(args);
		// There are no easter egss in this program.
		if (args.length == 1 && args[0].equals("moo")) {
			System.out.println("There is no aptitude in this program.");
			System.exit(0);
		} else if (args.length == 2 && args[0].startsWith("-v")
				&& args[1].equals("moo")) {
			System.out
					.println("Wait, that didn’t come out right. There is aptitude in this program; what I meant is that this program isn’t aptitude.");
			System.exit(0);
		} else if (args.length == 3 && args[0].startsWith("-v")
				&& args[1].equals("moo")
				&& args[2].equals("There are no easter eggs in this program.")) {
			System.out.println("Who said that?");
			System.out.println("              \\ /");
			System.out.println("            -->*<--");
			System.out.println("              /o\\");
			System.out.println("             /_\\_\\");
			System.out.println("            /_/_0_\\");
			System.out.println("           /_o_\\_\\_\\");
			System.out.println("          /_/_/_/_/o\\");
			System.out.println("         /@\\_\\_\\@\\_\\_\\");
			System.out.println("        /_/_/O/_/_/_/_\\");
			System.out.println("       /_\\_\\_\\_\\_\\o\\_\\_\\");
			System.out.println("      /_/0/_/_/_0_/_/@/_\\");
			System.out.println("     /_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\");
			System.out.println("    /_/o/_/_/@/_/_/o/_/0/_\\");
			System.out.println("             [___]  ");
			System.exit(0);
		}
	}
}
