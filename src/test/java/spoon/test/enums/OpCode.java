package spoon.test.enums;

import java.util.Stack;

public enum OpCode implements Performable {
    PUSH(1) {
        public void perform(Stack<Integer> s, int[] op) {
            s.push(op[0]); }},
    ADD(0) {
        public void perform(Stack<Integer> s, int[] op) {
            s.push( s.pop() + s.pop() ); }};
    OpCode(int numOp) { this.numOp = numOp; }
    private int numOp;
    private OpCode OP;
    public int dummy() {
    	return OP.numOp+numOp;
    }
}
