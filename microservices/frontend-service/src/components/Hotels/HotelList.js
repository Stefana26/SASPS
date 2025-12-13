import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  StarIcon,
  MapPinIcon,
  MagnifyingGlassIcon,
} from "@heroicons/react/24/solid";

const HotelList = () => {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [city, setCity] = useState("");
  const [cities, setCities] = useState([]);
  const [user, setUser] = useState(null);
  const navigate = useNavigate();
  const BASE_URL = "/api/hotels";
  //const BASE_URL = "http://localhost:8080/api/hotels";

  useEffect(() => {
    // Check if user is logged in and get their role
    const userData = localStorage.getItem("user");
    if (userData) {
      setUser(JSON.parse(userData));
    }
  }, []);

  const fetchHotels = async () => {
    setLoading(true);
    try {
      const res = await fetch('/api/hotels');
      const data = await res.json();
      setHotels(data);
      const cityList = [...new Set(data.map((h) => h.city))];
      setCities(cityList);
    } catch (err) {
      console.error("Error fetching hotels:", err);
    } finally {
      setLoading(false);
    }
  };

  const searchHotels = async () => {
    setLoading(true);
    try {
      if (city) {
        const res = await fetch(`${BASE_URL}/city/${city}`);
        const data = await res.json();
        setHotels(data);
      } else if (search.trim()) {
        const body = {
          searchTerm: search.trim(),
          city: null,
          country: null,
          minStarRating: null,
          onlyWithAvailableRooms: false,
        };

        const res = await fetch(`${BASE_URL}/search`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(body),
        });

        const data = await res.json();
        setHotels(data);
      } else {
        fetchHotels();
      }
    } catch (err) {
      console.error("Error searching hotels:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHotels();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <p className="text-blue-600 text-lg animate-pulse">Loading hotels...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6 rounded-3xl">
      <div className="max-w-5xl mx-auto bg-white rounded-3xl shadow-md border border-blue-100 p-4 flex flex-col md:flex-row md:items-center gap-4 mb-6">
        <div className="flex items-center flex-1 border border-blue-100 rounded-full px-3 py-2">
          <MagnifyingGlassIcon className="h-5 w-5 text-blue-500 mr-2" />
          <input
            type="text"
            placeholder="Search hotels..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1 text-gray-700 focus:outline-none"
          />
        </div>
        <div className="relative">
          <select
            value={city}
            onChange={(e) => setCity(e.target.value)}
            className="appearance-none border border-blue-200 bg-white rounded-full pl-10 pr-10 py-2 text-gray-700 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 shadow-sm hover:border-blue-400 transition w-48"
          >
            <option value="">All cities</option>
            {cities.map((c, i) => (
              <option key={i} value={c}>
                {c}
              </option>
            ))}
          </select>

          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-4 w-4 text-blue-500 absolute left-3 top-1/2 transform -translate-y-1/2 pointer-events-none"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 11c1.104 0 2-.896 2-2s-.896-2-2-2-2 .896-2 2 .896 2 2 2z"
            />
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 22s8-4.438 8-12a8 8 0 10-16 0c0 7.562 8 12 8 12z"
            />
          </svg>

          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-4 w-4 text-gray-500 absolute right-3 top-1/2 transform -translate-y-1/2 pointer-events-none"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 9l-7 7-7-7"
            />
          </svg>
        </div>

        <button
          onClick={searchHotels}
          className="bg-blue-600 text-white font-medium px-6 py-2 rounded-full hover:bg-blue-700 transition"
        >
          Search
        </button>
      </div>

      {/* Only show Add Hotel button for ADMIN users */}
      {user && user.role === "ADMIN" && (
        <div className="flex justify-end mb-6">
          <button
            onClick={() => navigate("/hotel/new")}
            className="bg-blue-600 text-white px-4 py-2 rounded-3xl hover:bg-blue-700 transition"
          >
            Add Hotel
          </button>
        </div>
      )}

      <h1 className="text-4xl font-bold text-center mb-10 text-blue-700">
        Available Hotels
      </h1>

      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 max-w-7xl mx-auto">
        {hotels.length === 0 ? (
          <p className="text-center text-gray-500 col-span-full">
            No hotels found.
          </p>
        ) : (
          hotels.map((hotel) => (
            <div
              key={hotel.id}
              onClick={() => navigate(`/hotel/${hotel.id}`)}
              className="cursor-pointer bg-white rounded-3xl shadow-lg hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 p-6 border border-blue-100"
            >
              <img
                src={
                  hotel.imageUrl ||
                  "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1000&q=80"
                }
                alt={hotel.name}
                className="rounded-2xl w-full h-56 object-cover mb-4"
              />
              <h2 className="text-xl font-semibold text-blue-800 mb-1">
                {hotel.name}
              </h2>
              <div className="flex items-center text-gray-500 mb-2">
                <MapPinIcon className="h-4 w-4 text-blue-500 mr-1" />
                <span className="text-sm">
                  {hotel.city}, {hotel.country}
                </span>
              </div>
              <p className="text-gray-700 text-sm mb-3 line-clamp-3">
                {hotel.description}
              </p>
              <div className="flex items-center gap-1 text-yellow-500 mb-3">
                {[...Array(hotel.starRating)].map((_, i) => (
                  <StarIcon key={i} className="h-4 w-4" />
                ))}
              </div>
              <div className="bg-blue-50 rounded-xl p-2 text-sm text-gray-700">
                <span className="font-semibold text-blue-700">Amenities:</span>{" "}
                {hotel.amenities}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default HotelList;
