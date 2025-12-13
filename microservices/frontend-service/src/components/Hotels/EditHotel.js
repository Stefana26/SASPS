import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Swal from "sweetalert2";
import HotelForm from "./HotelForm";

const EditHotel = () => {
  const { id } = useParams();
  const [hotel, setHotel] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const BASE_URL = "/api_room_service";

  useEffect(() => {
    const fetchHotel = async () => {
      try {
        const res = await fetch(`${BASE_URL}/${id}`);
        const data = await res.json();
        setHotel(data);
      } catch (err) {
        Swal.fire({
          title: "Error",
          text: "Failed to load hotel details.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
    };
    fetchHotel();
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setHotel((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(hotel),
      });
      if (res.ok) {
        await Swal.fire({
          title: "Updated Successfully",
          text: "Hotel information has been updated.",
          icon: "success",
          confirmButtonColor: "#2563eb",
        });
        navigate(`/hotel/${id}`);
      } else {
        Swal.fire({
          title: "Update Failed",
          text: "Please check your input and try again.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
    } catch {
      Swal.fire({
        title: "Network Error",
        text: "Could not connect to the server.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
    } finally {
      setLoading(false);
    }
  };

  if (!hotel)
    return (
      <div className="flex justify-center items-center h-screen">
        <p className="text-blue-600 text-lg">Loading hotel...</p>
      </div>
    );

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6 rounded-3xl">
      <div className="max-w-3xl mx-auto">
        <HotelForm
          hotel={hotel}
          onChange={handleChange}
          onSubmit={handleSubmit}
          loading={loading}
        />
      </div>
    </div>
  );
};

export default EditHotel;
