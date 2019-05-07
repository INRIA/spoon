import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.ANNOTATION_TYPE})
public @interface ClassWithSyntheticEnumNotParsable {

    String[] method() default {};

    String remover() default "";

    public enum NONE {
        private static final /* synthetic */ NONE[] $VALUES;

        static {
            $VALUES = new NONE[0];
        }

        public static NONE valueOf(final String s) {
            return Enum.valueOf(NONE.class, s);
        }

        public static NONE[] values() {
            return NONE.$VALUES.clone();
        }
    }
}