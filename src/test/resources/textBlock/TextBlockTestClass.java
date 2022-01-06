package textBlock;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class TextBlockTestClass{
	void m1() {
		String html = """
	                  <html>
	                      <body>
	                          <p>Hello, कसौटी 🥲</p>
	                      </body>
	                  </html>
	                  """;
	}

	void m2(){
		String query = """
	                       SELECT "EMP_ID", "LAST_NAME" FROM "EMPLOYEE_TB"
	                       WHERE "CITY" = 'INDIANAPOLIS'
	                       ORDER BY "EMP_ID", "LAST_NAME";
	                       """;
	}

	void m3(){
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		try {
			Object obj = engine.eval("""
			                         function hello() {
			                             print('"Hello, world"');
			                         }
			                     
			                         hello();
			                         """);
		}catch(Exception e) {}
		System.out.println(query);
	}

	void m4() {
		String empty = """
		""";
	}

	void m5() {
		String escape = """
        no-break space: \\00a0
        newline:        \\n
        tab:            \\t ('\t')
        """;
	}
}
