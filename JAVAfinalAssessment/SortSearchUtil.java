import java.util.ArrayList;

/**
 * Utility class providing sorting and searching algorithms for LibraryUser records.
 *
 * Sorting:  Merge Sort  — O(n log n) time, O(n) space. Chosen because it is
 *           stable (preserves original order of equal elements) and guarantees
 *           O(n log n) in all cases, unlike QuickSort which degrades to O(n²)
 *           in the worst case.
 *
 * Searching: Binary Search — O(log n) time on a sorted list. Chosen for fast
 *            lookup by ID or name after sorting. Linear Search O(n) is also
 *            provided as a fallback for unsorted keyword searches.
 *
 * @author Bijaya Sharma
 * @version 2.0
 */
public class SortSearchUtil {

    // ─────────────────────────────────────────────────────────────────────────
    // MERGE SORT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sorts a list of LibraryUser objects using Merge Sort by the given field.
     *
     * Time Complexity:  O(n log n) — best, average, and worst case.
     * Space Complexity: O(n) — requires auxiliary arrays during merge.
     *
     * @param users     the list of users to sort
     * @param sortField the field to sort by: "name", "id", "booksRead", "points"
     * @param ascending true for ascending order, false for descending
     * @return a new sorted list (original list is not modified)
     */
    public static ArrayList<LibraryUser> mergeSort(ArrayList<LibraryUser> users,
                                                    String sortField,
                                                    boolean ascending) {
        if (users == null || users.size() <= 1) {
            return new ArrayList<>(users != null ? users : new ArrayList<>());
        }

        ArrayList<LibraryUser> copy = new ArrayList<>(users);
        mergeSortRecursive(copy, sortField, ascending);
        return copy;
    }

    /**
     * Recursive merge sort helper — splits list in half, sorts each half, then merges.
     */
    private static void mergeSortRecursive(ArrayList<LibraryUser> list,
                                            String sortField,
                                            boolean ascending) {
        int size = list.size();
        if (size <= 1) return;

        int mid = size / 2;

        // Split into left and right halves
        ArrayList<LibraryUser> left  = new ArrayList<>(list.subList(0, mid));
        ArrayList<LibraryUser> right = new ArrayList<>(list.subList(mid, size));

        // Recursively sort each half
        mergeSortRecursive(left,  sortField, ascending);
        mergeSortRecursive(right, sortField, ascending);

        // Merge sorted halves back into list
        merge(list, left, right, sortField, ascending);
    }

    /**
     * Merges two sorted sublists into the original list in sorted order.
     */
    private static void merge(ArrayList<LibraryUser> list,
                               ArrayList<LibraryUser> left,
                               ArrayList<LibraryUser> right,
                               String sortField,
                               boolean ascending) {
        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            int cmp = compare(left.get(i), right.get(j), sortField);
            if (ascending ? cmp <= 0 : cmp >= 0) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }

        while (i < left.size())  list.set(k++, left.get(i++));
        while (j < right.size()) list.set(k++, right.get(j++));
    }

    /**
     * Compares two users by the given field.
     * Returns negative if a < b, 0 if equal, positive if a > b.
     */
    private static int compare(LibraryUser a, LibraryUser b, String sortField) {
        return switch (sortField.toLowerCase()) {
            case "name"      -> a.getName().compareToIgnoreCase(b.getName());
            case "id"        -> Integer.compare(a.getId(), b.getId());
            case "booksread" -> Integer.compare(a.getBooksRead(), b.getBooksRead());
            case "points"    -> Integer.compare(a.getPoints(), b.getPoints());
            default          -> Integer.compare(a.getId(), b.getId());
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BINARY SEARCH
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Searches for a user by ID using Binary Search on a list sorted by ID.
     *
     * Time Complexity:  O(log n) — halves the search space each step.
     * Space Complexity: O(1) — iterative, no extra space needed.
     *
     * PRECONDITION: the list must be sorted by ID in ascending order.
     *
     * @param sortedUsers list of users sorted by ID ascending
     * @param targetId    the ID to search for
     * @return the LibraryUser if found, null otherwise
     */
    public static LibraryUser binarySearchById(ArrayList<LibraryUser> sortedUsers, int targetId) {
        if (sortedUsers == null || sortedUsers.isEmpty()) return null;

        int low  = 0;
        int high = sortedUsers.size() - 1;

        while (low <= high) {
            int mid    = low + (high - low) / 2; // avoids integer overflow
            int midId  = sortedUsers.get(mid).getId();

            if (midId == targetId) {
                return sortedUsers.get(mid);      // found
            } else if (midId < targetId) {
                low = mid + 1;                    // search right half
            } else {
                high = mid - 1;                   // search left half
            }
        }

        return null; // not found
    }

    /**
     * Searches for a user by name using Binary Search on a list sorted by name.
     *
     * Time Complexity:  O(log n)
     * Space Complexity: O(1)
     *
     * PRECONDITION: the list must be sorted by name in ascending order (case-insensitive).
     *
     * @param sortedUsers list sorted by name ascending
     * @param targetName  exact name to search for (case-insensitive)
     * @return the LibraryUser if found, null otherwise
     */
    public static LibraryUser binarySearchByName(ArrayList<LibraryUser> sortedUsers, String targetName) {
        if (sortedUsers == null || sortedUsers.isEmpty() || targetName == null) return null;

        int low  = 0;
        int high = sortedUsers.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = sortedUsers.get(mid).getName().compareToIgnoreCase(targetName);

            if (cmp == 0) {
                return sortedUsers.get(mid);
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LINEAR SEARCH
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Searches for users whose name contains the given keyword (case-insensitive).
     * Used as a fallback when the list is not sorted or for partial name matching.
     *
     * Time Complexity:  O(n) — must check every element in the worst case.
     * Space Complexity: O(k) where k is the number of matching results.
     *
     * @param users   the list of users to search
     * @param keyword the keyword to look for within user names
     * @return a list of all matching users (may be empty)
     */
    public static ArrayList<LibraryUser> linearSearchByName(ArrayList<LibraryUser> users, String keyword) {
        ArrayList<LibraryUser> results = new ArrayList<>();
        if (users == null || keyword == null || keyword.isBlank()) return results;

        String lower = keyword.toLowerCase();
        for (LibraryUser user : users) {
            if (user.getName().toLowerCase().contains(lower)) {
                results.add(user);
            }
        }
        return results;
    }

    /**
     * Searches for a user by exact ID using Linear Search.
     * Used when the list is not sorted.
     *
     * Time Complexity:  O(n)
     * Space Complexity: O(1)
     *
     * @param users    the list of users
     * @param targetId the ID to find
     * @return the matching LibraryUser, or null if not found
     */
    public static LibraryUser linearSearchById(ArrayList<LibraryUser> users, int targetId) {
        if (users == null) return null;
        for (LibraryUser user : users) {
            if (user.getId() == targetId) return user;
        }
        return null;
    }
}
