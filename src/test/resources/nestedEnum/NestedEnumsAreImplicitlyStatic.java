class NestedInClass {
  enum NestedEnum {
    A;
  }
}
interface NestedInInterface {
  enum NestedEnum {
    A;
  }
}
enum NestedInEnum {
  A;
  enum NestedEnum {
    A;
  }
}
@interface NestedInAnnotation {
  enum NestedEnum {
    A;
  }
}
