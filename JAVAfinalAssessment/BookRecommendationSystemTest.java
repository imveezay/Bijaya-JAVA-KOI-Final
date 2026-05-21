import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Test Cases for the E-UNIVERSE Book Recommendation System.
 * Tests cover Merge Sort, Binary Search, Linear Search, and evaluate() logic.
 *
 * @author Bijaya Sharma
 * @version 2.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookRecommendationSystemTest {

    private ArrayList<LibraryUser> users;
    private BookRecommendationSystemCLI cli;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();
        users.add(new StandardMember(3, "Balen Brown",  "Balen@mail.com", 8,  50, "fiction"));
        users.add(new StandardMember(1, "Harke Smith",    "Harke@mail.com",   2,  30, "science"));
        users.add(new PremiumMember (5, "Eve Davis",      "eve@mail.com",     10, 90, "mystery"));
        users.add(new PremiumMember (2, "Bob Johnson",    "bob@mail.com",     6,  70, "fiction"));
        users.add(new StandardMember(4, "Diana Prince",   "diana@mail.com",   0,  20, "science"));
        cli = new BookRecommendationSystemCLI();
    }

    // ── MERGE SORT TESTS ─────────────────────────────────────────────────────

    @Test
@Order(1)
@DisplayName("Merge Sort - Sort by Name Ascending")
void testMergeSortByNameAscending() {
    ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(users, "name", true);
    assertEquals(5, sorted.size(), "Sorted list should have 5 users");
    for (int i = 0; i < sorted.size() - 1; i++) {
        assertTrue(
            sorted.get(i).getName().compareToIgnoreCase(sorted.get(i + 1).getName()) <= 0,
            "Names should be in ascending order"
        );
    }
}

    @Test
    @Order(2)
    @DisplayName("Merge Sort - Sort by ID Ascending")
    void testMergeSortByIdAscending() {
        ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(users, "id", true);
        assertEquals(1, sorted.get(0).getId(), "First ID should be 1");
        assertEquals(5, sorted.get(4).getId(), "Last ID should be 5");
    }

    @Test
    @Order(3)
    @DisplayName("Merge Sort - Empty List Returns Empty")
    void testMergeSortEmptyList() {
        ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(new ArrayList<>(), "name", true);
        assertNotNull(sorted, "Result should not be null");
        assertTrue(sorted.isEmpty(), "Sorted empty list should be empty");
    }

    @Test
    @Order(4)
    @DisplayName("Merge Sort - Original List Not Modified")
    void testMergeSortDoesNotModifyOriginal() {
        String firstName = users.get(0).getName();
        SortSearchUtil.mergeSort(users, "name", true);
        assertEquals(firstName, users.get(0).getName(), "Original list should not change");
    }

    // ── BINARY SEARCH TESTS ──────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("Binary Search - Found by ID")
    void testBinarySearchFound() {
        ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(users, "id", true);
        LibraryUser result = SortSearchUtil.binarySearchById(sorted, 3);
        assertNotNull(result, "User with ID 3 should be found");
        assertEquals("Balen Brown", result.getName());
    }

    @Test
    @Order(6)
    @DisplayName("Binary Search - Not Found Returns Null")
    void testBinarySearchNotFound() {
        ArrayList<LibraryUser> sorted = SortSearchUtil.mergeSort(users, "id", true);
        LibraryUser result = SortSearchUtil.binarySearchById(sorted, 99);
        assertNull(result, "Non-existent ID should return null");
    }

    @Test
    @Order(7)
    @DisplayName("Binary Search - Empty List Returns Null")
    void testBinarySearchEmptyList() {
        assertNull(SortSearchUtil.binarySearchById(new ArrayList<>(), 1),
                "Searching empty list should return null");
    }

    // ── LINEAR SEARCH TESTS ──────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("Linear Search - Partial Name Match")
    void testLinearSearchPartialMatch() {
        ArrayList<LibraryUser> results = SortSearchUtil.linearSearchByName(users, "son");
        assertEquals(1, results.size(), "Should find Bob Johnson");
        assertEquals("Bob Johnson", results.get(0).getName());
    }

    @Test
    @Order(9)
    @DisplayName("Linear Search - No Match Returns Empty List")
    void testLinearSearchNoMatch() {
        ArrayList<LibraryUser> results = SortSearchUtil.linearSearchByName(users, "XYZ");
        assertTrue(results.isEmpty(), "No match should return empty list");
    }

    // ── EVALUATE TESTS ───────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("StandardMember - More than 5 books gives +10 points")
    void testStandardMemberEvaluatePositive() {
        StandardMember member = new StandardMember(1, "Test", "test@mail.com", 8, 50, "fiction");
        member.evaluate();
        assertEquals(60, member.getPoints(), "Should gain 10 points");
    }

    @Test
    @Order(11)
    @DisplayName("StandardMember - 5 or fewer books loses -5 points")
    void testStandardMemberEvaluateNegative() {
        StandardMember member = new StandardMember(2, "Test", "test@mail.com", 3, 50, "fiction");
        member.evaluate();
        assertEquals(45, member.getPoints(), "Should lose 5 points");
    }

    @Test
    @Order(12)
    @DisplayName("PremiumMember - More than 5 books gives +20 points")
    void testPremiumMemberEvaluatePositive() {
        PremiumMember member = new PremiumMember(1, "VIP", "vip@mail.com", 7, 100, "fiction");
        member.evaluate();
        assertEquals(120, member.getPoints(), "Should gain 20 points");
    }
}
