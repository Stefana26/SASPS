import { useState, useEffect } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { Bars3Icon, XMarkIcon, UserCircleIcon, ShieldCheckIcon } from "@heroicons/react/24/outline";

const Navbar = () => {
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  // Function to check and update user state
  const checkUser = () => {
    const userData = localStorage.getItem("user");
    if (userData) {
      const parsedUser = JSON.parse(userData);
      setUser(parsedUser);
    } else {
      setUser(null);
    }
  };

  useEffect(() => {
    // Check user on mount
    checkUser();

    // Listen for storage changes (for logout in other tabs)
    window.addEventListener("storage", checkUser);
    
    // Listen for custom login event
    window.addEventListener("userLoggedIn", checkUser);

    return () => {
      window.removeEventListener("storage", checkUser);
      window.removeEventListener("userLoggedIn", checkUser);
    };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    setUser(null);
    navigate("/");
  };

  return (
    <nav className="bg-white shadow-md fixed w-full top-0 z-50 border-b border-blue-100">
      <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
        <Link
          to="/"
          className="text-2xl font-bold text-blue-700 tracking-tight"
        >
          HotelHub
        </Link>

        <div className="hidden md:flex items-center space-x-8">
          <NavLink
            to="/"
            className={({ isActive }) =>
              `text-sm font-medium ${
                isActive ? "text-blue-600" : "text-gray-600 hover:text-blue-600"
              }`
            }
          >
            Hotels
          </NavLink>

          {/* Only show Add Hotel if user is ADMIN */}
          {user && user.role === "ADMIN" && (
            <NavLink
              to="/hotel/new"
              className={({ isActive }) =>
                `text-sm font-medium ${
                  isActive ? "text-blue-600" : "text-gray-600 hover:text-blue-600"
                }`
              }
            >
              Add Hotel
            </NavLink>
          )}

          <NavLink
            to="/bookings"
            className={({ isActive }) =>
              `text-sm font-medium ${
                isActive ? "text-blue-600" : "text-gray-600 hover:text-blue-600"
              }`
            }
          >
            Bookings
          </NavLink>
          
          {user ? (
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2 text-sm font-medium text-gray-700 bg-blue-50 px-3 py-2 rounded-lg">
                {user.role === "ADMIN" ? (
                  <ShieldCheckIcon className="h-5 w-5 text-purple-600" />
                ) : (
                  <UserCircleIcon className="h-5 w-5 text-blue-600" />
                )}
                <span>
                  {user.firstName} {user.lastName}
                </span>
                <span className={`text-xs px-2 py-1 rounded-full ${
                  user.role === "ADMIN" 
                    ? "bg-purple-100 text-purple-700" 
                    : "bg-green-100 text-green-700"
                }`}>
                  {user.role === "ADMIN" ? "Admin" : "User"}
                </span>
              </div>
              <button
                onClick={handleLogout}
                className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition text-sm font-medium"
              >
                Logout
              </button>
            </div>
          ) : (
            <>
              <NavLink
                to="/login"
                className={({ isActive }) =>
                  `text-sm font-medium ${
                    isActive ? "text-blue-600" : "text-gray-600 hover:text-blue-600"
                  }`
                }
              >
                Login
              </NavLink>

              <NavLink
                to="/signup"
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition text-sm font-medium"
              >
                Sign Up
              </NavLink>
            </>
          )}
        </div>

        <button
          className="md:hidden text-gray-700"
          onClick={() => setOpen(!open)}
        >
          {open ? (
            <XMarkIcon className="h-6 w-6" />
          ) : (
            <Bars3Icon className="h-6 w-6" />
          )}
        </button>
      </div>

      {open && (
        <div className="md:hidden bg-white border-t border-blue-100 shadow-sm">
          <div className="flex flex-col px-6 py-4 space-y-4">
            <NavLink
              to="/"
              onClick={() => setOpen(false)}
              className="text-gray-700 hover:text-blue-600 font-medium"
            >
              Hotels
            </NavLink>
            
            {/* Only show Add Hotel if user is ADMIN */}
            {user && user.role === "ADMIN" && (
              <NavLink
                to="/hotel/new"
                onClick={() => setOpen(false)}
                className="text-gray-700 hover:text-blue-600 font-medium"
              >
                Add Hotel
              </NavLink>
            )}
            
            <NavLink
              to="/bookings"
              onClick={() => setOpen(false)}
              className="text-gray-700 hover:text-blue-600 font-medium"
            >
              Bookings
            </NavLink>
            
            {user ? (
              <>
                <div className="flex items-center space-x-2 text-gray-700 font-medium bg-blue-50 px-3 py-2 rounded-lg">
                  {user.role === "ADMIN" ? (
                    <ShieldCheckIcon className="h-5 w-5 text-purple-600" />
                  ) : (
                    <UserCircleIcon className="h-5 w-5 text-blue-600" />
                  )}
                  <span>{user.firstName} {user.lastName}</span>
                  <span className={`text-xs px-2 py-1 rounded-full ${
                    user.role === "ADMIN" 
                      ? "bg-purple-100 text-purple-700" 
                      : "bg-green-100 text-green-700"
                  }`}>
                    {user.role === "ADMIN" ? "Admin" : "User"}
                  </span>
                </div>
                <button
                  onClick={() => {
                    handleLogout();
                    setOpen(false);
                  }}
                  className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition text-sm font-medium w-full"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <NavLink
                  to="/login"
                  onClick={() => setOpen(false)}
                  className="text-gray-700 hover:text-blue-600 font-medium"
                >
                  Login
                </NavLink>
                <NavLink
                  to="/signup"
                  onClick={() => setOpen(false)}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition text-sm font-medium inline-block text-center"
                >
                  Sign Up
                </NavLink>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
