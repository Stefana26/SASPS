package com.sasps.roomservice.service;

import com.sasps.roomservice.model.Hotel;
import com.sasps.roomservice.model.Room;
import com.sasps.roomservice.repository.HotelRepository;
import com.sasps.roomservice.repository.RoomRepository;
import com.sasps.roomservice.dto.RoomDto;
import com.sasps.roomservice.exception.BusinessException;
import com.sasps.roomservice.exception.ResourceNotFoundException;
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
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomDto getRoomById(Long id) {
        log.debug("Fetching room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
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

    public RoomDto getRoomByRoomNumberAndHotel(String roomNumber, Long hotelId) {
        log.debug("Fetching room with number: {} in hotel: {}", roomNumber, hotelId);
        Room room = roomRepository.findByRoomNumberAndHotelId(roomNumber, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomNumber", roomNumber));
        return convertToDto(room);
    }

    public List<RoomDto> getRoomsByType(String roomType) {
        log.debug("Fetching rooms of type: {}", roomType);
        Room.RoomType type = Room.RoomType.valueOf(roomType);
        return roomRepository.findByRoomType(type).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RoomDto> searchAvailableRooms(RoomDto.SearchRequest searchRequest) {
        log.debug("Searching available rooms with criteria: {}", searchRequest);
        
        // Start with all rooms
        List<Room> rooms = roomRepository.findAll();
        
        // Apply filters
        return rooms.stream()
                .filter(room -> {
                    if (searchRequest.getHotelId() != null && !room.getHotel().getId().equals(searchRequest.getHotelId())) {
                        return false;
                    }
                    
                    if (searchRequest.getRoomType() != null && !room.getRoomType().name().equals(searchRequest.getRoomType())) {
                        return false;
                    }
                    
                    if (searchRequest.getMinOccupancy() != null && room.getMaxOccupancy() < searchRequest.getMinOccupancy()) {
                        return false;
                    }
                    
                    if (searchRequest.getMinPrice() != null && room.getPricePerNight().compareTo(searchRequest.getMinPrice()) < 0) {
                        return false;
                    }
                    if (searchRequest.getMaxPrice() != null && room.getPricePerNight().compareTo(searchRequest.getMaxPrice()) > 0) {
                        return false;
                    }
                    
                    return room.getStatus() == Room.RoomStatus.AVAILABLE;
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDto createRoom(RoomDto.CreateRequest request) {
        log.info("Creating new room with room number: {} in hotel: {}", request.getRoomNumber(), request.getHotelId());
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", request.getHotelId()));
        
        if (roomRepository.existsByRoomNumberAndHotelId(request.getRoomNumber(), request.getHotelId())) {
            throw new BusinessException("Room with number " + request.getRoomNumber() + 
                    " already exists in hotel " + hotel.getName());
        }
        
        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(request.getRoomNumber())
                .roomType(Room.RoomType.valueOf(request.getRoomType()))
                .pricePerNight(request.getPricePerNight())
                .maxOccupancy(request.getMaxOccupancy())
                .description(request.getDescription())
                .facilities(request.getFacilities())
                .floorNumber(request.getFloorNumber())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus() != null ? Room.RoomStatus.valueOf(request.getStatus()) : Room.RoomStatus.AVAILABLE)
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
            throw new BusinessException("Room with number " + request.getRoomNumber() + 
                    " already exists in this hotel");
        }
        
        if (request.getRoomNumber() != null) room.setRoomNumber(request.getRoomNumber());
        if (request.getRoomType() != null) room.setRoomType(Room.RoomType.valueOf(request.getRoomType()));
        if (request.getPricePerNight() != null) room.setPricePerNight(request.getPricePerNight());
        if (request.getMaxOccupancy() != null) room.setMaxOccupancy(request.getMaxOccupancy());
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getFacilities() != null) room.setFacilities(request.getFacilities());
        if (request.getFloorNumber() != null) room.setFloorNumber(request.getFloorNumber());
        if (request.getImageUrl() != null) room.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) room.setStatus(Room.RoomStatus.valueOf(request.getStatus()));

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

    @Transactional
    public RoomDto updateRoomStatus(Long id, String status) {
        log.info("Updating room status for room id: {} to {}", id, status);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        room.setStatus(Room.RoomStatus.valueOf(status));
        Room updatedRoom = roomRepository.save(room);
        log.info("Room status updated successfully");
        return convertToDto(updatedRoom);
    }

    private RoomDto convertToDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .hotelName(room.getHotel().getName())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType().name())
                .pricePerNight(room.getPricePerNight())
                .maxOccupancy(room.getMaxOccupancy())
                .description(room.getDescription())
                .facilities(room.getFacilities())
                .floorNumber(room.getFloorNumber())
                .imageUrl(room.getImageUrl())
                .status(room.getStatus().name())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}

