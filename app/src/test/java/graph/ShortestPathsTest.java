package graph;

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;

import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URL;
import java.io.FileNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortestPathsTest {

    /* Performs the necessary gradle-related incantation to get the
       filename of a graph text file in the src/test/resources directory at
       test time.*/
    private String getGraphResource(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource.getPath();
    }

    /* Returns the Graph loaded from the file with filename fn located in
     * src/test/resources at test time. */
    private Graph loadBasicGraph(String fn) {
        Graph result = null;
        String filePath = getGraphResource(fn);
        try {
          result = ShortestPaths.parseGraph("basic", filePath);
        } catch (FileNotFoundException e) {
          fail("Could not find graph " + fn);
        }
        return result;
    }
    private Graph absolutelyLoadIt(String fn) {
        Graph result = null;
        String filePath = fn;
        try {
            result = ShortestPaths.parseGraph("basic", filePath);
        } catch (FileNotFoundException e) {
            fail("Could not find graph " + fn);
        }
        return result;
    }

    /** Dummy test case demonstrating syntax to create a graph from scratch.
     * Write your own tests below. */
    @Test
    public void test00Nothing() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        g.addEdge(a, b, 1);

        // sample assertion statements:
        assertTrue(true);
        assertEquals(2+2, 4);
    }

    /** Minimal test case to check the path from A to B in Simple0.txt */
    @Test
    public void test01Simple0() {
        Graph g = absolutelyLoadIt("C:\\Users\\melis\\Desktop\\coursework\\western courses\\year one\\a4-Sonnysboy\\app\\src\\test\\resources\\Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node b = g.getNode("B");
        LinkedList<Node> abPath = sp.shortestPath(b);
        assertEquals(abPath.size(), 2);
        assertEquals(abPath.getFirst(), a);
        assertEquals(abPath.getLast(),  b);
        assertEquals(sp.shortestPathLength(b), 1.0, 1e-6);
    }
    @Test
    public void testShortestPathLength_OriginNode() {
        ShortestPaths shortestPaths = new ShortestPaths();
        Graph graph = createSimpleGraph();
        Node origin = graph.getNode("A");
        shortestPaths.compute(origin);
        assertEquals(0.0, shortestPaths.shortestPathLength(origin), 0.001);
    }
    @Test
    public void testAddNeighbor() {
        Node node1 = new Node("N1");
        Node node2 = new Node("N2");
        node1.addNeighbor(node2, 5.0);
        assertEquals(1, node1.getNeighbors().size());
        assertTrue(node1.getNeighbors().containsKey(node2));
        assertEquals(5.0, node1.getNeighbors().get(node2), 0.001);
    }

    @Test
    public void testUpdateNeighborWeight() {
        Node node1 = new Node("N1");
        Node node2 = new Node("N2");
        node1.addNeighbor(node2, 5.0);
        node1.addNeighbor(node2, 7.0); // Update weight
        assertEquals(1, node1.getNeighbors().size());
        assertEquals(7.0, node1.getNeighbors().get(node2), 0.001);
    }
    @Test
    public void testCompute_NoEdgesGraph() {
        ShortestPaths shortestPaths = new ShortestPaths();
        Graph graph = new Graph();
        Node a = graph.getNode("A");
        Node b = graph.getNode("B");
        shortestPaths.compute(a);

        assertEquals(0.0, shortestPaths.shortestPathLength(a), 0.001);
        assertEquals(Double.POSITIVE_INFINITY, shortestPaths.shortestPathLength(b), 0.001);
        assertEquals(Collections.singletonList(a), shortestPaths.shortestPath(a));
        assertNull(shortestPaths.shortestPath(b));
    }


    private Graph createDisconnectedGraph() {
        Graph graph = new Graph();
        Node a = graph.getNode("A");
        Node b = graph.getNode("B");
        Node c = graph.getNode("C");
        Node x = graph.getNode("X"); // Disconnected node
        Node y = graph.getNode("Y"); // Disconnected node

        graph.addEdge(a, b, 1);
        graph.addEdge(b, c, 1);
        graph.addEdge(x, y, 1); // Disconnected component

        return graph;
    }

    @Test
    public void testCompute_DisconnectedGraph() {
        Graph g = createDisconnectedGraph();
        Node o = g.getNode("A");
        ShortestPaths shortestPaths = new ShortestPaths();
        shortestPaths.compute(o);

        Node b = g.getNode("B");
        Node c = g.getNode("C");
        assertEquals(1.0, shortestPaths.shortestPathLength(b), 0.001);
        assertEquals(2.0, shortestPaths.shortestPathLength(c), 0.001);

        Node x = g.getNode("X");
        Node y = g.getNode("Y");
        assertEquals(Double.POSITIVE_INFINITY, shortestPaths.shortestPathLength(x), 0.11);
        assertEquals(Double.POSITIVE_INFINITY, shortestPaths.shortestPathLength(y), 0.11);
        assertNull(shortestPaths.shortestPath(x));
        assertNull(shortestPaths.shortestPath(y));
    }
    private Graph createSimpleGraph() {
        Graph graph = new Graph();
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        graph.addEdge(a, b, 10);
        graph.addEdge(b, c, 5);
        graph.addEdge(a, c, 100);
        graph.addEdge(c, d, 2);
        graph.addEdge(b, d, 20);

        return graph;
    }

    /* Pro tip: unless you include @Test on the line above your method header,
     * gradle test will not run it! This gets me every time. */
}
