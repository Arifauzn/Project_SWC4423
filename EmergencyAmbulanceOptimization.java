import java.util.*;

public class EmergencyAmbulanceOptimization {

    // Travel Cost Matrix (Adjacency Matrix)
    static int[][] costMatrix = {
            {0, 15, 25, 35},
            {15, 0, 30, 28},
            {25, 30, 0, 20},
            {35, 28, 20, 0}
        };

    // Location names
    static String[] locations = {
            "Hospital",
            "Emergency Location B",
            "Emergency Location C",
            "Emergency Location D"
        };

    // ============================================   
    // Greedy Route Optimization
    // ============================================
    public static String greedyEAROP(int[][] dist) {

        int n = dist.length;
        boolean[] visited = new boolean[n];

        int current = 0;       // Start at Hospital
        int totalCost = 0;

        StringBuilder path = new StringBuilder();

        visited[0] = true;
        path.append(locations[0]);

        // Visit nearest unvisited location
        for (int count = 1; count < n; count++) {

            int nearest = -1;
            int minCost = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {

                if (!visited[i] && dist[current][i] < minCost) {
                    minCost = dist[current][i];
                    nearest = i;
                }
            }

            visited[nearest] = true;
            totalCost += minCost;
            current = nearest;

            path.append(" -> ").append(locations[current]);
        }

        // Return to Hospital
        totalCost += dist[current][0];
        path.append(" -> ").append(locations[0]);

        return "Greedy Route: " + path +
        "\nTotal Travel Cost: " + totalCost;
    }

    // ============================================
    // Dynamic Programming Route Optimization
    // ============================================
    public static String dynamicProgrammingEAROP(int[][] dist) {

        int n = dist.length;

        // Number of possible visited sets
        int VISITED_ALL = (1 << n) - 1;

        int[][] memo = new int[n][1 << n];
        String[][] paths = new String[n][1 << n];

        // Initialize memo table
        for (int i = 0; i < n; i++) {
            Arrays.fill(memo[i], -1);
        }

        int minimumCost = dynamicProgrammingEAROPHelper(
                0,
                1,              // Hospital is already visited
                dist,
                memo,
                VISITED_ALL,
                paths
            );

        String route = paths[0][1];

        return "Dynamic Programming Route: " + route +
        "\nTotal Travel Cost: " + minimumCost;
    }

    // Dynamic Programming Helper Method
    private static int dynamicProgrammingEAROPHelper(
    int pos,
    int mask,
    int[][] dist,
    int[][] memo,
    int VISITED_ALL,
    String[][] paths) {

        // All locations visited
        if (mask == VISITED_ALL) {
            paths[pos][mask] = locations[pos] + " -> " + locations[0];
            return dist[pos][0];
        }

        // Already calculated
        if (memo[pos][mask] != -1) {
            return memo[pos][mask];
        }

        int n = dist.length;
        int minimumCost = Integer.MAX_VALUE;
        int bestNext = -1;

        // Try every unvisited location
        for (int next = 0; next < n; next++) {

            if ((mask & (1 << next)) == 0) {

                int newMask = mask | (1 << next);

                int cost = dist[pos][next]
                    + dynamicProgrammingEAROPHelper(
                        next,
                        newMask,
                        dist,
                        memo,
                        VISITED_ALL,
                        paths
                    );

                if (cost < minimumCost) {
                    minimumCost = cost;
                    bestNext = next;
                }
            }
        }

        memo[pos][mask] = minimumCost;

        // Store route
        if (bestNext != -1) {

            int newMask = mask | (1 << bestNext);

            String remainingPath =
                paths[bestNext][newMask];

            if (remainingPath == null) {

                dynamicProgrammingEAROPHelper(
                    bestNext,
                    newMask,
                    dist,
                    memo,
                    VISITED_ALL,
                    paths
                );

                remainingPath = paths[bestNext][newMask];
            }

            paths[pos][mask] =
            locations[pos] + " -> " + remainingPath;
        }

        return minimumCost;
    }

