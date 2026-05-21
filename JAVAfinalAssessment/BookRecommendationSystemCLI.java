import java.io.*;
import java.util.*;

/**
 * Text-Based Interface (TBI) for the BIJAYA's Book Recommendation System.
 * Provides full CRUD, sorting (Merge Sort), searching (Binary + Linear),
 * periodic evaluation, and book recommendations via a console menu.
 *
 * @author Bijaya Sharma (enhanced from Assessment 3 group base)
 * @version 2.0
 */
public class BookRecommendationSystemCLI {
    private static final String FILE_NAME = "members.csv";
    final ArrayList<LibraryUser> users = new ArrayList<>();
    final ArrayList<String[]> books = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        BookRecommendationSystemCLI app = new BookRecommendationSystemCLI();
        app.loadFromFile();
        app.run();
    }

    /** Made public so Main.java can call it after mode selection. */
    public void run() {
        int choice;
        do {
            showMenu();
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1  -> addUser();
                case 2  -> viewAllUsers();
                case 3  -> queryUser();
                case 4  -> updateUser();
                case 5  -> deleteUser();
                case 6  -> runPeriodicEvaluation();
                case 7  -> recommendBooks();
                case 8  -> addBook();
                case 9  -> viewAllBooks();
                case 10 -> sortUsers();
                case 11 -> searchUsers();
                case 12 -> saveAndExit();
                default -> System.out.println("Invalid choice. Please select 1 to 12.");
            }
        } while (choice != 12);
    }

    private void showMenu() {
        System.out.println("\n============================================");
        System.out.println(" BIJAYA's Book Recommendation System (TBI)");
        System.out.println("============================================");
        System.out.println("1.  Add New User");
        System.out.println("2.  View All Users");
        System.out.println("3.  Query User");
        System.out.println("4.  Update User");
        System.out.println("5.  Delete User");
        System.out.println("6.  Run Periodic Evaluation");
        System.out.println("7.  Recommend Books");
        System.out.println("8.  Add New Book");
        System.out.println("9.  View Added Books");
        System.out.println("10. Sort Users");
        System.out.println("11. Search Users");
        System.out.println("12. Save & Exit");
    }

    private void addUser() {
        System.out.println("\n--- Add New User ---");
        int id        = readUniqueId();
        String name   = readNonEmptyString("Enter full name: ");
        String email  = readValidEmail("Enter email: ");
        String type   = readMemberType("Enter member type (standard/premium): ");
        int booksRead = readNonNegativeInt("Enter books read: ");
        int points    = readNonNegativeInt("Enter initial points: ");
        String genre  = readNonEmptyString("Enter preferred genre: ");

        users.add(createUser(id, name, email, type, booksRead, points, genre));
        System.out.println("User added successfully.");
        saveToFile();
    }

    private void viewAllUsers() {
        System.out.println("\n--- All Users ---");
        if (users.isEmpty()) { System.out.println("No users found."); return; }
        for (LibraryUser user : users) System.out.println(user);
    }

    private void queryUser() {
        System.out.println("\n--- Query User ---");
        String input = readNonEmptyString("Search by ID or Name: ");
        boolean found = false;
        try {
            int id = Integer.parseInt(input);
            for (LibraryUser user : users) {
                if (user.getId() == id) { System.out.println(user); found = true; }
            }
        } catch (NumberFormatException e) {
            String keyword = input.toLowerCase();
            for (LibraryUser user : users) {
                if (user.getName().toLowerCase().contains(keyword)) { System.out.println(user); found = true; }
            }
        }
        if (!found) System.out.println("No matching user found.");
    }

    private void updateUser() {
        System.out.println("\n--- Update User ---");
        int id = readInt("Enter user ID to update: ");
        LibraryUser user = findUserById(id);
        if (user == null) { System.out.println("User not found."); return; }

        System.out.println("Current record: " + user);
        System.out.println("Leave a field blank to keep the current value.");

        String newName = readOptionalString("New name [" + user.getName() + "]: ");
        if (!newName.isBlank()) user.setName(newName);

        String newEmail = readOptionalString("New email [" + user.getEmail() + "]: ");
        if (!newEmail.isBlank()) {
            while (!isValidEmail(newEmail)) {
                System.out.println("Invalid email format.");
                newEmail = readOptionalString("New email [" + user.getEmail() + "]: ");
                if (newEmail.isBlank()) break;
            }
            if (!newEmail.isBlank()) user.setEmail(newEmail);
        }

        String newBooksRead = readOptionalString("New books read [" + user.getBooksRead() + "]: ");
        if (!newBooksRead.isBlank()) user.setBooksRead(parseNonNegativeInt(newBooksRead, "books read"));

        String newPoints = readOptionalString("New points [" + user.getPoints() + "]: ");
        if (!newPoints.isBlank()) user.setPoints(parseNonNegativeInt(newPoints, "points"));

        String newGenre = readOptionalString("New preferred genre [" + user.getPreferredGenre() + "]: ");
        if (!newGenre.isBlank()) user.setPreferredGenre(newGenre);

        System.out.println("User updated successfully.");
        saveToFile();
    }

    private void deleteUser() {
        System.out.println("\n--- Delete User ---");
        int id = readInt("Enter user ID to delete: ");
        LibraryUser user = findUserById(id);
        if (user == null) { System.out.println("User not found."); return; }
        users.remove(user);
        System.out.println("User deleted successfully.");
        saveToFile();
    }

    private void runPeriodicEvaluation() {
        System.out.println("\n--- Periodic Evaluation ---");
        if (users.isEmpty()) { System.out.println("No users available."); return; }
        for (LibraryUser user : users) user.evaluate();
        saveToFile();
        System.out.println("Periodic evaluation completed.");
    }

    private void recommendBooks() {
        System.out.println("\n--- Book Recommendations ---");
        int id = readInt("Enter user ID: ");
        LibraryUser user = findUserById(id);
        if (user == null) { System.out.println("User not found."); return; }
        System.out.println("Recommended books for " + user.getName() + " (Genre: " + user.getPreferredGenre() + "):");
        for (String book : getRecommendationsByGenre(user.getPreferredGenre()))
            System.out.println("- " + book);
    }

    private void addBook() {
        System.out.println("\n--- Add New Book ---");
        String title = readNonEmptyString("Enter book title: ");
        String genre = readNonEmptyString("Enter book genre: ");
        books.add(new String[]{title, genre});
        System.out.println("Book \"" + title + "\" added under genre: " + genre);
    }

    private void viewAllBooks() {
        System.out.println("\n--- Added Books ---");
        if (books.isEmpty()) { System.out.println("No books added yet."); return; }
        System.out.printf("%-5s %-40s %-20s%n", "No.", "Title", "Genre");
        System.out.println("-".repeat(67));
        int count = 1;
        for (String[] book : books)
            System.out.printf("%-5d %-40s %-20s%n", count++, book[0], book[1]);
        System.out.println("-".repeat(67));
        System.out.println("Total books: " + books.size());
    }

    // ── Sort Users using Merge Sort ──────────────────────────────────────────
    private void sortUsers() {
        System.out.println("\n--- Sort Users (Merge Sort | O(n log n)) ---");
        if (users.isEmpty()) { System.out.println("No users to sort."); return; }

        System.out.println("Sort by: 1) Name  2) ID  3) Books Read  4) Points");
        int fieldChoice = readInt("Enter choice: ");
        String field = switch (fieldChoice) {
            case 1 -> "name";
            case 2 -> "id";
            case 3 -> "booksRead";
            case 4 -> "points";
            default -> { System.out.println("Invalid choice. Defaulting to ID."); yield "id"; }
        };

        System.out.println("Order: 1) Ascending  2) Descending");
        int orderChoice = readInt("Enter choice: ");
        boolean ascending = (orderChoice != 2);

        ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(users, field, ascending);
        System.out.println("\nSorted Results:");
        for (LibraryUser u : sorted) System.out.println(u);
    }

    // ── Search Users using Binary Search or Linear Search ────────────────────
    private void searchUsers() {
        System.out.println("\n--- Search Users ---");
        System.out.println("1) Search by ID  [Binary Search | O(log n)]");
        System.out.println("2) Search by Name keyword  [Linear Search | O(n)]");
        int choice = readInt("Enter choice: ");

        if (choice == 1) {
            int targetId = readInt("Enter user ID: ");
            ArrayList<LibraryUser> sortedById = SortSearchUtil.mergeSort(users, "id", true);
            LibraryUser result = SortSearchUtil.binarySearchById(sortedById, targetId);
            if (result != null) System.out.println("Found: " + result);
            else System.out.println("No user found with ID: " + targetId);

        } else if (choice == 2) {
            String keyword = readNonEmptyString("Enter name keyword: ");
            ArrayList<LibraryUser> results = SortSearchUtil.linearSearchByName(users, keyword);
            if (results.isEmpty()) System.out.println("No users found matching: \"" + keyword + "\"");
            else results.forEach(System.out::println);

        } else {
            System.out.println("Invalid choice.");
        }
    }

    private List<String> getRecommendationsByGenre(String genre) {
        String normalized = genre.trim().toLowerCase();
        List<String> userAdded = new ArrayList<>();
        for (String[] book : books) {
            if (book[1].trim().equalsIgnoreCase(normalized)) userAdded.add(book[0]);
        }
        List<String> defaults = switch (normalized) {
            case "fiction"  -> new ArrayList<>(Arrays.asList("The Midnight Library", "The Alchemist", "Before the Coffee Gets Cold"));
            case "science"  -> new ArrayList<>(Arrays.asList("A Brief History of Time", "Astrophysics for People in a Hurry", "The Selfish Gene"));
            case "mystery"  -> new ArrayList<>(Arrays.asList("Gone Girl", "The Silent Patient", "Sherlock Holmes"));
            default         -> new ArrayList<>(Arrays.asList("Atomic Habits", "Dune", "The Psychology of Money"));
        };
        for (String book : userAdded) {
            if (!defaults.contains(book)) defaults.add(0, book);
        }
        return defaults;
    }

    /** Made public so Main.java can call it after mode selection. */
    public void loadFromFile() {
        users.clear();
        Set<Integer> usedIds = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String header = br.readLine();
            if (header == null) { System.out.println("CSV file is empty."); return; }
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length != 7) {
                    System.out.println("Skipped invalid row at line " + lineNumber); continue;
                }
                try {
                    int id        = Integer.parseInt(data[0].trim());
                    if (usedIds.contains(id)) { System.out.println("Skipped duplicate ID: " + id); continue; }
                    String name   = data[1].trim();
                    String email  = data[2].trim();
                    String type   = data[3].trim().toLowerCase();
                    int booksRead = Integer.parseInt(data[4].trim());
                    int points    = Integer.parseInt(data[5].trim());
                    String genre  = data[6].trim();
                    if (name.isEmpty() || !isValidEmail(email) ||
                        (!type.equals("standard") && !type.equals("premium")) ||
                        booksRead < 0 || points < 0) {
                        System.out.println("Skipped invalid data at line " + lineNumber); continue;
                    }
                    users.add(createUser(id, name, email, type, booksRead, points, genre));
                    usedIds.add(id);
                } catch (NumberFormatException e) {
                    System.out.println("Skipped non-numeric data at line " + lineNumber);
                }
            }
            System.out.println("Loaded " + users.size() + " valid users from " + FILE_NAME);
        } catch (FileNotFoundException e) {
            System.out.println("No existing data file found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            pw.println("id,name,email,memberType,booksRead,points,preferredGenre");
            for (LibraryUser user : users)
                pw.println(user.getId() + "," + user.getName() + "," + user.getEmail() + "," +
                        user.getMemberType() + "," + user.getBooksRead() + "," +
                        user.getPoints() + "," + user.getPreferredGenre());
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    private void saveAndExit() { saveToFile(); System.out.println("Data saved. Goodbye."); }

    LibraryUser createUser(int id, String name, String email, String type,
                            int booksRead, int points, String genre) {
        if (type.equalsIgnoreCase("premium"))
            return new PremiumMember(id, name, email, booksRead, points, genre);
        return new StandardMember(id, name, email, booksRead, points, genre);
    }

    LibraryUser findUserById(int id) {
        for (LibraryUser user : users) { if (user.getId() == id) return user; }
        return null;
    }

    private int readUniqueId() {
        while (true) {
            int id = readInt("Enter user ID: ");
            if (id < 0) { System.out.println("ID cannot be negative."); continue; }
            if (findUserById(id) != null) { System.out.println("ID already exists."); continue; }
            return id;
        }
    }

    int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid number. Please try again."); }
        }
    }

    private int readNonNegativeInt(String prompt) {
        while (true) {
            int v = readInt(prompt);
            if (v >= 0) return v;
            System.out.println("Value cannot be negative.");
        }
    }

    private int parseNonNegativeInt(String value, String fieldName) {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < 0) throw new NumberFormatException();
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName);
        }
    }

    String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("This field cannot be empty.");
        }
    }

    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readMemberType(String prompt) {
        while (true) {
            String type = readNonEmptyString(prompt).toLowerCase();
            if (type.equals("standard") || type.equals("premium")) return type;
            System.out.println("Invalid type. Enter 'standard' or 'premium'.");
        }
    }

    private String readValidEmail(String prompt) {
        while (true) {
            String email = readNonEmptyString(prompt);
            if (isValidEmail(email)) return email;
            System.out.println("Invalid email format.");
        }
    }

    boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
