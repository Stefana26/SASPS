// src/components/Rooms/AddRoom.jsx
import { useNavigate } from "react-router-dom";
import RoomForm from "./RoomForm";

export default function AddRoom() {
  const navigate = useNavigate();
   const BASE_URL = "/api_room_service";

  const handleAddRoom = async (roomData) => {
    try {
      const response = await fetch(BASE_URL + "/rooms", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(roomData),
      });

      if (response.ok) {
        alert("Room added successfully!");
        navigate(`/hotel/${roomData.hotelId}`);
      } else {
        const errorText = await response.text();
        console.error("Add failed:", errorText);
        alert("Failed to add room.");
      }
    } catch (error) {
      console.error("Error adding room:", error);
      alert("Error adding room.");
    }
  };

  return <RoomForm onSubmit={handleAddRoom} submitLabel="Add" />;
}
