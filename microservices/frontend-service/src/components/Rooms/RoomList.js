import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { FaBed } from "react-icons/fa"; // Room Icon

const RoomList = () => {
  const { id: hotelId } = useParams();
  const [rooms, setRooms] = useState([]);
  const [user, setUser] = useState(null);
  const navigate = useNavigate();
  const BASE_URL = "/api_room_service";

  useEffect(() => {
    // Check if user is logged in and get their role
    const userData = localStorage.getItem("user");
    if (userData) {
      setUser(JSON.parse(userData));
    }
    
    const fetchRooms = async () => {
      try {
        const res = await fetch(BASE_URL + `/rooms/hotel/${hotelId}`);
        if (!res.ok) {
          throw new Error('Failed to fetch rooms');
        }
        const data = await res.json();
        setRooms(data);
      } catch (error) {
        console.error("Error fetching rooms:", error);
        setRooms([]);
      }
    };

    if (hotelId) {
      fetchRooms();
    }
  }, [hotelId]);

  return (
    <div className="max-w-5xl mx-auto mt-12 bg-white p-6 rounded-2xl shadow">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-3xl font-bold text-blue-700">Rooms</h2>

        {/* Add Room Button - Only for ADMIN */}
        {user && user.role === "ADMIN" && (
          <button
            onClick={() => navigate(`/hotel/${hotelId}/room/new`)}
            className="bg-green-600 text-white px-5 py-2 rounded-lg hover:bg-green-700 transition"
          >
           Add Room
          </button>
        )}
      </div>

      {/* Room Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {rooms.map(room => (
          <div
            key={room.id}
            className="border rounded-xl shadow hover:shadow-lg transition p-5 flex items-center gap-4"
          >
            {/* ICON INSTEAD OF IMAGE */}
            <div className="bg-blue-100 p-4 rounded-full">
              <FaBed size={35} className="text-blue-600" />
            </div>

            <div className="flex-1">
              <h3 className="text-xl font-semibold text-blue-600">
                Room {room.roomNumber}
              </h3>

              <p className="text-gray-600">
                Type: {room.roomType} • Status: {room.status}
              </p>

              <p className="text-gray-700 mt-1">
                Price: <strong>${room.pricePerNight}</strong> / night
              </p>

              <button
                onClick={() => navigate(`/hotel/${hotelId}/rooms/${room.id}`)}
                className="mt-3 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition"
              >
                View Details & Reserve
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RoomList;
