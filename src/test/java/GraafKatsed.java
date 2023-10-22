import ee.ut.dendroloj.Dendrologist;
import ee.ut.dendroloj.GraphCanvas;

import java.util.ArrayList;
import java.util.List;

public class GraafKatsed {

    public static void main(String[] args) {
        String[] nimed = new String[]{
                "A",
                "B",
                "C",
                "D",
                "E"
        };
        int[][] M = {
                {0, 11, 1, 12, -1},
                {4, 0, 2, -1, 6},
                {-1, 2, 0, 3, -1},
                {12, -1, -1, 0, -1},
                {-1, -1, 5, 4, 0}
        };
        Dendrologist.drawGraph(M, nimed);
        Dendrologist.drawGraph(M, null);

        List<Tipp> tipud = new ArrayList<>();
        tipud.add(new Tipp("A"));
        tipud.add(new Tipp("B"));
        tipud.add(new Tipp("C"));
        tipud.add(new Tipp("D"));
        tipud.add(new Tipp("E"));

        tipud.get(0).kaared.add(new Kaar(11, tipud.get(1)));
        tipud.get(0).kaared.add(new Kaar(22, tipud.get(2)));
        tipud.get(3).kaared.add(new Kaar(31, tipud.get(2)));
        tipud.get(4).kaared.add(new Kaar(0, tipud.get(4)));
        tipud.get(4).kaared.add(new Kaar(7, tipud.get(2)));

        GraphCanvas<Tipp> lõuend = new GraphCanvas<>();
        for (Tipp tipp : tipud) {
            lõuend.drawVertex(tipp, tipp.tähis);
            for (Kaar kaar : tipp.kaared) {
                lõuend.drawEdge(tipp, kaar.lõppTipp, String.valueOf(kaar.kaal));
            }
        }
        Dendrologist.drawGraph(lõuend);
    }

    private static class Tipp {
        public final String tähis;
        public final List<Kaar> kaared;

        public Tipp(String tähis) {
            this.tähis = tähis;
            this.kaared = new ArrayList<>();
        }
    }

    private static class Kaar {
        final int kaal;
        final Tipp lõppTipp;

        public Kaar(int kaal, Tipp lõppTipp) {
            this.kaal = kaal;
            this.lõppTipp = lõppTipp;
        }
    }

}
