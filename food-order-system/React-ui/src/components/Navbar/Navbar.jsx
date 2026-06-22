import React, { useContext, useState } from "react";
import "./Navbar.css";
import { assets } from "../../assets/assets";
import { Link } from "react-router-dom";
import { StoreContext } from "../../Context/StoreContext";
import { useNavigate } from "react-router-dom";

const Navbar = ({ setShowLogin, setLoginMode }) => {
  const [showDropdown, setShowDropdown] = useState(false);
  const [menu, setMenu] = useState("home");
  const { getTotalCartAmount, search, setSearch } = useContext(StoreContext);
  const navigate = useNavigate();

  return (
    <div className="navbar">
      <Link to="/">
        <img className="logo" src={assets.logo} alt="" />
      </Link>
      <ul className="navbar-menu">
        <Link
          to="/"
          onClick={() => setMenu("home")}
          className={`${menu === "home" ? "active" : ""}`}
        >
          home
        </Link>
        <a
          href="#explore-menu"
          onClick={() => setMenu("menu")}
          className={`${menu === "menu" ? "active" : ""}`}
        >
          menu
        </a>
        <a
          href="#app-download"
          onClick={() => setMenu("mob-app")}
          className={`${menu === "mob-app" ? "active" : ""}`}
        >
          mobile app
        </a>
        <a
          href="#footer"
          onClick={() => setMenu("contact")}
          className={`${menu === "contact" ? "active" : ""}`}
        >
          contact us
        </a>
      </ul>
      <div className="navbar-right">
        <div className="navbar-search">
          <img
            src={assets.search_icon}
            alt="search"
            onClick={() => {
              if (search.trim()) {
                navigate("/");
                setMenu("menu");

                setTimeout(() => {
                  const section = document.getElementById("explore-menu");
                  if (section) {
                    section.scrollIntoView({ behavior: "smooth" });
                  }
                }, 100);
              }
            }}
          />

          <input
            type="text"
            placeholder="Search Pizza, Burger, Cake..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter" && search.trim()) {
                navigate("/");

                setTimeout(() => {
                  const section = document.getElementById("explore-menu");
                  if (section) {
                    section.scrollIntoView({ behavior: "smooth" });
                  }
                }, 100);
              }
            }}
          />
        </div>
        <Link to="/cart" className="navbar-search-icon">
          <img src={assets.basket_icon} alt="" />
          <div className={getTotalCartAmount() > 0 ? "dot" : ""}></div>
        </Link>
        {localStorage.getItem("isLoggedIn") === "true" ? (
          <div
            className="navbar-user"
            onClick={() => setShowDropdown(!showDropdown)}
          >
            <span>👤 {localStorage.getItem("userName")} ▼</span>

            {showDropdown && (
              <div className="user-dropdown">
                <Link to="/myorder">My Orders</Link>

                <button
                  onClick={() => {
                    localStorage.removeItem("isLoggedIn");
                    localStorage.removeItem("userEmail");
                    localStorage.removeItem("userName");
                    window.location.reload();
                  }}
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="navbar-auth">
            <button
              onClick={() => {
                setLoginMode("Login");
                setShowLogin(true);
              }}
            >
              Login
            </button>

            <button
              onClick={() => {
                setLoginMode("Sign Up");
                setShowLogin(true);
              }}
            >
              Sign Up
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Navbar;
