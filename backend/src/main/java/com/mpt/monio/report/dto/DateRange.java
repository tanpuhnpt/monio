package com.mpt.monio.report.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record DateRange(LocalDateTime start, LocalDateTime end) {

    // mặc định nếu không truyền thì lấy từ đầu tháng đến hiện tại
    public static DateRange ofCurrentMonthIfNull(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDate.now().withDayOfMonth(1).atStartOfDay();

        LocalDateTime endDateTime = (endDate != null)
                ? endDate.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        return new DateRange(startDateTime, endDateTime);
    }
}
