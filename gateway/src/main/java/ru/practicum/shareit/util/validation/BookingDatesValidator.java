package ru.practicum.shareit.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingRqDto;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingRqDto> {
    @Override
    public boolean isValid(BookingRqDto booking, ConstraintValidatorContext context) {
        if (booking.getStart() == null || booking.getEnd() == null) {
            return false;
        }
        if (!booking.getStart().isBefore(booking.getEnd())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата начала должна быть раньше даты окончания и не совпадать с ней.")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