    // ============================================
    // Backtracking Route Optimization
    // ============================================
    public static String backtrackingEAROP(int[][] dist) {

        int n = dist.length;

        boolean[] visited = new boolean[n];
        visited[0] = true;

        StringBuilder path = new StringBuilder();
        path.append(locations[0]);

        int minimumCost = earopBacktracking(
                0,
                dist,
                visited,
                n,
                1,
                0,
                path
            );

        return "Backtracking Route: " + path +
        "\nTotal Travel Cost: " + minimumCost;
    }

    // Backtracking Helper Method
    private static int earopBacktracking(
    int pos,
    int[][] dist,
    boolean[] visited,
    int n,
    int count,
    int cost,
    StringBuilder path) {

        // All locations visited
        if (count == n) {

            int returnCost = dist[pos][0];

            path.append(" -> ").append(locations[0]);

            return cost + returnCost;
        }

        int minimumCost = Integer.MAX_VALUE;
        String bestPath = "";

        // Try every unvisited location
        for (int next = 0; next < n; next++) {

            if (!visited[next]) {

                int oldLength = path.length();

                visited[next] = true;

                path.append(" -> ")
                .append(locations[next]);

                int totalCost = earopBacktracking(
                        next,
                        dist,
                        visited,
                        n,
                        count + 1,
                        cost + dist[pos][next],
                        path
                    );

                // Save best route
                if (totalCost < minimumCost) {
                    minimumCost = totalCost;
                    bestPath = path.toString();
                }

                // Backtrack
                visited[next] = false;
                path.setLength(oldLength);
            }
        }

        path.setLength(0);
        path.append(bestPath);

        return minimumCost;
    }

    // ============================================
    // Divide and Conquer Route Optimization
    // ============================================
    public static String divideAndConquerEAROP(int[][] dist) {

        int n = dist.length;

        boolean[] visited = new boolean[n];
        visited[0] = true;

        StringBuilder path = new StringBuilder();
        path.append(locations[0]);

        int minimumCost = divideAndConquerHelper(
                0,
                visited,
                0,
                dist,
                n,
                path
            );

        return "Divide and Conquer Route: " + path +
        "\nTotal Travel Cost: " + minimumCost;
    }

    // Divide and Conquer Helper Method
    private static int divideAndConquerHelper(
    int pos,
    boolean[] visited,
    int currentCost,
    int[][] dist,
    int n,
    StringBuilder path) {

        // Base case:
        // All locations have been visited
        if (allVisited(visited)) {

            path.append(" -> ").append(locations[0]);

            return currentCost + dist[pos][0];
        }

        int minimumCost = Integer.MAX_VALUE;
        String bestPath = "";

        // Divide:
        // Try each possible unvisited location
        for (int next = 0; next < n; next++) {

            if (!visited[next]) {

                boolean[] newVisited =
                    Arrays.copyOf(visited, n);

                newVisited[next] = true;

                StringBuilder newPath =
                    new StringBuilder(path);

                newPath.append(" -> ")
                .append(locations[next]);

                // Conquer:
                int totalCost = divideAndConquerHelper(
                        next,
                        newVisited,
                        currentCost + dist[pos][next],
                        dist,
                        n,
                        newPath
                    );

                if (totalCost < minimumCost) {
                    minimumCost = totalCost;
                    bestPath = newPath.toString();
                }
            }
        }

        // Store best path
        path.setLength(0);
        path.append(bestPath);

        return minimumCost;
    }

    // Check whether all emergency locations have been visited
    private static boolean allVisited(boolean[] visited) {

        for (boolean value : visited) {

            if (!value) {
                return false;
            }
        }

        return true;
    }

    // ============================================
    // Insertion Sort
    // ============================================
    public static String insertionSort(int[] arr) {

        for (int i = 1; i < arr.length; i++) {

            int key = arr[i];
            int j = i - 1;

            // Move larger elements one position forward
            while (j >= 0 && arr[j] > key) {

                arr[j + 1] = arr[j];
                j--;
            }

            arr[j + 1] = key;
        }

        return Arrays.toString(arr);
    }

    // ============================================
    // Binary Search
    // ============================================
    public static String binarySearch(int[] arr, int target) {

        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {

            int mid = (left + right) / 2;

            if (arr[mid] == target) {
                return String.valueOf(mid);
            }

            if (arr[mid] < target) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }

        return "-1";
    }

    // ============================================
    // Min-Heap
    // ============================================
    static class MinHeap {

