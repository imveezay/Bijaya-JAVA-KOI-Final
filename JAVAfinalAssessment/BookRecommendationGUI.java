import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Graphical User Interface (GUI) for the BIJAYA's Book Recommendation System.
 * Built with Java Swing. Provides all features from the TBI plus visual
 * sorting and searching using Merge Sort and Binary/Linear Search.
 *
 * @author Bijaya Sharma
 * @version 2.0
 */
public class BookRecommendationGUI extends JFrame {

    // ── Colours & Fonts ──────────────────────────────────────────────────────
    private static final Color PRIMARY   = new Color(30, 100, 160);
    private static final Color SECONDARY = new Color(245, 248, 252);
    private static final Color ACCENT    = new Color(52, 152, 219);
    private static final Color SUCCESS   = new Color(39, 174, 96);
    private static final Color DANGER    = new Color(192, 57, 43);
    private static final Color WHITE     = Color.WHITE;
    private static final Font  TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  LABEL_FONT  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font  TABLE_FONT  = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Data ─────────────────────────────────────────────────────────────────
    private final BookRecommendationSystemCLI dataStore = new BookRecommendationSystemCLI();

    // ── UI Components ─────────────────────────────────────────────────────────
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;
    private JComboBox<String> sortFieldCombo, sortOrderCombo;

    // ── Constructor ───────────────────────────────────────────────────────────
    public BookRecommendationGUI() {
        dataStore.loadFromFile();
        initUI();
        refreshTable();
    }

    private void initUI() {
        setTitle("BIJAYA's Book Recommendation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(),     BorderLayout.NORTH);
        add(buildMainPanel(),  BorderLayout.CENTER);
        add(buildStatusBar(),  BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("BIJAYA's Book Recommendation System");
        title.setFont(TITLE_FONT);
        title.setForeground(WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("GUI Mode  |  Powered by Merge Sort & Binary Search");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(new Color(200, 220, 240));
        header.add(subtitle, BorderLayout.EAST);

        return header;
    }

    // ── Main Panel with Tabs ──────────────────────────────────────────────────
    private JTabbedPane buildMainPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("👥  Users",           buildUsersTab());
        tabs.addTab("🔍  Sort & Search",   buildSortSearchTab());
        tabs.addTab("📚  Books",           buildBooksTab());
        tabs.addTab("⭐  Recommendations", buildRecommendTab());
        return tabs;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 1 — USERS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(SECONDARY);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"ID", "Name", "Email", "Type", "Books Read", "Points", "Genre"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(TABLE_FONT);
        userTable.setRowHeight(24);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(PRIMARY);
        userTable.getTableHeader().setForeground(WHITE);
        userTable.setSelectionBackground(ACCENT);
        userTable.setSelectionForeground(WHITE);
        userTable.setGridColor(new Color(220, 230, 240));
        userTable.setShowGrid(true);

        // Column widths
        int[] widths = {50, 150, 200, 80, 90, 70, 100};
        for (int i = 0; i < widths.length; i++)
            userTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 225)));
        panel.add(scroll, BorderLayout.CENTER);

