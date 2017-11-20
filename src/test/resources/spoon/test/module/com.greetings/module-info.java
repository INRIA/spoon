module com.greetings {
    requires static java.logging;
    exports com.greetings.pkg to com.other.module, com.second.module;
}