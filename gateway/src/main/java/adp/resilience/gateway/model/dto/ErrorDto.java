package adp.resilience.gateway.model.dto;

public record ErrorDto(
    String error,
    Integer status,
    String timestamp,
    String path,
    String requestId) {
}