package ee.ut.dendroloj;

import org.graphstream.graph.Graph;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.view.util.ShortcutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

class GraphGUI {

    static {
        // NB: This needs to be set before any Swing objects are created, otherwise OS UI scaling will break the GUI
        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("org.graphstream.ui", "swing");
    }

    /**
     * Initializes global Swing and GraphStream properties. This will be called automatically when the GUI is initialized.
     * To avoid issues this should also be called manually before any code that might create Swing objects is executed.
     */
    public static void initProperties() {
        // The actual initialization happens inside the static block above
        // Calling this method ensures that the static block has been executed
    }

    public static boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    public static void initGenericGUI(double uiScale, Graph graph, Layout layout) {
        // TODO: Fix edge labels overlapping if there are multiple edges.
        // TODO: Fix arrows appearing outside the edge if the edge is straight and fully inside the node.

        graph.setAttribute("ui.stylesheet", String.format(Locale.ROOT,
                "edge {" +
                        " size: %fpx;" +
                        " arrow-size: %fpx, %fpx;" +
                        " fill-mode: dyn-plain;" +
                        " text-size: %f; text-alignment: center;" +
                        " text-background-mode: plain; text-background-color: rgba(255, 255, 255, 180);" +
                        " text-padding: %f;" +
                        "}" +
                        "edge.error {" +
                        " fill-color: rgb(255, 0, 0);" +
                        "}" +
                        "edge.arrowonly {" +
                        " text-mode: hidden;" +
                        " size: 0px;" +
                        "}" +
                        "node {" +
                        " size: %fpx;" +
                        " fill-mode: dyn-plain;" +
                        " fill-color: rgb(225, 225, 225);" +
                        " text-size: %f; text-alignment: center;" +
                        " text-offset: 0px, -%fpx;" +
                        "}" +
                        "node:selected {" +
                        " fill-mode: plain;" +
                        " fill-color: #0096ff;" +
                        "}",
                Math.max(0.8, Math.sqrt(uiScale)),
                (uiScale > 1 ? Math.sqrt(uiScale) : uiScale) * 8, (uiScale > 1 ? Math.sqrt(uiScale) : uiScale) * 4,
                uiScale * 14, uiScale + 1,
                uiScale * 28, uiScale * 14, uiScale * 3));

        initGUI(graph, layout, null);
    }

    public static void initCallTreeGUI(double uiScale) {
        CallTreeLayout.graph.setAttribute("ui.stylesheet", String.format(Locale.ROOT,
                "edge {" +
                        " size: %fpx;" +
                        " text-size: %f; text-alignment: center;" +
                        " text-background-mode: plain; text-background-color: rgba(255, 255, 255, 180);" +
                        " text-padding: %f; text-offset: 5, 0;" +
                        "}" +
                        "edge.returned {" +
                        " fill-color: gray;" +
                        "}" +
                        "node {" +
                        " size: %fpx;" +
                        " fill-mode: dyn-plain;" +
                        " text-size: %f; text-alignment: at-right;" +
                        " text-background-mode: plain; text-background-color: rgba(255, 255, 255, 180);" +
                        " text-padding: %f; text-offset: 5, 0;" +
                        "}" +
                        "node.error {" +
                        " fill-mode: plain;" +
                        " fill-color: #fa4c29;" +
                        "}" +
                        "node:selected {" +
                        " fill-mode: plain;" +
                        " fill-color: #0096ff;" +
                        "}",
                Math.max(0.8, Math.sqrt(uiScale)), uiScale * 12, uiScale + 1, Math.sqrt(uiScale) * 10, uiScale * 12, uiScale + 1));

        initGUI(CallTreeLayout.graph, null, CallTreeLayout.stepSlider);
    }

    private static void initGUI(Graph graph, Layout layout, JComponent toolbar) {
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        View view = viewer.addDefaultView(false);
        if (layout != null) {
            // Adding an auto-layout makes dragging nodes feel really choppy and introduces strange rubber banding.
            // TODO: Figure out why and fix this. (A good starting point might be seeing if official examples have this problem.)
            viewer.enableAutoLayout(layout);
        }
        Component viewComponent = (Component) view;

        // GraphStream DefaultMouseManager but with event handling enabled only for left click, to prevent interference with panning.
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
                        // TODO: This enables selecting nodes, but there is nothing that can be done with these selected nodes
                        view.selectionGrowsAt(event.getX(), event.getY());
                    }
                }
            }

            @Override
            protected void mouseButtonReleaseOffElement(GraphicElement element, MouseEvent event) {
                if (!element.hasAttribute("layout._fixed")) {
                    view.freezeElement(element, false);
                }
                if (event.getButton() != 3) {
                    element.removeAttribute("ui.clicked");
                }
            }
        };
        view.setMouseManager(restrictedDefaultMouseManager);

        // Replace shortcut manager with one that does nothing, to disable built-in shortcuts.
        // Supported shortcuts are added directly to root pane as key bindings.
        ShortcutManager noopShortcutManager = new ShortcutManager() {
            @Override
            public void init(GraphicGraph graph, View view) {
            }

            @Override
            public void release() {
            }
        };
        view.setShortcutManager(noopShortcutManager);

        MouseAdapter zoomAndPanAdapter = new MouseAdapter() {
            private static final double ZOOM_FACTOR = 1.25;

            private final Point lastPxCursor = new Point();

            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                event.consume();

                // Zoom towards cursor with scroll wheel. Adapted from https://stackoverflow.com/a/52929241.
                // Note: This is smooth on touchpad when scrolling with two fingers, but not when pinching.
                // TODO: Figure out why. If possible make scrolling by pinching smooth as well.
                double factor = Math.pow(ZOOM_FACTOR, event.getPreciseWheelRotation());
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
        // URL icon = GraphGUI.class.getResource("ee/ut/dendroloj/icon.png");
        // if (icon == null) throw new RuntimeException("Icon file not found");
        // root.setIconImage(Toolkit.getDefaultToolkit().getImage(icon));
        root.setTitle("dendroloj");
        root.setSize(800, 600);
        root.setLocationRelativeTo(null);

        root.setLayout(new BorderLayout());
        root.add(viewComponent, BorderLayout.CENTER);
        if (toolbar != null) {
            root.add(toolbar, BorderLayout.SOUTH);
        }
        root.addWindowListener((WindowListener) viewComponent);
        root.addComponentListener((ComponentListener) viewComponent);

        // Add shortcuts
        InputMap inputMap = root.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), "resetZoom");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK), "resetZoom");
        root.getRootPane().getActionMap().put("resetZoom", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                view.getCamera().resetView();
            }
        });

        root.setVisible(true);
    }

}
