import React, { useState } from "react";
import "./LoginPopup.css";
import { assets } from "../../assets/assets";
import api from "../../services/api";

const LoginPopup = ({ setShowLogin, initialMode }) => {
  const [currState, setCurrState] = useState(initialMode || "Sign Up");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [loginMessage, setLoginMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [agreeTerms, setAgreeTerms] = useState(false);

  const handleSubmit = async () => {
    setErrorMessage("");
    setSuccessMessage("");
    setLoginMessage("");

    if (currState === "Sign Up") {
      if (!name.trim()) {
        setErrorMessage("Name is required");
        return;
      }

      if (!email.trim()) {
        setErrorMessage("Email is required");
        return;
      }

      if (!email.includes("@")) {
        setErrorMessage("Please enter a valid email address");
        return;
      }

      if (!password.trim()) {
        setErrorMessage("Password is required");
        return;
      }
    }

    if (currState === "Login") {
      if (!email.trim()) {
        setErrorMessage("Email is required");
        return;
      }

      if (!password.trim()) {
        setErrorMessage("Password is required");
        return;
      }
    }

    try {
      if (currState === "Sign Up") {
        const response = await api.post(
          "/api/users/register",
          {
            name,
            email,
            password,
          },
        );

        if (response.data === "Account created successfully") {
          setSuccessMessage("Account created successfully. Please login.");
          setErrorMessage("");

          setCurrState("Login");

          setName("");
          setEmail("");
          setPassword("");
        } else {
          setErrorMessage(response.data || "Invalid registration details");
        }

        setName("");
        setEmail("");
        setPassword("");
      } else {
        const response = await api.post(
          "/api/users/login",
          {
            email,
            password,
          },
        );

        if (response.data) {
          localStorage.setItem("isLoggedIn", "true");
          localStorage.setItem("userEmail", response.data.email);
          localStorage.setItem("userName", response.data.name);

          setLoginMessage("Login successful. Redirecting...");
          setErrorMessage("");

          setTimeout(() => {
            setShowLogin(false);
            window.location.href = "/order";
          }, 2000);
        } else {
          setErrorMessage("Invalid email or password");
          setLoginMessage("");
        }
      }
    } catch (error) {
      console.log("FULL ERROR:", error);
      console.log("RESPONSE:", error.response);

      setErrorMessage(
        error.response?.data?.message ||
          error.response?.data ||
          "Something went wrong. Please try again.",
      );
    }
  };

  return (
    <div className="login-popup">
      <div className="login-popup-container">
        <div className="login-popup-title">
          <h2>{currState}</h2>{" "}
          <img
            onClick={() => setShowLogin(false)}
            src={assets.cross_icon}
            alt=""
          />
        </div>
        <div className="login-popup-inputs">
          {currState === "Sign Up" ? (
            <input
              type="text"
              placeholder="Your name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          ) : (
            <></>
          )}
          <input
            type="email"
            placeholder="Your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {successMessage && <p className="success-message">{successMessage}</p>}

        {loginMessage && <p className="success-message">{loginMessage}</p>}

        {errorMessage && <p className="error-message">{errorMessage}</p>}

        <button
          onClick={handleSubmit}
          disabled={
            currState === "Sign Up"
              ? !name.trim() || !email.trim() || !password.trim() || !agreeTerms
              : !email.trim() || !password.trim()
          }
        >
          {currState === "Login" ? "Login" : "Create account"}
        </button>
        {currState === "Sign Up" && (
          <div className="login-popup-condition">
            <input
              type="checkbox"
              checked={agreeTerms}
              onChange={(e) => setAgreeTerms(e.target.checked)}
            />
            <p>By continuing, I agree to the terms of use & privacy policy.</p>
          </div>
        )}
        {currState === "Login" ? (
          <p>
            Create a new account?{" "}
            <span
              onClick={() => {
                setCurrState("Sign Up");
                setErrorMessage("");
                setSuccessMessage("");
                setLoginMessage("");
              }}
            >
              Click here
            </span>
          </p>
        ) : (
          <p>
            Already have an account?{" "}
            <span
              onClick={() => {
                setCurrState("Login");
                setErrorMessage("");
                setSuccessMessage("");
                setLoginMessage("");
              }}
            >
              Login here
            </span>
          </p>
        )}
      </div>
    </div>
  );
};

export default LoginPopup;
