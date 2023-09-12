package ee.ut.dendroloj;

import org.graphstream.graph.Graph;

import org.graphstream.graph.implementations.SingleGraph;

class GraphGUI {
    public static void init(double uiScale) {
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("DendroloJ");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", String.format("""                 
                edge {
                    size: %fpx;
                    text-size: %f;
                    text-alignment: center;
                    text-background-mode: plain;
                    text-background-color: rgba(255, 255, 255, 180);
                    text-padding: %f;
                    text-offset: 5, 0;
                }
                                
                edge .returned {
                    fill-color: gray;
                }
                                
                node {
                    size: %fpx;
                    text-size: %f;
                    text-alignment: at-right;
                    text-background-mode: plain;
                    text-background-color: rgba(255, 255, 255, 180);
                    text-padding: %f;
                    text-offset: 5, 0;
                }
                                
                node .error {
                    fill-color: #fa4c29;
                }
                """, Math.sqrt(uiScale), uiScale * 12, uiScale + 1, Math.sqrt(uiScale) * 10, uiScale * 12, uiScale + 1));

        SimpleTreeLayout.setGraph(graph);
        graph.display(false);
    }
}