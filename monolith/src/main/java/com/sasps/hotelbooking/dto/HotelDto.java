package com.sasps.hotelbooking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private String website;
    private Integer starRating;
    private String amenities;
    private String imageUrl;
    private Boolean active;
    private Integer totalRooms;
    private Integer availableRooms;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "Hotel name is required")
        @Size(max = 200, message = "Hotel name must not exceed 200 characters")
        private String name;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @NotBlank(message = "Address is required")
        @Size(max = 200, message = "Address must not exceed 200 characters")
        private String address;

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        private String city;

        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country must not exceed 100 characters")
        private String country;

        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        private String postalCode;

        @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "Phone number must be valid")
        private String phoneNumber;

        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        private String email;

        @Size(max = 200, message = "Website must not exceed 200 characters")
        private String website;

        @Min(value = 1, message = "Star rating must be at least 1")
        @Max(value = 5, message = "Star rating must not exceed 5")
        private Integer starRating;

        @Size(max = 500, message = "Amenities must not exceed 500 characters")
        private String amenities;

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        private String imageUrl;

        private Boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @Size(max = 200, message = "Hotel name must not exceed 200 characters")
        private String name;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @Size(max = 200, message = "Address must not exceed 200 characters")
        private String address;

        @Size(max = 100, message = "City must not exceed 100 characters")
        private String city;

        @Size(max = 100, message = "Country must not exceed 100 characters")
        private String country;

        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        private String postalCode;

        @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "Phone number must be valid")
        private String phoneNumber;

        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        private String email;

        @Size(max = 200, message = "Website must not exceed 200 characters")
        private String website;

        @Min(value = 1, message = "Star rating must be at least 1")
        @Max(value = 5, message = "Star rating must not exceed 5")
        private Integer starRating;

        @Size(max = 500, message = "Amenities must not exceed 500 characters")
        private String amenities;

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        private String imageUrl;

        private Boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchRequest {
        private String searchTerm;
        private String city;
        private String country;
        
        @Min(value = 1, message = "Minimum star rating must be at least 1")
        @Max(value = 5, message = "Minimum star rating must not exceed 5")
        private Integer minStarRating;
        
        private Boolean onlyWithAvailableRooms;
    }
}
