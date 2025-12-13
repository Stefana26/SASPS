import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "sweetalert2/dist/sweetalert2.min.css";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!username.trim() || !password.trim()) {
      Swal.fire({
        icon: "warning",
        title: "Missing fields",
        text: "Please enter both username/email and password.",
        confirmButtonColor: "#2563eb",
      });
      return;
    }

    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (res.ok) {
        const data = await res.json();
        // Store user data in localStorage
        localStorage.setItem("user", JSON.stringify(data));
        
        // Dispatch custom event to notify Navbar
        window.dispatchEvent(new Event("userLoggedIn"));
        
        Swal.fire({
          icon: "success",
          title: "Login successful!",
          text: `Welcome back, ${data.firstName}!`,
          showConfirmButton: false,
          timer: 1500,
        });
        navigate("/");
      } else {
        const errorData = await res.json();
        Swal.fire({
          icon: "error",
          title: "Login failed",
          text: errorData.message || "Please check your username or password.",
          confirmButtonColor: "#2563eb",
        });
      }
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Connection error",
        text: "Unable to connect to the server.",
        confirmButtonColor: "#2563eb",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col md:flex-row rounded-3xl">
      <div className="rounded-3xl flex-1 flex flex-col justify-center items-center bg-gradient-to-br from-blue-50 via-white to-blue-100 px-10 py-12">
        <div className="w-full max-w-md">
          <h2 className="text-4xl font-bold text-blue-700 mb-3">Welcome to HotelHub</h2>
          <p className="text-gray-500 mb-8">
            Sign in to your account to manage hotels and bookings.
          </p>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Username or Email
              </label>
              <input
                type="text"
                placeholder="username or email"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full border border-gray-300 rounded-full px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Password
              </label>
              <input
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full border border-gray-300 rounded-full px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 text-white font-medium py-2 rounded-full hover:bg-blue-700 transition disabled:opacity-50"
            >
              {loading ? "Signing in..." : "Login"}
            </button>
          </form>

          <p className="text-center text-gray-500 text-sm mt-6">
            Don't have an account?{" "}
            <span
              onClick={() => navigate("/signup")}
              className="text-blue-600 hover:underline cursor-pointer"
            >
              Sign Up
            </span>
          </p>
        </div>
      </div>

      <div className="hidden md:flex flex-1">
        <img
          src="https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTB8fGhvdGVsfGVufDB8fDB8fHww&fm=jpg&q=60&w=3000"
          alt="Hotel"
          className="w-full h-full object-cover"
        />
      </div>
    </div>
  );
};

export default Login;
