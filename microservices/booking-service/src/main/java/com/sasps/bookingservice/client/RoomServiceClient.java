package com.sasps.bookingservice.client;

import com.sasps.bookingservice.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "room-service", url = "http://room-service:8082")
public interface RoomServiceClient {
    
    @GetMapping("/api/rooms/{id}")
    RoomDto getRoomById(@PathVariable("id") Long id);
    
    @PatchMapping("/api/rooms/{id}/status")
    RoomDto updateRoomStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> request);
}
