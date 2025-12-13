// src/components/Rooms/RoomDetails.jsx
import { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

export default function RoomDetails() {
  const { roomId } = useParams(); // Changed from 'id' to 'roomId'
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const [showBookingForm, setShowBookingForm] = useState(false);
  const BASE_URL = "/api_room_service";
  const [bookingData, setBookingData] = useState({
    checkInDate: "",
    checkOutDate: "",
    numberOfGuests: 1,
    specialRequests: "",
    paymentMethod: "CREDIT_CARD",
  });
  const navigate = useNavigate();

  useEffect(() => {
    // Get logged-in user
    const userData = localStorage.getItem("user");
    if (userData) {
      setUser(JSON.parse(userData));
    }

    const fetchRoom = async () => {
      try {
        const response = await fetch(
          BASE_URL + `/rooms/${roomId}`
        );
        if (!response.ok) throw new Error("Room not found");
        const data = await response.json();
        setRoom(data);
      } catch (error) {
        console.error("Error fetching room:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchRoom();
  }, [roomId]); // Changed from 'id' to 'roomId'

  const handleBookRoom = async () => {
    if (!bookingData.checkInDate || !bookingData.checkOutDate) {
      Swal.fire({
        icon: "warning",
        title: "Missing Information",
        text: "Please select check-in and check-out dates.",
        confirmButtonColor: "#2563eb",
      });
      return;
    }

    const booking = {
      userId: user.id,
      roomId: room.id,
      checkInDate: bookingData.checkInDate,
      checkOutDate: bookingData.checkOutDate,
      numberOfGuests: bookingData.numberOfGuests,
      paymentMethod: "CREDIT_CARD",
      specialRequests: bookingData.specialRequests,
    };

    try {
      const response = await fetch("http://localhost:8080/api/bookings", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(booking),
      });

      if (response.ok) {
        const bookingResult = await response.json();
        Swal.fire({
          icon: "success",
          title: "Booking Successful!",
          html: `
            <div class="text-left">
              <p class="mb-2"><strong>Confirmation Number:</strong> ${bookingResult.confirmationNumber}</p>
              <p class="mb-2"><strong>Total Paid:</strong> $${bookingResult.totalPrice}</p>
              <p class="mb-2"><strong>Payment Method:</strong> ${bookingResult.paymentMethod}</p>
              <p class="mt-4 text-green-600">✓ Payment confirmed - Your reservation is confirmed!</p>
            </div>
          `,
          confirmButtonColor: "#2563eb",
        }).then(() => {
          navigate(`/bookings`);
        });
      } else {
        const errorData = await response.json();
        Swal.fire({
          icon: "error",
          title: "Booking Failed",
          text:
            errorData.message ||
            "Unable to complete the booking. Please try again.",
          confirmButtonColor: "#2563eb",
        });
      }
    } catch (error) {
      console.error("Error booking room:", error);
      Swal.fire({
        icon: "error",
        title: "Connection Error",
        text: "Unable to connect to the server.",
        confirmButtonColor: "#2563eb",
      });
    }
  };

  if (loading) return <p>Loading room details...</p>;
  if (!room) return <p>Room not found.</p>;

  return (
    <div className="bg-white shadow-lg rounded-2xl p-6 max-w-2xl mx-auto">
      <h2 className="text-3xl font-bold mb-3 text-blue-700">
        Room {room.roomNumber} — {room.roomType}
      </h2>
      <p className="text-gray-600 mb-4">{room.description}</p>

      <div className="space-y-2 mb-6">
        <p>
          <b>Price per Night:</b>{" "}
          <span className="text-green-600 text-xl font-bold">
            ${room.pricePerNight}
          </span>
        </p>
        <p>
          <b>Max Occupancy:</b> {room.maxOccupancy} guests
        </p>
        <p>
          <b>Floor:</b> {room.floorNumber}
        </p>
        <p>
          <b>Status:</b>{" "}
          <span
            className={`font-semibold ${
              room.status === "AVAILABLE" ? "text-green-600" : "text-red-600"
            }`}
          >
            {room.status}
          </span>
        </p>
        <p>
          <b>Facilities:</b> {room.facilities}
        </p>
      </div>

      {/* Booking Form - Only for CUSTOMER users */}
      {user && user.role === "CUSTOMER" && room.status === "AVAILABLE" && (
        <>
          {!showBookingForm ? (
            <button
              onClick={() => setShowBookingForm(true)}
              className="w-full bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition font-semibold mb-4"
            >
              Reserve This Room
            </button>
          ) : (
            <div className="border border-blue-200 rounded-lg p-6 mb-4 bg-blue-50">
              <h3 className="text-xl font-bold mb-4 text-blue-700">
                Complete Your Reservation
              </h3>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Check-in Date *
                  </label>
                  <input
                    type="date"
                    value={bookingData.checkInDate}
                    onChange={(e) =>
                      setBookingData({
                        ...bookingData,
                        checkInDate: e.target.value,
                      })
                    }
                    min={new Date().toISOString().split("T")[0]}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Check-out Date *
                  </label>
                  <input
                    type="date"
                    value={bookingData.checkOutDate}
                    onChange={(e) =>
                      setBookingData({
                        ...bookingData,
                        checkOutDate: e.target.value,
                      })
                    }
                    min={
                      bookingData.checkInDate ||
                      new Date().toISOString().split("T")[0]
                    }
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Number of Guests *
                  </label>
                  <input
                    type="number"
                    min="1"
                    max={room.maxOccupancy}
                    value={bookingData.numberOfGuests}
                    onChange={(e) =>
                      setBookingData({
                        ...bookingData,
                        numberOfGuests: parseInt(e.target.value),
                      })
                    }
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Special Requests (Optional)
                  </label>
                  <textarea
                    value={bookingData.specialRequests}
                    onChange={(e) =>
                      setBookingData({
                        ...bookingData,
                        specialRequests: e.target.value,
                      })
                    }
                    rows="3"
                    placeholder="Any special requirements..."
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Payment Method *
                  </label>
                  <select
                    value={bookingData.paymentMethod}
                    onChange={(e) =>
                      setBookingData({
                        ...bookingData,
                        paymentMethod: e.target.value,
                      })
                    }
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  >
                    <option value="CREDIT_CARD">Credit Card</option>
                    <option value="DEBIT_CARD">Debit Card</option>
                    <option value="CASH">Cash</option>
                    <option value="BANK_TRANSFER">Bank Transfer</option>
                  </select>
                </div>

                {/* Price Calculation */}
                {bookingData.checkInDate && bookingData.checkOutDate && (
                  <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                    <p className="text-sm text-gray-600">
                      <b>Number of Nights:</b>{" "}
                      {Math.ceil(
                        (new Date(bookingData.checkOutDate) -
                          new Date(bookingData.checkInDate)) /
                          (1000 * 60 * 60 * 24)
                      )}
                    </p>
                    <p className="text-lg font-bold text-green-700 mt-2">
                      Total Price: $
                      {(
                        room.pricePerNight *
                        Math.ceil(
                          (new Date(bookingData.checkOutDate) -
                            new Date(bookingData.checkInDate)) /
                            (1000 * 60 * 60 * 24)
                        )
                      ).toFixed(2)}
                    </p>
                  </div>
                )}

                <div className="flex gap-4">
                  <button
                    onClick={handleBookRoom}
                    className="flex-1 bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition font-semibold"
                  >
                    Confirm & Pay
                  </button>
                  <button
                    onClick={() => setShowBookingForm(false)}
                    className="px-6 py-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition font-semibold"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {/* Message for non-logged in users */}
      {!user && room.status === "AVAILABLE" && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
          <p className="text-yellow-800">
            Please{" "}
            <a href="/login" className="text-blue-600 font-semibold underline">
              log in
            </a>{" "}
            as a user to make a reservation.
          </p>
        </div>
      )}

      {/* Message for admin users */}
      {user && user.role === "ADMIN" && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
          <p className="text-blue-800">
            Admin accounts cannot make reservations. This feature is for
            customers only.
          </p>
        </div>
      )}

      <div className="mt-6">
        <Link
          to={`/hotel/${room.hotelId}/rooms`}
          className="inline-block px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition font-semibold"
        >
          Back to Rooms
        </Link>
      </div>
    </div>
  );
}
