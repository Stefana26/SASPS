import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import HotelForm from "./HotelForm";

const AddHotel = () => {
  const [hotel, setHotel] = useState({});
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const BASE_URL = "/api_room_service";

  const handleChange = (e) => {
    const { name, value } = e.target;
    setHotel((prev) => ({ ...prev, [name]: value }));
  };
  const validateForm = () => {
    if (!hotel.name || !hotel.name.trim()) return "Hotel name is required";
    if (!hotel.address || !hotel.address.trim()) return "Address is required";
    if (!hotel.city || !hotel.city.trim()) return "City is required";
    if (!hotel.country || !hotel.country.trim()) return "Country is required";
    if (!hotel.description || !hotel.description.trim())
      return "Description is required";
    if (!hotel.starRating || hotel.starRating < 1 || hotel.starRating > 5)
      return "Star rating must be between 1 and 5";
    if (!hotel.imageUrl || !hotel.imageUrl.trim())
      return "Image URL is required";
    return "";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationError = validateForm();

    if (validationError) {
      Swal.fire({
        title: "Validation Error",
        text: validationError,
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(BASE_URL + "/hotels", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(hotel),
      });

      if (res.ok) {
        await Swal.fire({
          title: "Hotel Created!",
          text: "The hotel has been added successfully.",
          icon: "success",
          confirmButtonText: "Go back to list",
          confirmButtonColor: "#2563eb",
        });
        navigate("/");
      } else {
        Swal.fire({
          title: "Failed to create hotel",
          text: "Please check your input or try again later.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
    } catch (err) {
      console.error(err);
      Swal.fire({
        title: "Network Error",
        text: "Unable to connect to the server. Please try again later.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
    } finally {
      setLoading(false);
    }
  };

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

export default AddHotel;
