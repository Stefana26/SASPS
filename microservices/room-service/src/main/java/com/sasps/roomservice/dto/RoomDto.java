package com.sasps.roomservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDto {
    
    private Long id;
    private Long hotelId;
    private String hotelName;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    private Integer maxOccupancy;
    private String description;
    private String facilities;
    private Integer floorNumber;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotNull(message = "Hotel ID is required")
        private Long hotelId;

        @NotBlank(message = "Room number is required")
        @Size(max = 10, message = "Room number must not exceed 10 characters")
        private String roomNumber;

        @NotBlank(message = "Room type is required")
        private String roomType;

        @NotNull(message = "Price per night is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
        private BigDecimal pricePerNight;

        @NotNull(message = "Max occupancy is required")
        @Min(value = 1, message = "Max occupancy must be at least 1")
        @Max(value = 10, message = "Max occupancy must not exceed 10")
        private Integer maxOccupancy;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @Size(max = 500, message = "Facilities must not exceed 500 characters")
        private String facilities;

        @Min(value = -5, message = "Floor number must be at least -5")
        @Max(value = 100, message = "Floor number must not exceed 100")
        private Integer floorNumber;

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        private String imageUrl;

        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @Size(max = 10, message = "Room number must not exceed 10 characters")
        private String roomNumber;

        private String roomType;

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
        private BigDecimal pricePerNight;

        @Min(value = 1, message = "Max occupancy must be at least 1")
        @Max(value = 10, message = "Max occupancy must not exceed 10")
        private Integer maxOccupancy;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @Size(max = 500, message = "Facilities must not exceed 500 characters")
        private String facilities;

        @Min(value = -5, message = "Floor number must be at least -5")
        @Max(value = 100, message = "Floor number must not exceed 100")
        private Integer floorNumber;

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        private String imageUrl;

        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {
        private Long hotelId;
        private String roomType;

        @Min(value = 1, message = "Occupancy must be at least 1")
        private Integer minOccupancy;

        @DecimalMin(value = "0.0", inclusive = false, message = "Min price must be greater than 0")
        private BigDecimal minPrice;

        @DecimalMin(value = "0.0", inclusive = false, message = "Max price must be greater than 0")
        private BigDecimal maxPrice;

        @NotNull(message = "Check-in date is required")
        @FutureOrPresent(message = "Check-in date must be today or in the future")
        private LocalDate checkInDate;

        @NotNull(message = "Check-out date is required")
        @Future(message = "Check-out date must be in the future")
        private LocalDate checkOutDate;
    }
}

