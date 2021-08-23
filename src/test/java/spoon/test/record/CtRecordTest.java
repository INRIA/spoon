package spoon.test.record;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class CtRecordTest {
  
  @Test
  public void testEmptyRecord() {
    String code = "src/test/resources/records/EmptyRecord.java";
    Launcher launcher = new Launcher();
    launcher.getEnvironment().setComplianceLevel(16);
    launcher.addInputResource(code);
    CtModel model = launcher.buildModel();
    Collection<?> records = model.getAllTypes();
    assertEquals(1, model.getAllTypes().size());
    int a = 3;
  }

  @Test
  public void testSingleParameterRecord() {
    String code = "src/test/resources/records/SingleParameter.java";
    Launcher launcher = new Launcher();
    launcher.getEnvironment().setComplianceLevel(16);
    launcher.addInputResource(code);
    CtModel model = launcher.buildModel();
    assertEquals(1, model.getAllTypes().size());
    Collection<CtRecord<?>> records = model.getElements(new TypeFilter<>(CtRecord.class));
    assertEquals(1,records.iterator().next().getFields().size());
    assertEquals(1,records.iterator().next().getMethods().size());
  }

  @Test
  public void testMultipleParameterRecord() {
    String code = "src/test/resources/records/MultiParameter.java";
    Launcher launcher = new Launcher();
    launcher.getEnvironment().setComplianceLevel(16);
    launcher.addInputResource(code);
    CtModel model = launcher.buildModel();
    assertEquals(1, model.getAllTypes().size());
    Collection<CtRecord<?>> records = model.getElements(new TypeFilter<>(CtRecord.class));
    assertEquals(2,records.iterator().next().getFields().size());
    assertEquals(2,records.iterator().next().getMethods().size());
  }

  @Test
  public void testExplicitParameterAccessor() {
    String code = "src/test/resources/records/ExplicitAccessor.java";
    Launcher launcher = new Launcher();
    launcher.getEnvironment().setComplianceLevel(16);
    launcher.addInputResource(code);
    CtModel model = launcher.buildModel();
    assertEquals(1, model.getAllTypes().size());
    Collection<CtRecord<?>> records = model.getElements(new TypeFilter<>(CtRecord.class));
    assertEquals(2,records.iterator().next().getFields().size());
    assertEquals(2,records.iterator().next().getMethods().size());
    int a = 3;
  }
}
