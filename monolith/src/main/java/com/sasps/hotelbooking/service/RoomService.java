package com.sasps.hotelbooking.service;

import com.sasps.hotelbooking.dto.RoomDto;
import com.sasps.hotelbooking.exception.BusinessException;
import com.sasps.hotelbooking.exception.ResourceAlreadyExistsException;
import com.sasps.hotelbooking.exception.ResourceNotFoundException;
import com.sasps.hotelbooking.model.Hotel;
import com.sasps.hotelbooking.model.Room;
import com.sasps.hotelbooking.repository.HotelRepository;
import com.sasps.hotelbooking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomDto getRoomByRoomNumberAndHotel(String roomNumber, Long hotelId) {
        log.debug("Fetching room with room number: {} in hotel: {}", roomNumber, hotelId);
        Room room = roomRepository.findByRoomNumberAndHotelId(roomNumber, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with roomNumber: " + roomNumber + " in hotel: " + hotelId));
        return convertToDto(room);
    }

    public List<RoomDto> getRoomsByHotel(Long hotelId) {
        log.debug("Fetching all rooms for hotel: {}", hotelId);
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("Hotel", "id", hotelId);
        }
        return roomRepository.findByHotelId(hotelId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDto createRoom(RoomDto.CreateRequest request) {
        log.info("Creating new room with room number: {} in hotel: {}", request.getRoomNumber(), request.getHotelId());
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", request.getHotelId()));
        if (roomRepository.existsByRoomNumberAndHotelId(request.getRoomNumber(), request.getHotelId())) {
            throw new ResourceAlreadyExistsException("Room with number " + request.getRoomNumber() + 
                    " already exists in hotel " + hotel.getName());
        }
        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .pricePerNight(request.getPricePerNight())
                .maxOccupancy(request.getMaxOccupancy())
                .description(request.getDescription())
                .facilities(request.getFacilities())
                .floorNumber(request.getFloorNumber())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus() != null ? request.getStatus() : Room.RoomStatus.AVAILABLE)
                .build();
        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully with id: {}", savedRoom.getId());
        return convertToDto(savedRoom);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto.UpdateRequest request) {
        log.info("Updating room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        if (request.getRoomNumber() != null && 
            !request.getRoomNumber().equals(room.getRoomNumber()) &&
            roomRepository.existsByRoomNumberAndHotelId(request.getRoomNumber(), room.getHotel().getId())) {
            throw new ResourceAlreadyExistsException("Room with number " + request.getRoomNumber() + 
                    " already exists in this hotel");
        }
        if (request.getRoomNumber() != null) {
            room.setRoomNumber(request.getRoomNumber());
        }
        if (request.getRoomType() != null) {
            room.setRoomType(request.getRoomType());
        }
        if (request.getPricePerNight() != null) {
            room.setPricePerNight(request.getPricePerNight());
        }
        if (request.getMaxOccupancy() != null) {
            room.setMaxOccupancy(request.getMaxOccupancy());
        }
        if (request.getDescription() != null) {
            room.setDescription(request.getDescription());
        }
        if (request.getFacilities() != null) {
            room.setFacilities(request.getFacilities());
        }
        if (request.getFloorNumber() != null) {
            room.setFloorNumber(request.getFloorNumber());
        }
        if (request.getImageUrl() != null) {
            room.setImageUrl(request.getImageUrl());
        }
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        Room updatedRoom = roomRepository.save(room);
        log.info("Room updated successfully with id: {}", updatedRoom.getId());
        return convertToDto(updatedRoom);
    }

    @Transactional
    public void deleteRoom(Long id) {
        log.info("Deleting room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        roomRepository.delete(room);
        log.info("Room deleted successfully with id: {}", id);
    }

    public List<RoomDto> getRoomsByType(Room.RoomType roomType) {
        log.debug("Fetching rooms with type: {}", roomType);
        return roomRepository.findByRoomType(roomType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RoomDto> getRoomsByStatus(Room.RoomStatus status) {
        log.debug("Fetching rooms with status: {}", status);
        return roomRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RoomDto> searchAvailableRooms(RoomDto.SearchRequest searchRequest) {
        log.debug("Searching available rooms for dates: {} to {}", 
                searchRequest.getCheckInDate(), searchRequest.getCheckOutDate());
        if (searchRequest.getCheckInDate().isAfter(searchRequest.getCheckOutDate())) {
            throw new BusinessException("Check-in date must be before check-out date");
        }
        if (searchRequest.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Check-in date cannot be in the past");
        }
        List<Room> availableRooms;
        if (searchRequest.getHotelId() != null) {
            if (!hotelRepository.existsById(searchRequest.getHotelId())) {
                throw new ResourceNotFoundException("Hotel", "id", searchRequest.getHotelId());
            }
            availableRooms = roomRepository.findAvailableRoomsForHotelAndDates(
                    searchRequest.getHotelId(),
                    searchRequest.getCheckInDate(),
                    searchRequest.getCheckOutDate()
            );
        } else if (searchRequest.getRoomType() != null) {
            availableRooms = roomRepository.findAvailableRoomsByTypeForDates(
                    searchRequest.getRoomType(),
                    searchRequest.getCheckInDate(),
                    searchRequest.getCheckOutDate()
            );
        } else {
            availableRooms = roomRepository.findAvailableRoomsForDates(
                    searchRequest.getCheckInDate(),
                    searchRequest.getCheckOutDate()
            );
        }
        return availableRooms.stream()
                .filter(room -> searchRequest.getMinOccupancy() == null || 
                               room.getMaxOccupancy() >= searchRequest.getMinOccupancy())
                .filter(room -> searchRequest.getMinPrice() == null || 
                               room.getPricePerNight().compareTo(searchRequest.getMinPrice()) >= 0)
                .filter(room -> searchRequest.getMaxPrice() == null || 
                               room.getPricePerNight().compareTo(searchRequest.getMaxPrice()) <= 0)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDto updateRoomStatus(Long id, Room.RoomStatus status) {
        log.info("Updating room status for room id: {} to {}", id, status);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        room.setStatus(status);
        Room updatedRoom = roomRepository.save(room);
        log.info("Room status updated successfully");
        return convertToDto(updatedRoom);
    }

    public java.util.Map<Room.RoomStatus, Long> getRoomStatisticsByStatus() {
        log.debug("Fetching room statistics by status");
        return java.util.Arrays.stream(Room.RoomStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> roomRepository.countByStatus(status)
                ));
    }

    private RoomDto convertToDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .hotelName(room.getHotel().getName())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .maxOccupancy(room.getMaxOccupancy())
                .description(room.getDescription())
                .facilities(room.getFacilities())
                .floorNumber(room.getFloorNumber())
                .imageUrl(room.getImageUrl())
                .status(room.getStatus())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
