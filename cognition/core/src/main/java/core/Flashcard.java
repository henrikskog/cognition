package core;

import core.validators.Validator;

import java.util.Iterator;
import java.util.Objects;

public class Flashcard implements Iterable<Flashcard> {
    private String UUID;
    private String front;
    private String answer;


    /**
     * Initializes a Flashcard object with the provided parameters.
     *
     * @param UUID   is the identifier for the Flashcard.
     * @param front  is the front statement or question on the Flashcard.
     * @param answer is the flipped side of the Flashcard, and the answer to the front.
     */
    public Flashcard(String UUID, String front, String answer) {
        setUUID(UUID);
        setFront(front);
        setAnswer(answer);
    }

    /**
     * Initializes a Flashcard object with no values.
     * This is used when deserializing the object.
     */
    public Flashcard() {
    }

    public String getUUID() {
        return UUID;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        if (!Flashcard.isValidFront(front)) {
            throw new IllegalArgumentException();
        }

        this.front = front;
    }

    public static boolean isValidFront(String front) {
        return front != null && front.length() >= 1;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        if (!Flashcard.isValidAnswer(answer)) {
            throw new IllegalArgumentException();
        }
        this.answer = answer;
    }

    public static boolean isValidAnswer(String answer) {
        return answer != null && answer.length() >= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Flashcard flashcard = (Flashcard) o;
        return Objects.equals(UUID, flashcard.UUID) && Objects.equals(front, flashcard.front) && Objects.equals(answer, flashcard.answer);
    }

    private void setUUID(String UUID) {
        if (!Validator.isValidUUID(UUID)) {
            throw new IllegalArgumentException();
        }

        this.UUID = UUID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(UUID, front, answer);
    }

    @Override
    public String toString() {
        return "Flashcard{" +
                "UUID=" + UUID +
                ", front='" + front + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    @Override
    public Iterator iterator() {
        return null;
    }
}
