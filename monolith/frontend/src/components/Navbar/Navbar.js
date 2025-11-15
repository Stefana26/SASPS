import { useState } from "react";
import { Link, NavLink } from "react-router-dom";
import { Bars3Icon, XMarkIcon } from "@heroicons/react/24/outline";

const Navbar = () => {
  const [open, setOpen] = useState(false);

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

          <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition text-sm font-medium">
            Sign Up
          </button>
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
            <NavLink
              to="/hotel/new"
              onClick={() => setOpen(false)}
              className="text-gray-700 hover:text-blue-600 font-medium"
            >
              Add Hotel
            </NavLink>
            <NavLink
              to="/bookings"
              onClick={() => setOpen(false)}
              className="text-gray-700 hover:text-blue-600 font-medium"
            >
              Bookings
            </NavLink>
            <NavLink
              to="/login"
              onClick={() => setOpen(false)}
              className="text-gray-700 hover:text-blue-600 font-medium"
            >
              Login
            </NavLink>
            <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition text-sm font-medium">
              Sign Up
            </button>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
