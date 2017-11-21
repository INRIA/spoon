module com.greetings {
    requires transitive java.logging;
    exports com.greetings.pkg to com.other.module, com.second.module;
    opens com.greetings.otherpkg;
    opens com.greetings.openpkg to com.third.module;
    uses com.greetings.pkg.ConsumedService;
}