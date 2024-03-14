package adp.resilience.gateway.model.dto;

import java.time.LocalDateTime;

public record BookingResponseDto(
                Long bookingId,
                Long roomId,
                String startTime, // FIXME
                String endTime, // FIXME
                String guestName) {
}