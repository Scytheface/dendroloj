import ee.ut.dendroloj.Dendrologist;
import ee.ut.dendroloj.Grow;

public class Katsed {
    @Grow
    static int unaarne(int x, String y) {
        if (x == 0) return 1;
        return 1 + unaarne(x - 1, y);
    }

    @Grow
    static int fib(int n) {
        if (n < 2) return n;
        return fib(n - 2) + fib(n - 1);
    }

    @Grow
    private static void juhuslikHargnemine(int n) {
        if (n == 0) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            if (Math.random() < 0.4)
                juhuslikHargnemine(n - 1);
        }
    }

    public static void main(String[] args) {
        Dendrologist.wakeUp(1.5, true);

        fib(8);
        //new OhtlikAsi("A").fib(5, new OhtlikAsi("B"));
    }
}
