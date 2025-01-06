package uhk.projekt.model;

public enum TaskStatus {
    OPEN("Otevřený"),
    IN_PROGRESS("Probíhající"),
    COMPLETED("Dokončený"),
    CLOSED("Uzavřený");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