        // Button bar
        panel.add(buildUserButtonBar(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildUserButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(SECONDARY);

        JButton addBtn    = styledButton("➕ Add User",    SUCCESS);
        JButton editBtn   = styledButton("✏️ Edit User",   ACCENT);
        JButton deleteBtn = styledButton("🗑️ Delete User",  DANGER);
        JButton evalBtn   = styledButton("📊 Evaluate All", new Color(142, 68, 173));
        JButton refreshBtn = styledButton("🔄 Refresh",    new Color(100, 100, 100));

        addBtn.addActionListener(e -> showAddUserDialog());
        editBtn.addActionListener(e -> showEditUserDialog());
        deleteBtn.addActionListener(e -> deleteSelectedUser());
        evalBtn.addActionListener(e -> runEvaluation());
        refreshBtn.addActionListener(e -> refreshTable());

        bar.add(addBtn); bar.add(editBtn); bar.add(deleteBtn);
        bar.add(evalBtn); bar.add(refreshBtn);
        return bar;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 2 — SORT & SEARCH
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildSortSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(SECONDARY);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // ── Sort panel ──
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        sortPanel.setBackground(WHITE);
        sortPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT),
            "  Sort Users  (Merge Sort — O(n log n))  ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), PRIMARY));

        sortFieldCombo = new JComboBox<>(new String[]{"Name", "ID", "Books Read", "Points"});
        sortOrderCombo = new JComboBox<>(new String[]{"Ascending", "Descending"});
        sortFieldCombo.setFont(LABEL_FONT);
        sortOrderCombo.setFont(LABEL_FONT);

        JButton sortBtn = styledButton("Sort", PRIMARY);
        sortBtn.addActionListener(e -> performSort());

        sortPanel.add(new JLabel("Sort by: ")); sortPanel.add(sortFieldCombo);
        sortPanel.add(new JLabel("Order: "));   sortPanel.add(sortOrderCombo);
        sortPanel.add(sortBtn);

        // ── Search panel ──
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(SUCCESS),
            "  Search Users  ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(39, 130, 80)));

        searchField = new JTextField(20);
        searchField.setFont(LABEL_FONT);

        JButton searchByIdBtn   = styledButton("Search by ID  [Binary O(log n)]", new Color(39, 130, 80));
        JButton searchByNameBtn = styledButton("Search by Name [Linear O(n)]",     new Color(180, 100, 20));

        searchByIdBtn.addActionListener(e   -> performBinarySearch());
        searchByNameBtn.addActionListener(e -> performLinearSearch());

        searchPanel.add(new JLabel("Enter ID or Name: "));
        searchPanel.add(searchField);
        searchPanel.add(searchByIdBtn);
        searchPanel.add(searchByNameBtn);

        // ── Result table ──
        String[] columns = {"ID", "Name", "Email", "Type", "Books Read", "Points", "Genre"};
        DefaultTableModel resultModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable resultTable = new JTable(resultModel);
        resultTable.setFont(TABLE_FONT);
        resultTable.setRowHeight(24);
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(PRIMARY);
        resultTable.getTableHeader().setForeground(WHITE);

        JScrollPane resultScroll = new JScrollPane(resultTable);
        resultScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 225)));

        // Store reference so sort/search can populate it
        resultTable.setName("resultTable");

        // Action wrappers that use this result table
        sortBtn.addActionListener(null);
        sortBtn.removeActionListener(sortBtn.getActionListeners()[0]);
        sortBtn.addActionListener(e -> {
            String field = switch (sortFieldCombo.getSelectedIndex()) {
                case 0 -> "name"; case 1 -> "id"; case 2 -> "booksRead"; default -> "points";
            };
            boolean asc = sortOrderCombo.getSelectedIndex() == 0;
            ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(dataStore.users, field, asc);
            populateResultTable(resultModel, sorted);
            setStatus("Sorted " + sorted.size() + " users by " + field + " (" + (asc ? "Ascending" : "Descending") + ") using Merge Sort O(n log n)");
        });

        searchByIdBtn.removeActionListener(searchByIdBtn.getActionListeners()[0]);
        searchByIdBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(searchField.getText().trim());
                ArrayList<LibraryUser> sortedById = SortSearchUtil.mergeSort(dataStore.users, "id", true);
                LibraryUser found = SortSearchUtil.binarySearchById(sortedById, id);
                resultModel.setRowCount(0);
                if (found != null) {
                    populateResultTable(resultModel, new ArrayList<>(List.of(found)));
                    setStatus("Binary Search: Found user with ID " + id);
                } else {
                    setStatus("Binary Search: No user found with ID " + id);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        searchByNameBtn.removeActionListener(searchByNameBtn.getActionListeners()[0]);
        searchByNameBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isBlank()) { JOptionPane.showMessageDialog(this, "Enter a name keyword.", "Input Error", JOptionPane.WARNING_MESSAGE); return; }
            ArrayList<LibraryUser> results = SortSearchUtil.linearSearchByName(dataStore.users, keyword);
            populateResultTable(resultModel, results);
            setStatus("Linear Search: Found " + results.size() + " user(s) matching \"" + keyword + "\"");
        });

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 6));
        top.setBackground(SECONDARY);
        top.add(sortPanel);
        top.add(searchPanel);

        panel.add(top,         BorderLayout.NORTH);
        panel.add(resultScroll, BorderLayout.CENTER);
        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 3 — BOOKS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(SECONDARY);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Input form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), "  Add New Book  ",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), PRIMARY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField titleField = new JTextField(25); titleField.setFont(LABEL_FONT);
        JTextField genreField = new JTextField(25); genreField.setFont(LABEL_FONT);

        addFormRow(form, gbc, 0, "Book Title:", titleField);
        addFormRow(form, gbc, 1, "Genre:",      genreField);

        JButton addBookBtn = styledButton("➕ Add Book", SUCCESS);
        gbc.gridx = 1; gbc.gridy = 2;
        form.add(addBookBtn, gbc);

        // Book table
        String[] cols = {"#", "Title", "Genre"};
        DefaultTableModel bookModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable bookTable = new JTable(bookModel);
        bookTable.setFont(TABLE_FONT);
        bookTable.setRowHeight(24);
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookTable.getTableHeader().setBackground(PRIMARY);
        bookTable.getTableHeader().setForeground(WHITE);
        bookTable.setSelectionBackground(ACCENT);
        bookTable.setSelectionForeground(WHITE);
        bookTable.setGridColor(new Color(220, 230, 240));

        // Load existing books from dataStore into table
        refreshBookTable(bookModel);

        // Add book button
        addBookBtn.addActionListener(e -> {
            String t = titleField.getText().trim();
            String g = genreField.getText().trim();
            if (t.isEmpty() || g.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Genre cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dataStore.books.add(new String[]{t, g});
            refreshBookTable(bookModel);
            titleField.setText("");
            genreField.setText("");
            setStatus("Book \"" + t + "\" added under genre: " + g);
        });

        // Remove book button
        JButton removeBookBtn = styledButton("🗑️ Remove Book", DANGER);
        removeBookBtn.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bookTitle = (String) bookModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove \"" + bookTitle + "\"?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dataStore.books.remove(selectedRow);
                refreshBookTable(bookModel);
                setStatus("Book \"" + bookTitle + "\" removed.");
            }
        });

        // Button bar below table
        JPanel bookBtnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bookBtnBar.setBackground(SECONDARY);
        bookBtnBar.add(removeBookBtn);

        JScrollPane bookScroll = new JScrollPane(bookTable);
        bookScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 225)));

        panel.add(form,        BorderLayout.NORTH);
        panel.add(bookScroll,  BorderLayout.CENTER);
        panel.add(bookBtnBar,  BorderLayout.SOUTH);
        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 4 — RECOMMENDATIONS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildRecommendTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(SECONDARY);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        inputPanel.setBackground(WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT), "  Get Recommendations  ",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), PRIMARY));

        JTextField idField = new JTextField(10); idField.setFont(LABEL_FONT);
        JButton recBtn = styledButton("Get Recommendations", PRIMARY);

        JTextArea resultArea = new JTextArea(15, 60);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(248, 252, 255));
        resultArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        recBtn.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(idField.getText().trim());
                LibraryUser user = dataStore.findUserById(userId);
                if (user == null) {
                    resultArea.setText("No user found with ID: " + userId);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Recommendations for: ").append(user.getName())
                  .append("\nGenre: ").append(user.getPreferredGenre())
                  .append("\nMember Type: ").append(user.getMemberType())
                  .append("\n\n── Recommended Books ──────────────────\n");

                List<String> recs = getRecommendations(user.getPreferredGenre());
                for (int i = 0; i < recs.size(); i++)
                    sb.append(i + 1).append(". ").append(recs.get(i)).append("\n");

                resultArea.setText(sb.toString());
                setStatus("Recommendations loaded for user: " + user.getName());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric user ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputPanel.add(new JLabel("Enter User ID: "));
        inputPanel.add(idField);
        inputPanel.add(recBtn);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ADD USER DIALOG
    // ═════════════════════════════════════════════════════════════════════════
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(14, 14, 14, 14));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField    = new JTextField(15);
        JTextField nameField  = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"standard", "premium"});
        JTextField booksField = new JTextField(15);
        JTextField pointsField = new JTextField(15);
        JTextField genreField = new JTextField(15);

        addFormRow(form, gbc, 0, "User ID:",       idField);
        addFormRow(form, gbc, 1, "Full Name:",     nameField);
        addFormRow(form, gbc, 2, "Email:",         emailField);
        addFormRow(form, gbc, 3, "Member Type:",   typeCombo);
        addFormRow(form, gbc, 4, "Books Read:",    booksField);
        addFormRow(form, gbc, 5, "Points:",        pointsField);
        addFormRow(form, gbc, 6, "Preferred Genre:", genreField);

        JButton saveBtn   = styledButton("Save", SUCCESS);
        JButton cancelBtn = styledButton("Cancel", new Color(120, 120, 120));
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                int id        = Integer.parseInt(idField.getText().trim());
                String name   = nameField.getText().trim();
                String email  = emailField.getText().trim();
                String type   = (String) typeCombo.getSelectedItem();
                int booksRead = Integer.parseInt(booksField.getText().trim());
                int points    = Integer.parseInt(pointsField.getText().trim());
                String genre  = genreField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || genre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return;
                }
                if (!dataStore.isValidEmail(email)) {
                    JOptionPane.showMessageDialog(dialog, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE); return;
                }
                if (dataStore.findUserById(id) != null) {
                    JOptionPane.showMessageDialog(dialog, "User ID already exists.", "Validation Error", JOptionPane.WARNING_MESSAGE); return;
                }
                if (id < 0 || booksRead < 0 || points < 0) {
                    JOptionPane.showMessageDialog(dialog, "ID, Books Read, and Points must be non-negative.", "Validation Error", JOptionPane.WARNING_MESSAGE); return;
                }

                dataStore.users.add(dataStore.createUser(id, name, email, type, booksRead, points, genre));
                dataStore.saveToFile();
                refreshTable();
                setStatus("User '" + name + "' added successfully.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "ID, Books Read, and Points must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(SECONDARY);
        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);

        dialog.add(form,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // EDIT USER DIALOG
    // ═════════════════════════════════════════════════════════════════════════
    private void showEditUserDialog() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        LibraryUser user = dataStore.findUserById(id);
        if (user == null) return;

        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(420, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(14, 14, 14, 14));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField  = new JTextField(user.getName(), 15);
        JTextField emailField = new JTextField(user.getEmail(), 15);
        JTextField booksField = new JTextField(String.valueOf(user.getBooksRead()), 15);
        JTextField pointsField = new JTextField(String.valueOf(user.getPoints()), 15);
        JTextField genreField = new JTextField(user.getPreferredGenre(), 15);

        addFormRow(form, gbc, 0, "Full Name:",     nameField);
        addFormRow(form, gbc, 1, "Email:",         emailField);
        addFormRow(form, gbc, 2, "Books Read:",    booksField);
        addFormRow(form, gbc, 3, "Points:",        pointsField);
        addFormRow(form, gbc, 4, "Preferred Genre:", genreField);

        JButton saveBtn   = styledButton("Save", SUCCESS);
        JButton cancelBtn = styledButton("Cancel", new Color(120, 120, 120));
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            try {
                String name  = nameField.getText().trim();
                String email = emailField.getText().trim();
                int books    = Integer.parseInt(booksField.getText().trim());
                int points   = Integer.parseInt(pointsField.getText().trim());
                String genre = genreField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || genre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE); return;
                }
                if (!dataStore.isValidEmail(email)) {
                    JOptionPane.showMessageDialog(dialog, "Invalid email format.", "Validation", JOptionPane.WARNING_MESSAGE); return;
                }
                if (books < 0 || points < 0) {
                    JOptionPane.showMessageDialog(dialog, "Books Read and Points must be non-negative.", "Validation", JOptionPane.WARNING_MESSAGE); return;
                }

                user.setName(name); user.setEmail(email);
                user.setBooksRead(books); user.setPoints(points);
                user.setPreferredGenre(genre);
                dataStore.saveToFile();
                refreshTable();
                setStatus("User ID " + id + " updated successfully.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Books Read and Points must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(SECONDARY);
        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ACTIONS
    // ═════════════════════════════════════════════════════════════════════════
    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id   = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + name + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            LibraryUser user = dataStore.findUserById(id);
            if (user != null) {
                dataStore.users.remove(user);
                dataStore.saveToFile();
                refreshTable();
                setStatus("User '" + name + "' deleted.");
            }
        }
    }

    private void runEvaluation() {
        if (dataStore.users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users to evaluate.", "Evaluate", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder log = new StringBuilder("=== Periodic Evaluation Results ===\n\n");
        for (LibraryUser user : dataStore.users) {
            int before = user.getPoints();
            user.evaluate();
            int after = user.getPoints();
            int diff  = after - before;
            log.append(user.getName())
               .append(" (").append(user.getMemberType()).append(")")
               .append(" — Books: ").append(user.getBooksRead())
               .append(" | Points: ").append(before)
               .append(" → ").append(after)
               .append(" (").append(diff >= 0 ? "+" : "").append(diff).append(")\n");
        }
        dataStore.saveToFile();
        refreshTable();
        JTextArea area = new JTextArea(log.toString(), 15, 45);
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Evaluation Results", JOptionPane.INFORMATION_MESSAGE);
        setStatus("Periodic evaluation completed for " + dataStore.users.size() + " users.");
    }

    private void performSort() {
        String field = switch (sortFieldCombo.getSelectedIndex()) {
            case 0 -> "name"; case 1 -> "id"; case 2 -> "booksRead"; default -> "points";
        };
        boolean asc = sortOrderCombo.getSelectedIndex() == 0;
        ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(dataStore.users, field, asc);
        populateTableModel(tableModel, sorted);
        setStatus("Sorted by " + field + " using Merge Sort O(n log n)");
    }

    private void performBinarySearch() {
        try {
            int id = Integer.parseInt(searchField.getText().trim());
            ArrayList<LibraryUser> sortedById = SortSearchUtil.mergeSort(dataStore.users, "id", true);
            LibraryUser found = SortSearchUtil.binarySearchById(sortedById, id);
            tableModel.setRowCount(0);
            if (found != null) {
                populateTableModel(tableModel, new ArrayList<>(List.of(found)));
                setStatus("Binary Search [O(log n)]: Found user ID " + id);
            } else {
                setStatus("Binary Search [O(log n)]: No user with ID " + id);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performLinearSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isBlank()) return;
        ArrayList<LibraryUser> results = SortSearchUtil.linearSearchByName(dataStore.users, keyword);
        populateTableModel(tableModel, results);
        setStatus("Linear Search [O(n)]: Found " + results.size() + " user(s) matching \"" + keyword + "\"");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════════════════════════
    private void refreshBookTable(DefaultTableModel model) {
        model.setRowCount(0);
        int count = 1;
        for (String[] book : dataStore.books) {
            model.addRow(new Object[]{count++, book[0], book[1]});
        }
    }

    private void refreshTable() {
        populateTableModel(tableModel, dataStore.users);
        setStatus("Showing " + dataStore.users.size() + " users.");
    }

    private void populateTableModel(DefaultTableModel model, ArrayList<LibraryUser> list) {
        model.setRowCount(0);
        for (LibraryUser u : list)
            model.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(),
                u.getMemberType(), u.getBooksRead(), u.getPoints(), u.getPreferredGenre()});
    }

    private void populateResultTable(DefaultTableModel model, ArrayList<LibraryUser> list) {
        model.setRowCount(0);
        for (LibraryUser u : list)
            model.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(),
                u.getMemberType(), u.getBooksRead(), u.getPoints(), u.getPreferredGenre()});
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(220, 230, 245));
        bar.setBorder(new EmptyBorder(4, 12, 4, 12));
        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(60, 80, 100));
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private List<String> getRecommendations(String genre) {
        return switch (genre.trim().toLowerCase()) {
            case "fiction"  -> Arrays.asList("The Midnight Library", "The Alchemist", "Before the Coffee Gets Cold");
            case "science"  -> Arrays.asList("A Brief History of Time", "Astrophysics for People in a Hurry", "The Selfish Gene");
            case "mystery"  -> Arrays.asList("Gone Girl", "The Silent Patient", "Sherlock Holmes");
            default         -> Arrays.asList("Atomic Habits", "Dune", "The Psychology of Money");
        };
    }
}
