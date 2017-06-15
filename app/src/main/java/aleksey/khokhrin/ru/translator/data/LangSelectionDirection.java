package aleksey.khokhrin.ru.translator.data;

public enum LangSelectionDirection {
    SOURCE(0), TARGET(1);
    private int value;
    LangSelectionDirection(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}