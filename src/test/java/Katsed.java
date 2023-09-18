import ee.ut.dendroloj.Dendrologist;
import ee.ut.dendroloj.Grow;

import java.util.Arrays;

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
    static void juhuslikHargnemine(int n) {
        if (n == 0) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            if (Math.random() < 0.4)
                juhuslikHargnemine(n - 1);
        }
    }

    @Grow
    static int[] pööraJupid(int[] massiiv) {
        if (Math.random() < 0.2) {
            throw new RuntimeException("Random failure");
        }
        if (massiiv.length < 2) {
            return massiiv;
        } else if (massiiv.length == 2) {
            return new int[]{massiiv[1], massiiv[0]};
        } else {
            int[] väljund = new int[massiiv.length];
            int[] väljundA = pööraJupid(Arrays.copyOfRange(massiiv, 0, massiiv.length / 2));
            System.arraycopy(väljundA, 0, väljund, 0, väljundA.length);
            int[] väljundB = pööraJupid(Arrays.copyOfRange(massiiv, massiiv.length / 2, massiiv.length));
            System.arraycopy(väljundB, 0, väljund, väljundA.length, väljundB.length);
            return väljund;
        }
    }

    public static void main(String[] args) {
        Dendrologist.wakeUp();

        // fib(16);
        fib(5);
        // pööraJupid(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
    }
}
