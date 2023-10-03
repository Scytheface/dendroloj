import ee.ut.dendroloj.Dendrologist;

public class TippKatsed {

    public static void main(String[] args) {
        Dendrologist.setUIScale(1.5);

        Tipp tipp = new Tipp("juur",
                new Tipp("A",
                        new Tipp("B", null, new Tipp("C")),
                        new Tipp("DendroloJ",
                                new Tipp("D"),
                                new Tipp("E", new Tipp("F"), new Tipp("G")))),
                null
        );
        tipp.v.v.p.p = tipp.v.p.v;
        tipp.v.v.v = tipp.v.v.p;
        tipp.v.p.p.v.p = tipp.v;
        Dendrologist.drawBinaryTree(tipp, t -> t.info, t -> t.v, t -> t.p);
        // Dendrologist.drawBinaryTree(tipp, t -> t.info + " x=" + t.x, t -> t.v, t -> t.p);
    }

}
