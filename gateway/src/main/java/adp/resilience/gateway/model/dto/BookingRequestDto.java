package adp.resilience.gateway.model.dto;

import java.util.UUID;

public record BookingRequestDto(
                BookingRequestInfoDto bookingInfo,
                UUID paymentId) {
}
