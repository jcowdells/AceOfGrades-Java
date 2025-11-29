package forms;

import java.util.ArrayList;
import java.util.List;

public class FormEntry<T> {
    private final T input;
    private final List<String> errors;

    public FormEntry(T input) {
        this.input = input;
        this.errors = new ArrayList<>();
    }

    public FormEntry(T input, List<String> errors) {
        this.input = input;
        this.errors = errors;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public T getInput() {
        return input;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

}
