package spoon.test.intercession.insertBefore;

class InsertBeforeExample {
    public InsertBeforeExample() {
        super();
    }

    int ifWithoutBraces() {
        // without braces
        if (1==0+1)
            return 0;
        return -1;
    }

    int ifWithBraces() {
        if (1==0+1) {
            return 0;
        }
        return -1;
    }

    void switchMethod() {
        int i=0,j=0,k=0,l=0;
        switch(i){
            case 0: j++;
            case 1: k++;
            case 2: l++;
            case 3: l++;k++;
        }
    }

}
