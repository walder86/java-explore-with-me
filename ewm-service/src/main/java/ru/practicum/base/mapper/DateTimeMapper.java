package ru.practicum.base.mapper;


import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;



@UtilityClass
public class DateTimeMapper {

    public static String toStringDate(LocalDateTime date) {
        return date.toString();
    }


}
