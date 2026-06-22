import React, { useState } from "react";
import Home from "./pages/Home/Home";
import Footer from "./components/Footer/Footer";
import Navbar from "./components/Navbar/Navbar";
import { Route, Routes } from "react-router-dom";
import Cart from "./pages/Cart/Cart";
import LoginPopup from "./components/LoginPopup/LoginPopup";
import PlaceOrder from "./pages/PlaceOrder/PlaceOrder";
import MyOrders from "./pages/MyOrders/MyOrders";
import ProtectedRoute from "./components/ProtectedRoute";

const App = () => {
  const [showLogin, setShowLogin] = useState(false);
  const [loginMode, setLoginMode] = useState("Login");

  return (
    <>
      {showLogin ? (
        <LoginPopup setShowLogin={setShowLogin} initialMode={loginMode} />
      ) : (
        <></>
      )}
      <div className="app">
        <Navbar setShowLogin={setShowLogin} setLoginMode={setLoginMode} />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/cart" element={<Cart setShowLogin={setShowLogin} />} />
          <Route
            path="/order"
            element={
              <ProtectedRoute>
                <PlaceOrder />
              </ProtectedRoute>
            }
          />
          <Route
            path="/myorder"
            element={
              <ProtectedRoute>
                <MyOrders />
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>
      <Footer />
    </>
  );
};

export default App;
