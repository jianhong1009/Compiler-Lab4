public class Variable {
    public String name;
    public String store;
    public String load;
    public int status; // 1表示定义了，还未赋值，2表示定义了也赋值了
    public boolean isConst;

    public Variable(String name, String store, String load, int status, boolean isConst) {
        this.name = name;
        this.store = store;
        this.load = load;
        this.status = status;
        this.isConst = isConst;
    }

    public static String getStore(String name) {
        for (Variable v : Visitor.variableList) {
            if (v.name.equals(name)) {
                return v.store;
            }
        }
        return null;
    }

    public static String getLoad(String name) {
        for (Variable v : Visitor.variableList) {
            if (v.name.equals(name)) {
                return v.load;
            }
        }
        return null;
    }

    public static void setLoad(String name, String load) {
        for (Variable v : Visitor.variableList) {
            if (v.name.equals(name)) {
                v.load = load;
            }
        }
    }

    public static void checkRepeat(String name) {
        for (Variable v : Visitor.variableList) {
            if (v.name.equals(name)) {
                System.exit(1);
            }
        }
    }

    public static void checkExist(String name) {
        for (Variable v : Visitor.variableList) {
            if (v.name.equals(name)) {
                return;
            }
        }
        System.exit(1);
    }

    public static boolean isConst(String name) {
        for (Variable v : Visitor.variableList) {
            if (v.name.equals(name) && v.isConst) {
                return true;
            } else if (v.name.equals(name)) {
                return false;
            }
        }
        return true;
    }
}
