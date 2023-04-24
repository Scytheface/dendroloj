package ee.ut.dendroloj;

public class Dendrologist {
    private static boolean awake = false;

    public static void wakeUp() {
        wakeUp(true);
    }

    public static void wakeUp(boolean visualize){
        init(visualize);
    }

    private static void init(boolean visualize){
        if (awake) return;
        initTracing();
        if (visualize)
            initGraphics();
        awake = true;
    }

    private static void initTracing(){
        DebuggerTracer.init();
        AgentTracer.init();
    }

    private static void initGraphics(){
        JavaFXGUI.init();
    }
}

