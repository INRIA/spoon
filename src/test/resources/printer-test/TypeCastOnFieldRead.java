public class TypeCastOnFieldRead {
    double myDouble = 0.0;
    public void where() {
        int myInt = (int) myDouble;
        int myInt2 = ((Double) myDouble).intValue();
    }
}
