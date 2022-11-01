public class TypeCastOnFieldRead {
    double myDouble = 0.0;
    public void where() {
        int myInt = (int) myDouble;
    }
}
