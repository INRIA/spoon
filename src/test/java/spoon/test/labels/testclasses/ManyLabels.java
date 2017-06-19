package spoon.test.labels.testclasses;

/**
 * Created by urli on 19/06/2017.
 */
public class ManyLabels {
    public static void main(String[] args) {
        labelBlock:
        {
            System.out.println("block");
            if (args.length != 0 && args[0].equals("test")) {
                System.out.println("block after break3");
                break labelBlock;
            }

            labelIf:
            if (args.length > 1 && args[0].equals("test")) {
                if (args.length == 2) {
                    break labelIf;
                }
                System.out.println("block after break1");
            }
            System.out.println("block after break");
        }

        sw:
        switch ("f") {
            case "f":
                label:
                do {
                    System.out.println("do");
                    while (true) {
                        for (int i = 0; i < 4; i++) {
                            System.out.println("for");
                            if (i % 2 == 0) {
                                break label;
                            } else if (i == 3) {
                                continue label;
                            }
                            System.out.println("for 1");
                        }
                        break sw;
                    }
                } while (true);
                break;
        }
    }
}
