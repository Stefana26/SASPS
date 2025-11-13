import { Routes, Route } from "react-router-dom";
import HotelList from "./components/Hotels/HotelList";
import HotelDetails from "./components/Hotels/HotelDetails";
import AddHotel from "./components/Hotels/AddHotel";
import EditHotel from "./components/Hotels/EditHotel"
import Navbar from "./components/Navbar/Navbar";
import Login from "./components/Login/Login";
import RoomList from "./components/Rooms/RoomList";
import AddRoom from "./components/Rooms/AddRoom";
import EditRoom from "./components/Rooms/EditRoom";
import RoomDetails from "./components/Rooms/RoomDetails";

export default function App() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-100 via-sky-200 to-blue-400 pt-4 pb-10 px-4">
      <Navbar />
      <div className="pt-24 px-6">
        <Routes>
          <Route path="/" element={<HotelList />} />
          <Route path="/hotel/:id" element={<HotelDetails />} />
          <Route path="/hotel/new" element={<AddHotel />} />
          <Route path="/hotel/:id/edit" element={<EditHotel />} />
          <Route path="/hotel/:hotelId/rooms" element={<RoomList />} />
          <Route path="/hotel/:hotelId/rooms/new" element={<AddRoom />} />
          <Route path="/hotel/:hotelId/rooms/:roomId" element={<RoomDetails />} />
          <Route path="/room/:id/edit" element={<EditRoom />} />
          <Route path="/hotel/:hotelId/rooms/:roomId/edit" element={<EditRoom />} />
          <Route path="/login" element={<Login/>}/>
        </Routes>
      </div>
    </div>
  );
}
