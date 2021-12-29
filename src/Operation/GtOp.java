package Operation;

public class GtOp extends Operations {


        private static final String operation = ">";

        public GtOp(ExprOp e1, ExprOp e2) {
            super(e1,e2);
        }

        @Override
        public String toString() {
            return "GtOp{" +
                    "e1=" + super.getE1() +
                    ", e2=" + super.getE2() +
                    '}';
        }
}
