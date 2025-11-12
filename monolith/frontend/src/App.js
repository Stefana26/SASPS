import { Routes, Route } from "react-router-dom";
import HotelList from "./components/Hotels/HotelList"
import HotelDetails from "./components/Hotels/HotelDetails"

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HotelList />} />
      <Route path="/hotel/:id" element={<HotelDetails />} />
    </Routes>
  );
}
