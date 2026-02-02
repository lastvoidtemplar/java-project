package image.processing;

public record Pixel(long red, long green, long blue) {
    private static final String NEGATIVE_VALUE_MESSAGE = "Red, green and blue must non-negative";

    public Pixel {
        if (red < 0 || green < 0 || blue < 0) {
            throw new IllegalArgumentException(NEGATIVE_VALUE_MESSAGE);
        }
    }
}
