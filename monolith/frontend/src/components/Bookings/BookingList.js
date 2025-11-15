import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  StarIcon,
  MapPinIcon,
  MagnifyingGlassIcon,
} from "@heroicons/react/24/solid";

const BookingList = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
//   const [city, setCity] = useState("");
//   const [cities, setCities] = useState([]);
  const navigate = useNavigate();
  const BASE_URL = "http://localhost:8080/api/bookings";

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const res = await fetch(BASE_URL);
      const data = await res.json();
      setBookings(data);
    } catch (err) {
      console.error("Error fetching hotels:", err);
    } finally {
      setLoading(false);
    }
  };

  const searchBooking = async () => {
    if (!search.trim()) {
      fetchBookings(); // show all if no ID typed
      return;
    }

    const id = search.trim();

    setLoading(true);
    try {
      const res = await fetch(`${BASE_URL}/${id}`);

      if (!res.ok) {
        setBookings([]); // no booking found
        return;
      }

      const data = await res.json();

      // backend returns ONE booking, so store it as array
      setBookings([data]);

    } catch (err) {
      console.error("Error searching booking:", err);
      setBookings([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <p className="text-blue-600 text-lg animate-pulse">Loading bookings...</p>
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
            placeholder="Search booking by ID..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1 text-gray-700 focus:outline-none"
          />
        </div>

        <button
          onClick={searchBooking}
          className="bg-blue-600 text-white font-medium px-6 py-2 rounded-full hover:bg-blue-700 transition"
        >
          Search
        </button>
      </div>

      <h1 className="text-4xl font-bold text-center mb-10 text-blue-700">
        Active Bookings
      </h1>

      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 max-w-7xl mx-auto">
        {bookings.length === 0 ? (
          <p className="text-center text-gray-500 col-span-full">
            No bookings found.
          </p>
        ) : (
          bookings.map((booking) => (
            <div
              key={booking.id}
              onClick={() => navigate(`/bookings/${booking.id}`)}
              className="cursor-pointer bg-white rounded-3xl shadow-lg hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 p-6 border border-blue-100"
            >
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold text-blue-700">
                Booking #{booking.id}
                </h2>

                <span
                className={`px-3 py-1 rounded-full text-xs font-semibold 
                    ${
                    booking.status === "PENDING"
                        ? "bg-yellow-100 text-yellow-700"
                        : booking.status === "CONFIRMED"
                        ? "bg-green-100 text-green-700"
                        : booking.status === "CHECKED_IN"
                        ? "bg-blue-100 text-blue-700"
                        : booking.status === "CHECKED_OUT"
                        ? "bg-gray-300 text-gray-800"
                        : "bg-gray-100 text-gray-700"
                    }`}
                >
                {booking.status}
                </span>
            </div>

            {/* Content */}
            <div className="space-y-2">
                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Guest:</span>{" "}
                {booking.userFullName}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Room:</span>{" "}
                {booking.roomNumber}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Check-in:</span>{" "}
                {booking.checkInDate}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Check-out:</span>{" "}
                {booking.checkOutDate}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Guests:</span>{" "}
                {booking.numberOfGuests}
                </p>

            </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default BookingList;
