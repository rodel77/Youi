package xyz.rodeldev.templates;

import java.util.Optional;

public class ValidationResult {
    private boolean success = true;
    private String message = "Error";

    public ValidationResult(boolean success){
        this.success = success;
    }

    public ValidationResult(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public Optional<String> getError(){
        return this.success ? Optional.empty() : Optional.of(message);
    }

    public static ValidationResult ok(){
        return new ValidationResult(true);
    }

    public static ValidationResult error(String message){
        return new ValidationResult(false, message);
    }
}