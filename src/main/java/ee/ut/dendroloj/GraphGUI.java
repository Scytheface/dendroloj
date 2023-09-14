package ee.ut.dendroloj;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.MouseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        View view = viewer.addDefaultView(false);
        Component viewComponent = (Component) view;

        // GraphStream DefaultMouseManager but with event handling enabled only for left click to prevent interference with panning.
        MouseManager restrictedDefaultMouseManager = new DefaultMouseManager() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    super.mousePressed(event);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    super.mouseReleased(event);
                }
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    if (curElement != null) {
                        elementMoving(curElement, event);
                        viewComponent.repaint(); // Doing this makes moving elements appear much smoother.
                    } else {
                        view.selectionGrowsAt(event.getX(), event.getY());
                    }
                }
            }
        };
        view.setMouseManager(restrictedDefaultMouseManager);

        MouseAdapter zoomAndPanAdapter = new MouseAdapter() {
            private static final double ZOOM_FACTOR = 1.25;

            private final Point lastPxCursor = new Point();

            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                event.consume();

                // Zoom towards cursor with scroll wheel. Adapted from https://stackoverflow.com/a/52929241.
                int i = event.getWheelRotation();
                double factor = Math.pow(ZOOM_FACTOR, i);
                Camera cam = view.getCamera();
                double zoom = cam.getViewPercent() * factor;
                Point2 pxCenter = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
                Point3 guCursor = cam.transformPxToGu(event.getX(), event.getY());
                double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu / factor;
                double x = guCursor.x + (pxCenter.x - event.getX()) / newRatioPx2Gu;
                double y = guCursor.y - (pxCenter.y - event.getY()) / newRatioPx2Gu;
                cam.setViewCenter(x, y, 0);
                cam.setViewPercent(zoom);

                viewComponent.repaint();
            }

            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    event.consume();

                    lastPxCursor.setLocation(event.getPoint());
                }
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    event.consume();

                    Point newPxCursor = event.getPoint();
                    Camera cam = view.getCamera();
                    Point2 viewCenter = cam.getViewCenter();
                    double ratioPx2Gu = cam.getMetrics().ratioPx2Gu;
                    double x = viewCenter.x + (lastPxCursor.x - newPxCursor.x) / ratioPx2Gu;
                    double y = viewCenter.y - (lastPxCursor.y - newPxCursor.y) / ratioPx2Gu;
                    cam.setViewCenter(x, y, 0);
                    lastPxCursor.setLocation(newPxCursor);

                    viewComponent.repaint();
                }
            }
        };
        viewComponent.addMouseListener(zoomAndPanAdapter);
        viewComponent.addMouseMotionListener(zoomAndPanAdapter);
        viewComponent.addMouseWheelListener(zoomAndPanAdapter);

        JFrame root = new JFrame();
        root.setTitle("dendroloj");
        root.setLayout(new BorderLayout());
        root.add(viewComponent, BorderLayout.CENTER);
        root.setSize(800, 600);
        root.setLocationRelativeTo(null);
        root.addWindowListener((WindowListener) viewComponent);
        root.addComponentListener((ComponentListener) viewComponent);
        root.setVisible(true);
    }
}