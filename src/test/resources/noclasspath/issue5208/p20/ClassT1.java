package p20;
public abstract class ClassT1 {
    ClassT3 classT3;

    public ClassT1(){
        this.classT3 = new ClassT3(this);
    }

    void fun2(ClassT3 classT3){
    }
}