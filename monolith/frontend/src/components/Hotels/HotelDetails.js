import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "sweetalert2/dist/sweetalert2.min.css";

const HotelDetails = () => {
  const { id } = useParams();
  const [hotel, setHotel] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchHotel = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/hotels/${id}`);
        const data = await res.json();
        setHotel(data);
      } catch (err) {
        console.error("Error fetching hotel:", err);
      }
    };
    fetchHotel();
  }, [id]);

  const handleDelete = async () => {
    Swal.fire({
      title: "Are you sure?",
      text: "This action will permanently delete the hotel.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#2563eb",
      cancelButtonColor: "#9ca3af",
      confirmButtonText: "Yes, delete it!",
      cancelButtonText: "Cancel",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const res = await fetch(`http://localhost:8080/api/hotels/${id}`, {
            method: "DELETE",
          });

          if (res.ok) {
            await Swal.fire({
              title: "Deleted!",
              text: "The hotel was deleted successfully.",
              icon: "success",
              confirmButtonColor: "#2563eb",
            });
            navigate("/");
          } else {
            Swal.fire({
              title: "Error!",
              text: "Something went wrong while deleting the hotel.",
              icon: "error",
              confirmButtonColor: "#2563eb",
            });
          }
        } catch (err) {
          Swal.fire({
            title: "Connection Error!",
            text: "Could not connect to the server.",
            icon: "error",
            confirmButtonColor: "#2563eb",
          });
        }
      }
    });
  };

  if (!hotel)
    return (
      <div className="flex justify-center items-center h-screen">
        <p className="text-blue-600 text-lg">Loading hotel details...</p>
      </div>
    );

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6">
      <div className="max-w-4xl mx-auto bg-white p-8 rounded-3xl shadow-lg border border-blue-100">
        <img
          src={
            hotel.imageUrl ||
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1000&q=80"
          }
          alt={hotel.name}
          className="rounded-2xl w-full h-80 object-cover mb-6"
        />

        <h1 className="text-3xl font-bold text-blue-700 mb-2">{hotel.name}</h1>
        <p className="text-gray-600 mb-4">
          {hotel.address}, {hotel.city}, {hotel.country}
        </p>
        <p className="text-gray-700 mb-4">{hotel.description}</p>

        <p className="text-sm text-gray-500 mb-2">
          <strong>Amenities:</strong> {hotel.amenities}
        </p>
        <p className="text-sm text-gray-500 mb-2">
          {hotel.starRating} stars — {hotel.availableRooms}/{hotel.totalRooms}{" "}
          rooms available
        </p>
        <p className="text-sm text-gray-500 mb-2">
          {hotel.phoneNumber} | {hotel.email}
        </p>
        <a
          href={`https://${hotel.website}`}
          target="_blank"
          rel="noreferrer"
          className="text-blue-600 underline text-sm"
        >
          {hotel.website}
        </a>

        <div className="flex justify-end gap-4 mt-8">
          <button
            onClick={() => navigate(`/hotel/${id}/edit`)}
            className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
          >
            Edit
          </button>
          <button
            onClick={() => navigate(`/hotel/${id}/rooms`)}
            className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition"
          >
            View Rooms
          </button>
          <button
            onClick={handleDelete}
            className="bg-red-500 text-white px-6 py-2 rounded-lg hover:bg-red-600 transition"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
};

export default HotelDetails;
