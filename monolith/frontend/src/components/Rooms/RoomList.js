import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

export default function RoomList() {
  const { hotelId } = useParams();
  const [rooms, setRooms] = useState([]);

  useEffect(() => {
    fetch(`/api/hotels/${hotelId}/rooms`)
      .then((res) => res.json())
      .then((data) => setRooms(data))
      .catch(console.error);
  }, [hotelId]);

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Rooms for Hotel #{hotelId}</h2>
      <Link to={`/hotel/${hotelId}/rooms/new`} className="text-blue-600 underline">
         Add New Room
      </Link>
      <ul className="mt-4 space-y-2">
        {rooms.map((room) => (
          <li key={room.id} className="p-3 border rounded bg-white shadow">
            <Link to={`/hotel/${hotelId}/rooms/${room.id}`} className="font-semibold">
              {room.name}
            </Link>{" "}
            - {room.type} | {room.price}$/night
          </li>
        ))}
      </ul>
    </div>
  );
}
