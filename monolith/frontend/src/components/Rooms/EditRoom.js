// src/components/Rooms/EditRoom.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import RoomForm from "./RoomForm";

export default function EditRoom() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [room, setRoom] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/rooms/${id}`)
      .then((res) => res.json())
      .then(setRoom)
      .catch((err) => console.error("Error loading room:", err));
  }, [id]);

  const handleEditRoom = async (roomData) => {
    try {
      const response = await fetch(`http://localhost:8080/api/rooms/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(roomData),
      });

      if (response.ok) {
        alert("Room updated successfully!");
        navigate(`/room/${id}`);
      } else {
        alert("Failed to update room.");
      }
    } catch (error) {
      console.error("Error updating room:", error);
    }
  };

  if (!room) return <p>Loading room...</p>;

  return <RoomForm initialData={room} onSubmit={handleEditRoom} submitLabel="Update" />;
}
