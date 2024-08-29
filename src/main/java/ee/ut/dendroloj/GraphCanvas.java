package ee.ut.dendroloj;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A helper class for drawing a graph.
 * Can be passed to {@link Dendrologist#drawGraph} to render the drawn graph on screen.
 *
 * @param <V> type of graph vertices
 */
public final class GraphCanvas<V> {

    private final Map<IdPair, Edge<V>> collapsibleEdges = new HashMap<>();
    private final Set<String> drawnVertices = new HashSet<>();
    final List<Vertex<V>> vertices = new ArrayList<>();
    final List<Edge<V>> edges = new ArrayList<>();

    // TODO: Add overload that allows drawing a vertex with the vertex itself as a default label?
    // Note that the default label being the vertex itself converted to a string
    // might be non-obvious for reference types (especially ones that don't override toString).

    // public void drawVertex(V vertex) {
    //    if (vertex == null) throw new NullPointerException("Vertex must not be null");
    //    drawVertex(vertex, vertex.toString(), null);
    // }

    /**
     * Draws a vertex with the given label.
     */
    public void drawVertex(V vertex, Object label) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label == null ? null : label.toString(), null));
    }

    /**
     * Draws a vertex with the given label.
     */
    public void drawVertex(V vertex, Object label, Color color) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label == null ? null : label.toString(), color));
    }

    // public void drawFixedVertex(V vertex, double x, double y) {
    //    if (vertex == null) throw new NullPointerException("Vertex must not be null");
    //    drawVertex(vertex, vertex.toString(), null);
    // }

    /**
     * Draws a vertex with the given label at the given coordinates.
     */
    public void drawFixedVertex(V vertex, double x, double y, Object label) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label == null ? null : label.toString(), null, x, y));
    }

    /**
     * Draws a vertex with the given label at the given coordinates.
     */
    public void drawFixedVertex(V vertex, double x, double y, Object label, Color color) {
        if (vertex == null) throw new NullPointerException("Vertex must not be null");
        addVertex(new Vertex<>(vertex, label == null ? null : label.toString(), color, x, y));
    }

    /**
     * @deprecated use {@link #drawFixedVertex(V, double, double, Object)} instead
     */
    @Deprecated(forRemoval = true)
    public void drawFixedVertex(V vertex, Object label, double x, double y) {
        drawFixedVertex(vertex, x, y, label);
    }

    /**
     * @deprecated use {@link #drawFixedVertex(V, double, double, Object, Color)} instead
     */
    @Deprecated(forRemoval = true)
    public void drawFixedVertex(V vertex, Object label, double x, double y, Color color) {
        drawFixedVertex(vertex, x, y, label, color);
    }

    private void addVertex(Vertex<V> vertex) {
        String id = IdHelper.getNodeId(vertex.vertex);
        if (drawnVertices.contains(id)) {
            throw new IllegalArgumentException("Vertex " + vertex.vertex + " (" + vertex.label + ") has already been drawn");
        }
        drawnVertices.add(id);
        vertices.add(vertex);
    }

    /**
     * Draws an undirected edge from vertex v1 to vertex v2.
     */
    public void drawEdge(V v1, V v2) {
        drawEdge(v1, v2, null, null);
    }

    /**
     * Draws an undirected edge from vertex v1 to vertex v2.
     */
    public void drawEdge(V v1, V v2, Object label) {
        drawEdge(v1, v2, label, null);
    }

    /**
     * Draws an undirected edge from vertex v1 to vertex v2.
     */
    public void drawEdge(V v1, V v2, Object label, Color color) {
        if (v1 == null || v2 == null) throw new NullPointerException("Target vertices must not be null");
        edges.add(new Edge<>(false, v1, v2, label == null ? null : label.toString(), color));
    }

    /**
     * Draws a directed edge from vertex v1 to vertex v2.
     */
    public void drawDirectedEdge(V v1, V v2) {
        drawDirectedEdge(v1, v2, null, null);
    }

    /**
     * Draws a directed edge from vertex v1 to vertex v2.
     */
    public void drawDirectedEdge(V v1, V v2, Object label) {
        drawDirectedEdge(v1, v2, label, null);
    }

    /**
     * Draws a directed edge from vertex v1 to vertex v2.
     */
    public void drawDirectedEdge(V v1, V v2, Object label, Color color) {
        if (v1 == null || v2 == null) throw new NullPointerException("Target vertices must not be null");
        String v1Id = IdHelper.getNodeId(v1);
        String v2Id = IdHelper.getNodeId(v2);
        Edge<V> edge = new Edge<>(true, v1, v2, label == null ? null : label.toString(), color);
        Edge<V> collapsibleEdge = collapsibleEdges.get(new IdPair(v2Id, v1Id));
        if (collapsibleEdge != null && !collapsibleEdge.collapsed && Objects.equals(collapsibleEdge.label, edge.label)) {
            edge.collapsed = true;
            edge.arrowOnly = true;
            collapsibleEdge.collapsed = true;
        }
        edges.add(edge);
        collapsibleEdges.putIfAbsent(new IdPair(v1Id, v2Id), edge);
    }

    private static class IdPair {
        public final String v1;
        public final String v2;

        public IdPair(String v1, String v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IdPair idPair = (IdPair) o;
            return Objects.equals(v1, idPair.v1) && Objects.equals(v2, idPair.v2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(v1, v2);
        }
    }

    static final class Vertex<T> {
        public final T vertex;
        public final String label;
        public final Color color;
        public final boolean fixed;
        public final double x;
        public final double y;

        private Vertex(T vertex, String label, Color color) {
            this.vertex = vertex;
            this.label = label;
            this.color = color;
            this.fixed = false;
            this.x = 0.0;
            this.y = 0.0;
        }

        private Vertex(T vertex, String label, Color color, double x, double y) {
            this.vertex = vertex;
            this.label = label;
            this.color = color;
            this.fixed = true;
            this.x = x;
            this.y = y;
        }
    }

    static final class Edge<T> {
        public final boolean directed;
        public boolean collapsed = false;
        public boolean arrowOnly = false;
        public final T v1;
        public final T v2;
        public final String label;
        public final Color color;

        private Edge(boolean directed, T v1, T v2, String label, Color color) {
            this.directed = directed;
            this.v1 = v1;
            this.v2 = v2;
            this.label = label;
            this.color = color;
        }
    }

}
