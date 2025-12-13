import React from "react";

const HotelForm = ({ hotel, onChange, onSubmit, loading }) => {
  return (
    <form
      onSubmit={onSubmit}
      className="bg-white shadow-xl rounded-3xl p-8 border border-blue-100 max-w-3xl mx-auto"
    >
      <h2 className="text-2xl font-bold text-blue-700 mb-6 text-center">
        Add New Hotel
      </h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            type="text"
            name="name"
            value={hotel.name || ""}
            onChange={onChange}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Star Rating (1–5)
          </label>
          <input
            type="number"
            name="starRating"
            value={hotel.starRating || ""}
            onChange={onChange}
            min="1"
            max="5"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="md:col-span-2">
          <label className="block text-sm font-medium text-gray-700">
            Address
          </label>
          <input
            type="text"
            name="address"
            value={hotel.address || ""}
            onChange={onChange}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            City
          </label>
          <input
            type="text"
            name="city"
            value={hotel.city || ""}
            onChange={onChange}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Country
          </label>
          <input
            type="text"
            name="country"
            value={hotel.country || ""}
            onChange={onChange}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Postal Code
          </label>
          <input
            type="text"
            name="postalCode"
            value={hotel.postalCode || ""}
            onChange={onChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Phone Number
          </label>
          <input
            type="text"
            name="phoneNumber"
            value={hotel.phoneNumber || ""}
            onChange={onChange}
            required
            placeholder="+40211234567"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Email
          </label>
          <input
            type="email"
            name="email"
            value={hotel.email || ""}
            onChange={onChange}
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Website
          </label>
          <input
            type="url"
            name="website"
            value={hotel.website || ""}
            onChange={onChange}
            required
            placeholder="https://www.example.com"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Total Rooms
          </label>
          <input
            type="number"
            name="totalRooms"
            value={hotel.totalRooms || ""}
            onChange={onChange}
            min="1"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Available Rooms
          </label>
          <input
            type="number"
            name="availableRooms"
            value={hotel.availableRooms || ""}
            onChange={onChange}
            min="0"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="md:col-span-2">
          <label className="block text-sm font-medium text-gray-700">
            Description
          </label>
          <textarea
            name="description"
            value={hotel.description || ""}
            onChange={onChange}
            rows="3"
            required
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          ></textarea>
        </div>

        <div className="md:col-span-2">
          <label className="block text-sm font-medium text-gray-700">
            Amenities
          </label>
          <input
            type="text"
            name="amenities"
            value={hotel.amenities || ""}
            onChange={onChange}
            placeholder="WiFi, Pool, SPA"
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="md:col-span-2">
          <label className="block text-sm font-medium text-gray-700">
            Image URL
          </label>
          <input
            type="url"
            name="imageUrl"
            placeholder="https://example.com/hotel.jpg"
            value={hotel.imageUrl || ""}
            onChange={onChange}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500"
          />
          {hotel.imageUrl && (
            <img
              src={hotel.imageUrl}
              alt="Preview"
              className="mt-3 w-full h-64 object-cover rounded-lg shadow-md"
            />
          )}
        </div>
      </div>

      <div className="flex justify-end mt-8">
        <button
          type="submit"
          disabled={loading}
          className="bg-blue-600 text-white font-medium px-6 py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
        >
          {loading ? "Saving..." : "Create Hotel"}
        </button>
      </div>
    </form>
  );
};

export default HotelForm;
