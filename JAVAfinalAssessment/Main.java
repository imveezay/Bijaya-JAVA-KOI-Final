import javax.swing.*;
import java.util.Scanner;

/**
 * Main entry point for the BIJAYA's Book Recommendation System.
 * Allows the user to choose between GUI mode and Text-Based Interface (TBI) mode.
 *
 * @author Bijaya Sharma
 * @version 2.0
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("  BIJAYA's Book Recommendation System");
        System.out.println("============================================");
        System.out.println("Please select your preferred interface:");
        System.out.println("  1. Graphical User Interface (GUI)");
        System.out.println("  2. Text-Based Interface (TBI)");
        System.out.print("Enter your choice (1 or 2): ");

        String input = scanner.nextLine().trim();

        switch (input) {
            case "1" -> {
                System.out.println("Launching GUI mode...");
                // Run GUI on the Event Dispatch Thread as required by Swing
                SwingUtilities.invokeLater(() -> {
                    BookRecommendationGUI gui = new BookRecommendationGUI();
                    gui.setVisible(true);
                });
            }
            case "2" -> {
                System.out.println("Launching Text-Based Interface...");
                BookRecommendationSystemCLI cli = new BookRecommendationSystemCLI();
                cli.loadFromFile();
                cli.run();
            }
            default -> {
                System.out.println("Invalid choice. Defaulting to Text-Based Interface.");
                BookRecommendationSystemCLI cli = new BookRecommendationSystemCLI();
                cli.loadFromFile();
                cli.run();
            }
        }

        scanner.close();
    }
}
