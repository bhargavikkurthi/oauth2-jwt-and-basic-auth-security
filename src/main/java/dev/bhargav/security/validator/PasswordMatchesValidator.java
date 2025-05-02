package dev.bhargav.security.validator;

import dev.bhargav.security.exception.ArgumentValidationException;
import dev.bhargav.security.model.SignUpRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignUpRequest> {

    @SneakyThrows
    @Override
    public boolean isValid(final SignUpRequest user, final ConstraintValidatorContext context) {
        if (!user.getPassword().equals(user.getMatchingPassword())) {
            throw new ArgumentValidationException("Values of 'password' and 'matchPassword' are different");
        }
        return true;
    }
}
