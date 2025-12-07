package com.sasps.roomservice.service;

import com.sasps.roomservice.dto.HotelDto;
import com.sasps.roomservice.exception.ResourceNotFoundException;
import com.sasps.roomservice.model.Hotel;
import com.sasps.roomservice.repository.HotelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelDto> getAllHotels() {
        log.debug("Fetching all hotels");
        return hotelRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<HotelDto> getAllActiveHotels() {
        log.debug("Fetching all active hotels");
        return hotelRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public HotelDto getHotelById(Long id) {
        log.debug("Fetching hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        return convertToDto(hotel);
    }

    @Transactional
    public HotelDto createHotel(HotelDto.CreateRequest request) {
        log.info("Creating new hotel with name: {}", request.getName());
        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .website(request.getWebsite())
                .starRating(request.getStarRating())
                .amenities(request.getAmenities())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created successfully with id: {}", savedHotel.getId());
        return convertToDto(savedHotel);
    }

    @Transactional
    public HotelDto updateHotel(Long id, HotelDto.UpdateRequest request) {
        log.info("Updating hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        
        if (request.getName() != null) hotel.setName(request.getName());
        if (request.getDescription() != null) hotel.setDescription(request.getDescription());
        if (request.getAddress() != null) hotel.setAddress(request.getAddress());
        if (request.getCity() != null) hotel.setCity(request.getCity());
        if (request.getCountry() != null) hotel.setCountry(request.getCountry());
        if (request.getPostalCode() != null) hotel.setPostalCode(request.getPostalCode());
        if (request.getPhoneNumber() != null) hotel.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) hotel.setEmail(request.getEmail());
        if (request.getWebsite() != null) hotel.setWebsite(request.getWebsite());
        if (request.getStarRating() != null) hotel.setStarRating(request.getStarRating());
        if (request.getAmenities() != null) hotel.setAmenities(request.getAmenities());
        if (request.getImageUrl() != null) hotel.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) hotel.setActive(request.getActive());

        Hotel updatedHotel = hotelRepository.save(hotel);
        log.info("Hotel updated successfully with id: {}", updatedHotel.getId());
        return convertToDto(updatedHotel);
    }

    @Transactional
    public void deleteHotel(Long id) {
        log.info("Deleting hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        hotelRepository.delete(hotel);
        log.info("Hotel deleted successfully with id: {}", id);
    }

    public List<HotelDto> getHotelsByCity(String city) {
        log.debug("Fetching hotels in city: {}", city);
        return hotelRepository.findByCity(city).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<HotelDto> searchHotels(HotelDto.SearchRequest searchRequest) {
        log.debug("Searching hotels with criteria: {}", searchRequest);
        
        List<Hotel> hotels = hotelRepository.findAll();
        
        return hotels.stream()
                .filter(hotel -> {
                    if (searchRequest.getSearchTerm() != null && !searchRequest.getSearchTerm().isEmpty()) {
                        String searchTerm = searchRequest.getSearchTerm().toLowerCase();
                        boolean matchesName = hotel.getName().toLowerCase().contains(searchTerm);
                        boolean matchesDescription = hotel.getDescription() != null && 
                                hotel.getDescription().toLowerCase().contains(searchTerm);
                        if (!matchesName && !matchesDescription) {
                            return false;
                        }
                    }
                    
                    if (searchRequest.getCity() != null && !hotel.getCity().equalsIgnoreCase(searchRequest.getCity())) {
                        return false;
                    }
                    
                    if (searchRequest.getCountry() != null && !hotel.getCountry().equalsIgnoreCase(searchRequest.getCountry())) {
                        return false;
                    }
                    
                    if (searchRequest.getMinStarRating() != null &&
                        (hotel.getStarRating() == null || hotel.getStarRating() < searchRequest.getMinStarRating())) {
                        return false;
                    }
                    
                    if (searchRequest.getOnlyWithAvailableRooms() != null && searchRequest.getOnlyWithAvailableRooms()) {
                        long availableRooms = hotel.getRooms().stream()
                                .filter(room -> room.getStatus() == com.sasps.roomservice.model.Room.RoomStatus.AVAILABLE)
                                .count();
                      return availableRooms != 0;
                    }
                    
                    return true;
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private HotelDto convertToDto(Hotel hotel) {
        return HotelDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .postalCode(hotel.getPostalCode())
                .phoneNumber(hotel.getPhoneNumber())
                .email(hotel.getEmail())
                .website(hotel.getWebsite())
                .starRating(hotel.getStarRating())
                .amenities(hotel.getAmenities())
                .imageUrl(hotel.getImageUrl())
                .active(hotel.getActive())
                .roomCount(hotel.getRooms().size())
                .createdAt(hotel.getCreatedAt())
                .updatedAt(hotel.getUpdatedAt())
                .build();
    }
}

