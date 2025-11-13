// src/components/Rooms/RoomDetails.jsx
import { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";

export default function RoomDetails() {
  const { id } = useParams(); // Assuming /room/:id route
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRoom = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/rooms/${id}`);
        if (!response.ok) throw new Error("Room not found");
        const data = await response.json();
        setRoom(data);
      } catch (error) {
        console.error("Error fetching room:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchRoom();
  }, [id]);

  const handleBookRoom = async () => {
    // Example booking body
    const booking = {
      userId: 1, // Replace with logged-in user
      roomId: room.id,
      checkInDate: "2025-12-01",
      checkOutDate: "2025-12-05",
      numberOfGuests: 2,
      paymentMethod: "CREDIT_CARD",
      specialRequests: "Late check-in",
    };

    try {
      const response = await fetch("http://localhost:8080/api/bookings", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(booking),
      });

      if (response.ok) {
        alert("Booking successful!");
        navigate(`/bookings`);
      } else {
        alert("Failed to book room.");
      }
    } catch (error) {
      console.error("Error booking room:", error);
    }
  };

  if (loading) return <p>Loading room details...</p>;
  if (!room) return <p>Room not found.</p>;

  return (
    <div className="bg-white shadow-lg rounded-2xl p-6 max-w-xl mx-auto">
      <h2 className="text-2xl font-bold mb-3">{room.roomNumber} — {room.roomType}</h2>
      <p className="text-gray-600 mb-4">{room.description}</p>

      <div className="space-y-2">
        <p><b>Price per Night:</b> ${room.pricePerNight}</p>
        <p><b>Max Occupancy:</b> {room.maxOccupancy}</p>
        <p><b>Floor:</b> {room.floorNumber}</p>
        <p><b>Status:</b> {room.status}</p>
        <p><b>Facilities:</b> {room.facilities}</p>
      </div>

      <div className="mt-6 flex gap-4">
        <button
          onClick={handleBookRoom}
          disabled={room.status !== "AVAILABLE"}
          className={`px-4 py-2 rounded text-white ${
            room.status === "AVAILABLE" ? "bg-green-600 hover:bg-green-700" : "bg-gray-400"
          }`}
        >
          {room.status === "AVAILABLE" ? "Book Room" : "Unavailable"}
        </button>

        <Link
          to={`/hotel/${room.hotelId}/rooms`}
          className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded"
        >
          Back
        </Link>
      </div>
    </div>
  );
}
