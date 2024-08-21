class ConstructorCallWithTypesNotOnClasspath {
    public static void main(String[] args) {
        type.not.on.Classpath instanceOfTypeNotOnClasspath = null;
        new another.type.not.on.Classpath(instanceOfTypeNotOnClasspath);
    }
}

