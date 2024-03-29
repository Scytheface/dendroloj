import ee.ut.dendroloj.Grow;

public class OhtlikAsi {
        public final String tekst;

        public OhtlikAsi(String tekst) {
                this.tekst = tekst;
        }

        @Grow
        int fib(int n, OhtlikAsi a) {
            if (n < 2) return n;
            return fib(n - 2, a) + fib(n - 1, a);
        }

        // @Grow annotatsioon toString meetodil mida kutsutakse puu uuendamise ajal
        @Grow
        @Override
        public String toString() {
            return tekst;
        }
}
