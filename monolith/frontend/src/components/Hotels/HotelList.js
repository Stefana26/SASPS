import React, { useEffect, useState } from "react";
import { StarIcon, MapPinIcon, GlobeAltIcon } from "@heroicons/react/24/solid";
import { useNavigate } from "react-router-dom";

const HotelList = () => {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const endpoint = "http://localhost:8080/api/hotels";

  useEffect(() => {
    const fetchHotels = async () => {
      try {
        const res = await fetch(endpoint);
        const data = await res.json();
        setHotels(data);
      } catch (err) {
        console.error("Error fetching hotels:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchHotels();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <p className="text-blue-600 text-lg animate-pulse">
          Loading hotels...
        </p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6">
      <h1 className="text-5xl font-bold text-center mb-10 text-blue-600 animate-pulse">
        Available Hotels
      </h1>

      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 max-w-7xl mx-auto">
        {hotels.map((hotel) => (
          <div
             onClick={() => navigate(`/hotel/${hotel.id}`)}
            key={hotel.id}
            className="hover:cursor-pointer bg-white rounded-3xl shadow-lg hover:shadow-2xl transition-all duration-300 p-6 border border-blue-100 flex flex-col justify-between"
          >
            <div>
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-xl mx-2 font-semibold text-blue-800">
                  {hotel.name}
                </h2>
                <div className="flex items-center gap-1">
                  {[...Array(hotel.starRating)].map((_, i) => (
                    <StarIcon
                      key={i}
                      className="h-5 w-5 text-yellow-400"
                    />
                  ))}
                </div>
              </div>

              <div className="flex items-center text-gray-500 mb-2">
                <MapPinIcon className="h-4 w-4 text-blue-500 mr-1" />
                <span className="text-sm">
                  {hotel.city}, {hotel.country}
                </span>
              </div>

              <p className="text-gray-700 mb-4 line-clamp-3">
                {hotel.description}
              </p>

              <div className="bg-blue-50 rounded-xl p-3 text-sm text-gray-700 mb-4">
                <span className="font-semibold text-blue-700">Amenities:</span>{" "}
                {hotel.amenities}
              </div>
            </div>

            <div className="mt-4 flex justify-between items-center">
              <a
                href={`https://${hotel.website}`}
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-xl hover:bg-blue-700 transition"
              >
                <GlobeAltIcon className="h-5 w-5" />
                <span>View Details</span>
              </a>

              <p className="text-xs text-gray-400">
                {hotel.availableRooms}/{hotel.totalRooms} rooms available
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default HotelList;
