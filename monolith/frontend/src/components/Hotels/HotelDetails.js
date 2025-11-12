import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { MapPinIcon, PhoneIcon, EnvelopeIcon, GlobeAltIcon, StarIcon } from "@heroicons/react/24/solid";

const HotelDetails = () => {
  const { id } = useParams();
  const [hotel, setHotel] = useState(null);
  const [loading, setLoading] = useState(true);

  const endpoint = `http://localhost:8080/api/hotels/${id}`;

  useEffect(() => {
    const fetchHotel = async () => {
      try {
        const res = await fetch(endpoint);
        const data = await res.json();
        setHotel(data);
      } catch (err) {
        console.error("Error fetching hotel details:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchHotel();
  }, [id]);

  if (loading)
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <p className="text-blue-600 text-lg animate-pulse">
          Loading hotel details...
        </p>
      </div>
    );

  if (!hotel)
    return (
      <div className="text-center mt-20">
        <p className="text-gray-600">Hotel not found.</p>
        <Link
          to="/"
          className="inline-block mt-4 text-blue-600 hover:underline"
        >
          ← Back to list
        </Link>
      </div>
    );

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6">
      <div className="max-w-5xl mx-auto bg-white rounded-3xl shadow-lg p-8 border border-blue-100">
        <div className="flex flex-col md:flex-row gap-8">

          <img
            src={
              hotel.imageUrl ||
              "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1000&q=80"
            }
            alt={hotel.name}
            className="rounded-2xl w-full md:w-1/2 h-72 object-cover shadow-md"
          />

       
          <div className="flex-1">
            <h1 className="text-3xl font-bold text-blue-800 mb-3">
              {hotel.name}
            </h1>

            <div className="flex items-center mb-2 text-gray-600">
              <MapPinIcon className="h-5 w-5 text-blue-500 mr-1" />
              <span>
                {hotel.address}, {hotel.city}, {hotel.country}
              </span>
            </div>

            <div className="flex items-center gap-1 mb-4">
              {[...Array(hotel.starRating)].map((_, i) => (
                <StarIcon key={i} className="h-5 w-5 text-yellow-400" />
              ))}
            </div>

            <p className="text-gray-700 mb-4">{hotel.description}</p>

            <div className="bg-blue-50 rounded-xl p-3 mb-4 text-sm text-gray-700">
              <span className="font-semibold text-blue-700">Amenities:</span>{" "}
              {hotel.amenities}
            </div>

            <div className="space-y-2 text-gray-600 text-sm">
              <p className="flex items-center gap-2">
                <PhoneIcon className="h-4 w-4 text-blue-500" /> {hotel.phoneNumber}
              </p>
              <p className="flex items-center gap-2">
                <EnvelopeIcon className="h-4 w-4 text-blue-500" /> {hotel.email}
              </p>
              <p className="flex items-center gap-2">
                <GlobeAltIcon className="h-4 w-4 text-blue-500" />
                <a
                  href={`https://${hotel.website}`}
                  target="_blank"
                  rel="noreferrer"
                  className="text-blue-600 hover:underline"
                >
                  {hotel.website}
                </a>
              </p>
            </div>

            <p className="mt-4 text-sm text-gray-500">
              Rooms available:{" "}
              <span className="font-medium text-blue-700">
                {hotel.availableRooms}/{hotel.totalRooms}
              </span>
            </p>

            <div className="mt-6 flex gap-3">
              <Link
                to="/"
                className="bg-gray-200 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-300 transition"
              >
                ← Back
              </Link>
              <a
                href={`https://${hotel.website}`}
                target="_blank"
                rel="noreferrer"
                className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 transition"
              >
                Visit Website
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HotelDetails;
