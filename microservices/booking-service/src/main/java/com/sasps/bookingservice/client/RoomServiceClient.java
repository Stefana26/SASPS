package com.sasps.bookingservice.client;

import com.sasps.bookingservice.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "room-service", url = "${room-service.url:http://room-service:8082}")
public interface RoomServiceClient {
    
    @GetMapping("/api/rooms/{id}")
    RoomDto getRoomById(@PathVariable("id") Long id);
    
    @PutMapping("/api/rooms/{id}/status")
    RoomDto updateRoomStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
}
