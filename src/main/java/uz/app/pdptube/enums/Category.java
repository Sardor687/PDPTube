package uz.app.pdptube.enums;

public enum Category {
    ENTERTAINMENT("entertainment"),
    EDUCATION("education"),
    SPORTS("sports"),
    MUSIC("music"),
    GAMING("gaming"),
    UNDEFINED("undefined");

    private final String displayName;


    Category(String displayName) {
        this.displayName = displayName;
    }


    public String getString() {
        return displayName;
    }


    public static Category fromString(String value) {
        value = value.toLowerCase();
        for (Category category : Category.values()) {
            if (category.displayName.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + value);
    }
}
