import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "sweetalert2/dist/sweetalert2.min.css";

const BookingDetails = () => {
  const { id } = useParams();
  const [booking, setBooking] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/bookings/${id}`);
        const data = await res.json();
        setBooking(data);
      } catch (err) {
        console.error("Error fetching booking:", err);
      }
    };
    fetchBooking();
  }, [id]);

  const handleDelete = async () => {
    Swal.fire({
      title: "Are you sure?",
      text: "This action will permanently delete the booking.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#2563eb",
      cancelButtonColor: "#9ca3af",
      confirmButtonText: "Yes, delete it!",
      cancelButtonText: "Cancel",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const res = await fetch(`http://localhost:8080/api/bookings/${id}`, {
            method: "DELETE",
          });

          if (res.ok) {
            await Swal.fire({
              title: "Deleted!",
              text: "The booking was deleted successfully.",
              icon: "success",
              confirmButtonColor: "#2563eb",
            });
            navigate("/bookings");
          } else {
            Swal.fire({
              title: "Error!",
              text: "Something went wrong while deleting the booking.",
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

  const handleConfirm = async () => {
    // Step 1 — Ask Payment Info (Cleaner UI)
    const { value: formValues } = await Swal.fire({
      title: "Payment Required",
      showCancelButton: true,
      confirmButtonColor: "#2563eb",
      cancelButtonColor: "#9ca3af",
      confirmButtonText: "Continue",
      html: `
        <div class="flex flex-col gap-4 text-left">
          <div>
            <label class="font-semibold">Payment Amount:</label>
            <input id="payment-amount" type="number" min="0" class="swal2-input" placeholder="Enter amount">
          </div>
          <div>
            <label class="font-semibold">Payment Method:</label>
            <input id="payment-method" class="swal2-input" placeholder="Cash / Card / POS">
          </div>
        </div>
      `,
      focusConfirm: false,
      preConfirm: () => {
        const amount = document.getElementById("payment-amount").value;
        const method = document.getElementById("payment-method").value;
  
        if (!amount || !method) {
          Swal.showValidationMessage("Please enter payment amount and method");
          return false;
        }
  
        return { paymentAmount: Number(amount), paymentMethod: method };
      },
    });
  
    if (!formValues) return;
  
    // Step 2 — Confirm action
    const result = await Swal.fire({
      title: "Confirm Booking?",
      text: "After confirmation no details can be modified.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#2563eb",
      cancelButtonColor: "#9ca3af",
      confirmButtonText: "Yes, confirm!",
    });
  
    if (!result.isConfirmed) return;
  
    // Step 3 — Send API request
    try {
      const res = await fetch(
        `http://localhost:8080/api/bookings/${id}/confirm`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formValues),
        }
      );
  
      if (res.status === 200) {
        await Swal.fire({
          title: "Confirmed!",
          text: "The booking was confirmed successfully.",
          icon: "success",
          confirmButtonColor: "#2563eb",
        });
  
        // 🔥 FIX: reload the page to update state
        return window.location.reload();
      }
  
      if (res.status === 400) {
        return Swal.fire({
          title: "Cannot Confirm",
          text: "Only PENDING bookings can be confirmed.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
  
      if (res.status === 404) {
        return Swal.fire({
          title: "Not Found",
          text: "Booking not found.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
  
      Swal.fire({
        title: "Error",
        text: "Unexpected error occurred.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
  
    } catch (err) {
      Swal.fire({
        title: "Connection Error",
        text: "Cannot connect to server.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
    }
  };
  

  const handleCheckIn = async () => {
    const result = await Swal.fire({
      title: "Check-In Guest?",
      text: "This will mark the guest as checked in.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#2563eb",
      cancelButtonColor: "#9ca3af",
      confirmButtonText: "Yes, check in!",
    });
  
    if (!result.isConfirmed) return;
  
    try {
      const res = await fetch(
        `http://localhost:8080/api/bookings/${id}/check-in`,
        {
          method: "POST",
        }
      );
  
      // 200 — success
      if (res.status === 200) {
        await Swal.fire({
          title: "Checked In!",
          text: "The guest has been checked in successfully.",
          icon: "success",
          confirmButtonColor: "#2563eb",
        });
        return window.location.reload();
      }
  
      // 400 — Business rule violation (wrong status or date)
      if (res.status === 400) {
        const errorData = await res.json();
        return Swal.fire({
          title: "Cannot Check In",
          text: errorData.message || "Only bookings with status 'CONFIRMED' and check-in date of today or earlier can be checked in.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
  
      // 404 — Booking not found
      if (res.status === 404) {
        return Swal.fire({
          title: "Not Found",
          text: "Booking not found.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
  
      Swal.fire({
        title: "Error",
        text: "An unexpected error occurred.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
  
    } catch (error) {
      Swal.fire({
        title: "Connection Error",
        text: "Cannot connect to server.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
    }
  };

  const handleCheckOut = async () => {
    const result = await Swal.fire({
      title: "Check-Out Guest?",
      text: "This will complete the stay and check the guest out.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#2563eb",
      cancelButtonColor: "#9ca3af",
      confirmButtonText: "Yes, check out!",
    });
  
    if (!result.isConfirmed) return;
  
    try {
      const res = await fetch(
        `http://localhost:8080/api/bookings/${id}/check-out`,
        {
          method: "POST",
        }
      );
  
      // 200 — success
      if (res.status === 200) {
        await Swal.fire({
          title: "Checked Out!",
          text: "The booking was checked out successfully.",
          icon: "success",
          confirmButtonColor: "#2563eb",
        });
        return window.location.reload();
      }
  
      // 400 — Only checked-in bookings can be checked out
      if (res.status === 400) {
        const errorData = await res.json();
        return Swal.fire({
          title: "Cannot Check Out",
          text: errorData.message || "Only bookings with status 'CHECKED_IN' can be checked out.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
  
      // 404 — Booking not found
      if (res.status === 404) {
        return Swal.fire({
          title: "Not Found",
          text: "Booking not found.",
          icon: "error",
          confirmButtonColor: "#2563eb",
        });
      }
  
      Swal.fire({
        title: "Error",
        text: "An unexpected error occurred.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
  
    } catch (error) {
      Swal.fire({
        title: "Connection Error",
        text: "Cannot connect to server.",
        icon: "error",
        confirmButtonColor: "#2563eb",
      });
    }
  };
  

  if (!booking)
    return (
      <div className="flex justify-center items-center h-screen">
        <p className="text-blue-600 text-lg">Loading booking details...</p>
      </div>
    );

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6">
      <div className="max-w-4xl mx-auto bg-white p-8 rounded-3xl shadow-lg border border-blue-100">
        <img
          src={
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1000&q=80"
          }
          alt={booking.id}
          className="rounded-2xl w-full h-80 object-cover mb-6"
        />

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
                <span className="font-semibold text-blue-600">Room ID:</span>{" "}
                {booking.roomId}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Room Number:</span>{" "}
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

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Total price:</span>{" "}
                {booking.totalPrice}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Paid amount:</span>{" "}
                {booking.paidAmount}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-blue-600">Number of nights:</span>{" "}
                {booking.numberOfNights}
                </p>

                <p className="text-gray-700 text-sm">
                <span className="font-semibold text-red-600">Special requests:</span>{" "}
                {booking.specialRequests}
                </p>

            </div>
            <div className="flex justify-between items-center mt-8">

                {/* Left group: Check-In / Check-Out (Admin actions) */}
                <div className="flex gap-4">
                <button
                    onClick={handleCheckIn}
                    disabled={booking.status !== "CONFIRMED"}
                    className={`px-6 py-2 rounded-lg transition ${
                      booking.status === "CONFIRMED" 
                        ? "bg-blue-600 text-white hover:bg-blue-700" 
                        : "bg-gray-300 text-gray-500 cursor-not-allowed"
                    }`}
                    title={booking.status !== "CONFIRMED" ? "Can only check in CONFIRMED bookings" : ""}
                >
                    Check-In
                </button>

                <button
                    onClick={handleCheckOut}
                    disabled={booking.status !== "CHECKED_IN"}
                    className={`px-6 py-2 rounded-lg transition ${
                      booking.status === "CHECKED_IN" 
                        ? "bg-green-600 text-white hover:bg-green-700" 
                        : "bg-gray-300 text-gray-500 cursor-not-allowed"
                    }`}
                    title={booking.status !== "CHECKED_IN" ? "Can only check out CHECKED_IN bookings" : ""}
                >
                    Check-Out
                </button>
                </div>

                {/* Right group: Delete only */}
                <div className="flex gap-4">
                <button
                    onClick={handleDelete}
                    className="bg-red-600 text-white px-6 py-2 rounded-lg hover:bg-red-700 transition"
                >
                    Delete
                </button>
            </div>
        </div>
      </div>
    </div>
  );
};

export default BookingDetails;
