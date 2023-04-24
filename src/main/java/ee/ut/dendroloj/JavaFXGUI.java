package ee.ut.dendroloj;

import javafx.application.Platform;
import org.graphstream.graph.Graph;

import org.graphstream.graph.implementations.SingleGraph;

import java.util.concurrent.CountDownLatch;

class JavaFXGUI {
    public static void init() {
        System.setProperty("org.graphstream.ui", "javafx");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);


        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Graph graph = new SingleGraph("DendroloJ");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", "url('graphStyle.css')");

        SimpleTreeLayout.setGraph(graph);





        //v.disableAutoLayout();
    }
}