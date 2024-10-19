package org.example.sem_backend.main_service.middleware.routing;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoutingController {

    private static final Logger logger = LogManager.getLogger(RoutingController.class);

    private final RestTemplate restTemplate;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> route(HttpServletRequest request) {
        long startTime = System.currentTimeMillis(); // Đánh dấu thời gian bắt đầu
        String path = request.getRequestURI();

        try {
            String targetUri = extractTargetUri(request);
            URI uri = new URI(targetUri);
            HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());

            logger.info("Routing request: [{}] {} -> {}", request.getMethod(), path, targetUri);
            ResponseEntity<String> response = restTemplate.exchange(uri, httpMethod, null, String.class);

            long duration = System.currentTimeMillis() - startTime; // Tính thời gian xử lý
            logger.info("Request [{}] {} completed with status {} in {} ms", request.getMethod(), path, response.getStatusCode(), duration);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());

        } catch (Exception e) {
            logger.error("Routing failed for request [{}] {}: {}", request.getMethod(), path, e.getMessage(), e);
            return ResponseEntity.status(500).body("Routing failed due to an error.");
        }
    }

    // Phương thức để xác định địa chỉ đích
    private String extractTargetUri(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.startsWith("/api/v1/equipment")) {
            return "http://localhost:8080/equipment" + path.replace("/api/v1/equipment", "");
        } else if (path.startsWith("/api/v1/user")) {
            return "http://localhost:8080/user" + path.replace("/api/v1/user", "");
        }

        throw new IllegalArgumentException("Unknown route for: " + path);
    }
}
