package adp.resilience.gateway.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import adp.resilience.gateway.exception.RandomExceptionService;
import adp.resilience.gateway.model.dto.BookingRequestDto;
import adp.resilience.gateway.model.dto.BookingResponseDto;
import adp.resilience.gateway.model.dto.ErrorDto;
import adp.resilience.gateway.model.dto.RoomDto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Component(value = "roomService")
public class GatewayRoomService {
    private static final String ROOMS_RESILIENCE_KEY = "rooms";
    private static final String SINGLE_ROOM_RESILIENCE_KEY = "singleRoom";

    private static double DEFAULT_FAILURE_RATE = 0.7f;
    private static final boolean CAN_THROW = false;
    private final Gson gsonParser = new Gson();

    @CircuitBreaker(name = ROOMS_RESILIENCE_KEY)
    @Bulkhead(name = ROOMS_RESILIENCE_KEY)
    @Retry(name = ROOMS_RESILIENCE_KEY)
    @RateLimiter(name = ROOMS_RESILIENCE_KEY)
    public RoomDto[] getRooms() throws Throwable {
        if (CAN_THROW)
            RandomExceptionService.throwRandomException(DEFAULT_FAILURE_RATE);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/rooms"))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());

        return gsonParser.fromJson(response.body(), RoomDto[].class);
    }

    @CircuitBreaker(name = SINGLE_ROOM_RESILIENCE_KEY)
    @Retry(name = SINGLE_ROOM_RESILIENCE_KEY)
    @Bulkhead(name = SINGLE_ROOM_RESILIENCE_KEY)
    public RoomDto getRoom(Long id) throws Throwable {
        if (CAN_THROW)
            RandomExceptionService.throwRandomException(DEFAULT_FAILURE_RATE);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/rooms/" + id))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            throw new ResponseStatusException(NOT_FOUND, "No room with ID " + id);
        }

        return gsonParser.fromJson(response.body(), RoomDto.class);
    }

    @CircuitBreaker(name = SINGLE_ROOM_RESILIENCE_KEY)
    @Retry(name = SINGLE_ROOM_RESILIENCE_KEY)
    @Bulkhead(name = SINGLE_ROOM_RESILIENCE_KEY)
    public BookingResponseDto bookRoom(Long id, BookingRequestDto bookingRequest) throws Throwable {
        if (CAN_THROW)
            RandomExceptionService.throwRandomException(DEFAULT_FAILURE_RATE);

        var requestBody = new JSONObject();
        requestBody.put("paymentId", bookingRequest.paymentId());
        var reqInfo = new JSONObject();
        var info = bookingRequest.bookingInfo();
        reqInfo.put("roomId", info.roomId());
        reqInfo.put("startTime", info.startTime().toString());
        reqInfo.put("endTime", info.endTime().toString());
        reqInfo.put("guestName", info.guestName());
        requestBody.put("bookingInfo", reqInfo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/booking"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());

        var objectMapper = new ObjectMapper();
        if (response.statusCode() != 200) {
            var err = objectMapper.readValue(response.body(), ErrorDto.class);
            throw new ResponseStatusException(BAD_REQUEST, err.error());
        }

        return gsonParser.fromJson(response.body(), BookingResponseDto.class);
    }
}
