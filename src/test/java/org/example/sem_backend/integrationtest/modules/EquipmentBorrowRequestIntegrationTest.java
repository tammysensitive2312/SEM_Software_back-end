package org.example.sem_backend.integrationtest.modules;

import io.restassured.http.ContentType;
import org.example.sem_backend.integrationtest.BaseIntegrationTest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.borrowing_module.repository.EquipmentBorrowRequestRepository;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.enums.EquipmentDetailStatus;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentRepository;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EquipmentBorrowRequestIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EquipmentBorrowRequestRepository requestRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentDetailRepository equipmentDetailRepository;

    @Autowired
    private UserRepository userRepository;

//    @SpyBean
//    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    public void setUp() {
        super.setUp();
        cleanupDatabase();
        setupTestData();
    }

    private User testUser;
    private Equipment testEquipment;

    private void setupTestData() {
        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("testemail@gmail.com")
                .password("testpassword")
                .build();
        testUser = userRepository.save(testUser);

        // Create test equipment
        testEquipment = Equipment.builder()
                .equipmentName("Test Equipment")
                .code("LPT")
                .usableQuantity(5)
                .totalQuantity(5)
                .inUseQuantity(0)
                .build();
        testEquipment = equipmentRepository.save(testEquipment);
    }

    private void cleanupDatabase() {
        requestRepository.deleteAll();
        equipmentDetailRepository.deleteAll();
        equipmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully approve borrow request when enough equipment available")
    void testApproveBorrowRequest_Success() {
        // Arrange
        List<EquipmentDetail> availableDetails = createAvailableEquipmentDetails(5); // Create 5 available items
        EquipmentBorrowRequest request = createBorrowRequest(3); // Request to borrow 3 items

        // Act & Assert API Response
        given()
                .when()
                .put("/api/borrow-requests/{id}/approve", request.getUniqueID())
                .then()
                .statusCode(200)
                .body(equalTo("Borrow Request approved successfully"));

        // Assert Database State
        EquipmentBorrowRequest approvedRequest = requestRepository.findById(request.getUniqueID()).orElse(null);
        assertNotNull(approvedRequest);
        assertEquals(EquipmentBorrowRequest.Status.BORROWED, approvedRequest.getStatus());

        // Verify equipment details were assigned
        EquipmentBorrowRequestDetail borrowDetail = approvedRequest.getBorrowRequestDetails().get(0);
        assertEquals(3, borrowDetail.getEquipmentDetails().size());

        // Verify the assigned equipment details are from available ones
        List<Long> assignedIds = borrowDetail.getEquipmentDetails()
                .stream()
                .map(EquipmentDetail::getId)
                .toList();

        assertTrue(availableDetails.stream()
                .map(EquipmentDetail::getId)
                .toList()
                .containsAll(assignedIds));
    }

    @Test
    @Order(2)
    @DisplayName("Should return 404 when request not found")
    void testApproveBorrowRequest_RequestNotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/borrow-requests/{id}/approve", 999L)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(3)
    @DisplayName("Should return 409 when not enough equipment available")
    void testApproveBorrowRequest_NotEnoughEquipment() {
        // Arrange
        createAvailableEquipmentDetails(2); // Only 2 available items
        EquipmentBorrowRequest request = createBorrowRequest(3); // Try to borrow 3 items

        // Act & Assert API Response
        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/borrow-requests/{id}/approve", request.getUniqueID())
                .then()
                .statusCode(409);

        // Verify database state hasn't changed
        EquipmentBorrowRequest unchangedRequest = requestRepository.findById(request.getUniqueID()).orElse(null);
        assertNotNull(unchangedRequest);
        assertEquals(EquipmentBorrowRequest.Status.NOT_BORROWED, unchangedRequest.getStatus());
        assertTrue(unchangedRequest.getBorrowRequestDetails().get(0).getEquipmentDetails().isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Should return 400 when request is in invalid state")
    void testApproveBorrowRequest_InvalidState() {
        // Arrange
        EquipmentBorrowRequest request = createBorrowRequest(1);
        request.setStatus(EquipmentBorrowRequest.Status.BORROWED);
        requestRepository.save(request);

        // Act & Assert
        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/borrow-requests/{id}/approve", request.getUniqueID())
                .then()
                .statusCode(400);
    }

    private List<EquipmentDetail> createAvailableEquipmentDetails(int count) {
        List<EquipmentDetail> details = IntStream.range(0, count)
                .mapToObj(i -> EquipmentDetail.builder()
                        .equipment(testEquipment)
                        .serialNumber(testEquipment.getCode() + i)
                        .status(EquipmentDetailStatus.USABLE)
                        .build())
                .collect(Collectors.toList());
        return equipmentDetailRepository.saveAll(details);
    }

    private EquipmentBorrowRequest createBorrowRequest(int quantityToBorrow) {
        EquipmentBorrowRequest request = new EquipmentBorrowRequest();
        request.setUser(testUser);
        request.setUniqueID(new Random().nextLong());
        request.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
        request.setExpectedReturnDate(LocalDate.now().plusDays(7));

        EquipmentBorrowRequestDetail requestDetail = new EquipmentBorrowRequestDetail();
        requestDetail.setBorrowRequest(request);
        requestDetail.setEquipment(testEquipment);
        requestDetail.setQuantityBorrowed(quantityToBorrow);
        request.getBorrowRequestDetails().add(requestDetail);

        return requestRepository.save(request);
    }

}