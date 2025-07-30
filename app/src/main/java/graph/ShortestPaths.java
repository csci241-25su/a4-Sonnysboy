package graph;

import com.google.common.collect.Lists;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 * Sample usage:
 * Graph g = // create your graph
 * ShortestPaths sp = new ShortestPaths();
 * Node a = g.getNode("A");
 * sp.compute(a);
 * Node b = g.getNode("B");
 * LinkedList<Node> abPath = sp.getShortestPath(b);
 * double abPathLength = sp.getShortestPathLength(b);
 */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest
    // paths computation:
    private HashMap<Node, PathData> paths;


    /**
     * Compute the shortest path to all nodes from origin using Dijkstra's
     * algorithm. Fill in the paths field, which associates each Node with its
     * PathData record, storing total distance from the source, and the
     * backpointer to the previous node on the shortest path.
     * Precondition: origin is a node in the Graph.
     */
    public void compute(Node origin) {
        paths = new HashMap<>();
        // djikstra time
        Set<Node> s = new HashSet<>();

//        Heap library jar has a different version file header, so I can't use it, it's close enough to a PQ anyway
        Queue<Node> frontier = new PriorityQueue<>(Comparator.comparing(t -> paths.get(t).distance));
        /**
         *  v.d = 0;
         *   v.bp = null;
         */
        paths.put(origin, new PathData(0, null));
        frontier.add(origin);
        while (!frontier.isEmpty()) {

            Node f = frontier.poll();
            PathData fData = paths.get(f);
            if (s.contains(f)) {
                continue;
            }
            s.add(f);
            for (Map.Entry<Node, Double> w : f.getNeighbors().entrySet()) {
                PathData pathData;
                Node k = w.getKey();
                double weight = w.getValue(); // weight(f,w)
                double newDistance = fData.distance + weight;
                if ((pathData = paths.get(k)) == null) {
                    pathData = new PathData(newDistance, f);
                    paths.put(k, pathData);
                    frontier.add(k);
                } else if (fData.distance + weight < pathData.distance) {
                    pathData.distance = newDistance;
                    pathData.previous = f;
                    frontier.add(k);
                }
            }
        }
        // shortest-path data for each Node reachable from origin.

    }

    /**
     * Returns the length of the shortest path from the origin to destination.
     * If no path exists, return Double.POSITIVE_INFINITY.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called.
     */
    public double shortestPathLength(Node destination) {
        PathData data = paths.get(destination);
        if (data == null) {
            return Double.POSITIVE_INFINITY;
        }
        return data.distance;
    }

    /**
     * Returns a LinkedList of the nodes along the shortest path from origin
     * to destination. This path includes the origin and destination. If origin
     * and destination are the same node, it is included only once.
     * If no path to it exists, return null.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called.
     */
    public LinkedList<Node> shortestPath(Node destination) {
        Stack<Node> s = new Stack<>();
        Node carry = destination;
        if (!(paths.containsKey(destination))) return null;
        while (carry != null) {
            s.push(carry);
            carry = paths.get(carry).previous;
        }
        Collections.reverse(s);
        return s.isEmpty() ? null : Lists.newLinkedList(s);
    }


    /**
     * Inner class representing data used by Dijkstra's algorithm in the
     * process of computing shortest paths from a given source node.
     */
    static class PathData { // no record? *laughs in java 14+*
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /**
         * constructor: initialize distance and previous node
         */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }
    }


    /**
     * Static helper method to open and parse a file containing graph
     * information. Can parse either a basic file or a DB1B CSV file with
     * flight data. See GraphParser, BasicParser, and DB1BParser for more.
     */
    protected static Graph parseGraph(String fileType, String fileName) throws
            FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db1b")) {
            parser = new DB1BParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }

    public static void main(String[] args) {
        // read command line args
        String fileType = args[0];
        String fileName = args[1];
        String origCode = args[2];

        String destCode = null;
        if (args.length == 4) {
            destCode = args[3];
        }

        // parse a graph with the given type and filename
        Graph graph;
        try {
            graph = parseGraph(fileType, fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file " + fileName);
            return;
        }
        graph.report();


        // TODO 4: create a ShortestPaths object, use it to compute shortest
        ShortestPaths pathData = new ShortestPaths();
        // paths data from the origin node given by origCode.
        pathData.compute(graph.getNode(origCode));

        // TODO 5:
        // If destCode was not given, print each reachable node followed by the
        // length of the shortest path to it from the origin.
        if (null == destCode) {
            pathData.paths.keySet().forEach(k -> System.out.println(k.getId() + " : " + pathData.shortestPathLength(k)));
        } else {
            System.out.println(
                    Stream.of(Arrays.stream(pathData.shortestPath(graph.getNode(destCode))
                                            .stream().map(Node::getId)
                                            .collect(Collectors.joining("有."))
                                            .split("有"))
                                    .map(k -> k.replace(".", " "))
                                    .map(System.out::printf)
                                    .collect(Collectors.toList())
                                    .stream(), Arrays.stream(new Object[]{System.out.printf(" ")}))
                            .flatMap(Function.identity())
                            .count() - 1);
        }

        // TODO 6:
        // If destCode was given, print the nodes in the path from
        // origCode to destCode, followed by the total path length
        // If no path exists, print a message saying so.
    }
}
