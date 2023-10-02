import ee.ut.dendroloj.Dendrologist;

import java.util.List;

public class TippKatsed {

    public static void main(String[] args) {
        Dendrologist.setUIScale(1.5);

        Tipp tipp = new Tipp("A",
                new Tipp("B", null, new Tipp("C")),
                new Tipp("DendroloJ", new Tipp("D"), null));
        tipp.p.v.v = tipp.v.p;
        tipp.p.v.p = tipp;
        Dendrologist.drawBinaryTree(tipp, t -> t.info, t -> t.v, t -> t.p);
        // Dendrologist.drawBinaryTree(tipp, t -> t.info + " x=" + t.x, t -> t.v, t -> t.p);
    }

}