        private PriorityQueue<Integer> heap =
            new PriorityQueue<>();

        // Insert a value into the Min-Heap
        public void insert(int value) {

            heap.add(value);
        }

        // Remove and return the minimum value
        public int extractMin() {

            if (heap.isEmpty()) {
                throw new NoSuchElementException(
                    "Heap is empty"
                );
            }

            return heap.poll();
        }
    }

    // ============================================
    // Splay Tree
    // ============================================
    static class SplayTree {

        // Node for Splay Tree
        private class Node {

            int value;
            Node left;
            Node right;

            Node(int value) {
                this.value = value;
            }
        }

        private Node root;

        // Right rotation
        private Node rotateRight(Node x) {

            Node y = x.left;

            x.left = y.right;
            y.right = x;

            return y;
        }

        // Left rotation
        private Node rotateLeft(Node x) {

            Node y = x.right;

            x.right = y.left;
            y.left = x;

            return y;
        }

        // Splay operation
        private Node splay(Node root, int value) {

            if (root == null ||
            root.value == value) {

                return root;
            }

            // Value is in left subtree
            if (value < root.value) {

                if (root.left == null) {
                    return root;
                }

                // Zig-Zig
                if (value < root.left.value) {

                    root.left.left =
                    splay(root.left.left, value);

                    root = rotateRight(root);
                }

                // Zig-Zag
                else if (value > root.left.value) {

                    root.left.right =
                    splay(root.left.right, value);

                    if (root.left.right != null) {
                        root.left =
                        rotateLeft(root.left);
                    }
                }

                // Final rotation
                if (root.left == null) {
                    return root;
                }

                return rotateRight(root);
            }

            // Value is in right subtree
            else {

                if (root.right == null) {
                    return root;
                }

                // Zag-Zag
                if (value > root.right.value) {

                    root.right.right =
                    splay(root.right.right, value);

                    root = rotateLeft(root);
                }

                // Zag-Zig
                else if (value < root.right.value) {

                    root.right.left =
                    splay(root.right.left, value);

                    if (root.right.left != null) {
                        root.right =
                        rotateRight(root.right);
                    }
                }

                // Final rotation
                if (root.right == null) {
                    return root;
                }

                return rotateLeft(root);
            }
        }

        // Insert value
        public void insert(int value) {

            if (root == null) {

                root = new Node(value);
                return;
            }

            root = splay(root, value);

            // Value already exists
            if (root.value == value) {
                return;
            }

            Node newNode = new Node(value);

            if (value < root.value) {

                newNode.right = root;
                newNode.left = root.left;

                root.left = null;
            }
            else {

                newNode.left = root;
                newNode.right = root.right;

                root.right = null;
            }

            root = newNode;
        }

        // Search value
        public boolean search(int value) {

            if (root == null) {
                return false;
            }

            root = splay(root, value);

            return root.value == value;
        }
    }

    // ============================================
    // Driver Method
    // ============================================
    public static void main(String[] args) {

        // Route Optimization Algorithms
        System.out.println(
            greedyEAROP(costMatrix)
        );

        System.out.println();

        System.out.println(
            dynamicProgrammingEAROP(costMatrix)
        );

        System.out.println();

        System.out.println(
            backtrackingEAROP(costMatrix)
        );

        System.out.println();

        System.out.println(
            divideAndConquerEAROP(costMatrix)
        );

        // ============================================
        // Sorting and Searching
        // ============================================

        int[] arr = {8, 3, 5, 1, 9, 2};

        insertionSort(arr);

        System.out.println(
            "\nSorted Emergency Response Times: "
            + Arrays.toString(arr)
        );

        System.out.println(
            "Binary Search (Response Time 5 found at index): "
            + binarySearch(arr, 5)
        );

        // ============================================
        // Min-Heap Test
        // ============================================

        MinHeap heap = new MinHeap();

        heap.insert(10);
        heap.insert(3);
        heap.insert(15);

        System.out.println(
            "Min-Heap Extract Minimum Priority Value: "
            + heap.extractMin()
        );

        // ============================================
        // Splay Tree Test
        // ============================================

        SplayTree tree = new SplayTree();

        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        System.out.println(
            "Splay Tree Search (Emergency Case 10 found): "
            + tree.search(10)
        );
    }
}