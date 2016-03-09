package spoon.test.constructor.testclasses;

@SuppressWarnings("rawtypes")
//raw types, where used, are used purposely
public class AClass extends @Tacos.TypeAnnotation AAClass<@Tacos.TypeAnnotation String>.@Tacos.TypeAnnotation BBClass<@Tacos.TypeAnnotation Number> {

    public AClass(final AAClass<String> enclosingInstance) {
        enclosingInstance.super();
    }
}


class AAClass<T> {

    public class BBClass<S> {
    }
}

class AAAClass extends AAClass<String> {
    public class BBBClass extends BBClass<String> {
    }
}