package adp.resilience.gateway.model.dto;

import java.time.LocalDateTime;

public record BookingRequestInfoDto(
                Long roomId,
                LocalDateTime startTime,
                LocalDateTime endTime,
                String guestName) {
}