public class CommentsOnCaseExpression {
    public void commentsShouldBeAttachedToCtLiteral(int stage) {
        String stageStr;
        boolean fullStatus;
        switch (stage) {
            // Inline comments are also a part now
            case (1/*org.apache.coyote.Constants.STAGE_PARSE*/):
                stageStr = "P";
                fullStatus = false;
                break;
            case (2/*org.apache.coyote.Constants.STAGE_PREPARE*/):
                stageStr = "P";
                fullStatus = false;
                break;
            case (3/*org.apache.coyote.Constants.STAGE_SERVICE*/):
                stageStr = "S";
                break;
        }
    }
}
