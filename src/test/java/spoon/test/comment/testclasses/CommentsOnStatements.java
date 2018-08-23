package spoon.test.comment.testclasses;

public class CommentsOnStatements {

    String value = "";

    public String m1() {
    	//c1
        if (value == null) {
        	//c2 belongs to toto
            value = "toto";
        } else if (value.equals("x")) {
        	//c3 belongs to getClass
        	this.getClass();
        }
        //c4 comment of return 
        return value.substring(1);
    }
}
