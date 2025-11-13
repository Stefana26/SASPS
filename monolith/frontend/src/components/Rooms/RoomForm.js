// src/components/Rooms/RoomForm.jsx
import { useState, useEffect } from "react";

export default function RoomForm({ initialData = {}, onSubmit, submitLabel }) {
  const [room, setRoom] = useState({
    hotelId: "",
    roomNumber: "",
    roomType: "SINGLE",
    pricePerNight: "",
    maxOccupancy: "",
    description: "",
    facilities: "",
    floorNumber: "",
    imageUrl: "",
    status: "AVAILABLE",
    ...initialData, // merge any pre-filled data for edit mode
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRoom((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(room);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white p-6 rounded-xl shadow-lg max-w-2xl mx-auto space-y-4"
    >
      <h2 className="text-2xl font-bold text-center mb-4">{submitLabel} Room</h2>

      {/* Hotel ID */}
      <div>
        <label className="block text-gray-700 font-medium">Hotel ID</label>
        <input
          type="number"
          name="hotelId"
          value={room.hotelId}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
      </div>

      {/* Room Number */}
      <div>
        <label className="block text-gray-700 font-medium">Room Number</label>
        <input
          type="text"
          name="roomNumber"
          value={room.roomNumber}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
      </div>

      {/* Room Type */}
      <div>
        <label className="block text-gray-700 font-medium">Room Type</label>
        <select
          name="roomType"
          value={room.roomType}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        >
          <option value="SINGLE">Single</option>
          <option value="DOUBLE">Double</option>
          <option value="TWIN">Twin</option>
          <option value="SUITE">Suite</option>
          <option value="DELUXE">Deluxe</option>
          <option value="PRESIDENTIAL">Presidential</option>
        </select>
      </div>

      {/* Price Per Night */}
      <div>
        <label className="block text-gray-700 font-medium">Price per Night</label>
        <input
          type="number"
          step="0.01"
          name="pricePerNight"
          value={room.pricePerNight}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
      </div>

      {/* Max Occupancy */}
      <div>
        <label className="block text-gray-700 font-medium">Max Occupancy</label>
        <input
          type="number"
          name="maxOccupancy"
          value={room.maxOccupancy}
          onChange={handleChange}
          required
          className="w-full border p-2 rounded"
        />
      </div>

      {/* Description */}
      <div>
        <label className="block text-gray-700 font-medium">Description</label>
        <textarea
          name="description"
          value={room.description}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />
      </div>

      {/* Facilities */}
      <div>
        <label className="block text-gray-700 font-medium">Facilities</label>
        <input
          type="text"
          name="facilities"
          value={room.facilities}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />
      </div>

      {/* Floor Number */}
      <div>
        <label className="block text-gray-700 font-medium">Floor Number</label>
        <input
          type="number"
          name="floorNumber"
          value={room.floorNumber}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />
      </div>

      {/* Status */}
      <div>
        <label className="block text-gray-700 font-medium">Status</label>
        <select
          name="status"
          value={room.status}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        >
          <option value="AVAILABLE">Available</option>
          <option value="RESERVED">Reserved</option>
          <option value="OCCUPIED">Occupied</option>
          <option value="MAINTENANCE">Maintenance</option>
          <option value="OUT_OF_SERVICE">Out of Service</option>
        </select>
      </div>

      {/* Image URL + Preview */}
      <div>
        <label className="block text-gray-700 font-medium">Image URL</label>
        <input
          type="text"
          name="imageUrl"
          value={room.imageUrl}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />
        {room.imageUrl && (
          <img
            src={room.imageUrl}
            alt="Room preview"
            className="mt-3 w-full h-48 object-cover rounded-lg"
          />
        )}
      </div>

      <button
        type="submit"
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
      >
        {submitLabel}
      </button>
    </form>
  );
}
