import React, { useEffect, useState } from "react";
import "./MyOrders.css";
import { getOrders } from "../../services/orderService";
import { useNavigate } from "react-router-dom";

const MyOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [activeTrackingOrder, setActiveTrackingOrder] = useState(null);
  
  const navigate = useNavigate();

  useEffect(() => {
    if (localStorage.getItem("isLoggedIn") !== "true") {
      navigate("/");
    }
  }, [navigate]);

  useEffect(() => {
    const msg = localStorage.getItem("orderSuccess");
    if (msg) {
      setSuccessMessage(msg);
      localStorage.removeItem("orderSuccess");
    }
  }, []);

  // Poll orders every 5 seconds to get real-time workflow status updates
  useEffect(() => {
    let isMounted = true;

    const loadOrders = async (showLoadingIndicator = false) => {
      try {
        if (showLoadingIndicator) setLoading(true);
        const data = await getOrders();
        if (isMounted) {
          setOrders(data);
          
          // If the user currently has an order tracking modal open, update its references live!
          if (activeTrackingOrder) {
            const freshTracked = data.find(o => o.id === activeTrackingOrder.id);
            if (freshTracked) {
              setActiveTrackingOrder(freshTracked);
            }
          }
        }
      } catch (err) {
        if (isMounted) setError("Unable to fetch your orders from the backend.");
      } finally {
        if (showLoadingIndicator && isMounted) setLoading(false);
      }
    };

    loadOrders(true); // show spinner on initial load
    const interval = setInterval(() => {
      loadOrders(false); // silent refresh in background
    }, 5000);

    return () => {
      isMounted = false;
      clearInterval(interval);
    };
  }, [activeTrackingOrder]);

  const getStepClass = (stepName, orderStatus) => {
    const steps = ["PLACED", "PAYMENT_PROCESSING", "KITCHEN_PREPARATION", "OUT_FOR_DELIVERY", "DELIVERED"];
    
    if (orderStatus === "CANCELLED" || orderStatus === "PAYMENT_FAILED") {
      if (stepName === "PAYMENT_PROCESSING") return "failed";
      if (steps.indexOf(stepName) > steps.indexOf("PAYMENT_PROCESSING")) return "pending";
    }

    const currentIndex = steps.indexOf(orderStatus);
    const targetIndex = steps.indexOf(stepName);

    if (targetIndex < 0) return "pending";

    if (orderStatus === "DELIVERED") {
      return "completed";
    }

    if (currentIndex > targetIndex) {
      return "completed";
    } else if (currentIndex === targetIndex) {
      return "active";
    } else {
      return "pending";
    }
  };

  return (
    <div className="my-orders">
      {successMessage && (
        <div className="success-message">{successMessage}</div>
      )}
      <h2>📦 My Orders</h2>

      {loading && <p>Loading orders...</p>}

      {error && <p style={{ color: "crimson" }}>{error}</p>}

      {!loading && !error && orders.length === 0 && <p>No orders found yet.</p>}

      <div className="orders-container">
        {orders.map((order) => (
          <div className="order-card" key={order.id}>
            <div className="order-top">
              <div className="order-id">
                Order #{order.orderNumber || order.id}
              </div>
              <div className={`order-status status-${order.status ? order.status.toLowerCase() : "default"}`}>
                {order.status}
              </div>
            </div>

            <div className="order-item">🍕 {order.item}</div>
            <div className="order-address">📍 {order.deliveryAddress}</div>
            <div className="order-footer">
              <button 
                className="track-btn" 
                onClick={() => setActiveTrackingOrder(order)}
              >
                Track Order
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Progress Tracker Modal */}
      {activeTrackingOrder && (
        <div className="modal-overlay" onClick={() => setActiveTrackingOrder(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Track Order #{activeTrackingOrder.orderNumber || activeTrackingOrder.id}</h3>
              <button className="close-btn" onClick={() => setActiveTrackingOrder(null)}>×</button>
            </div>

            <div className="steps-container">
              {/* Step 1: Placed */}
              <div className="step-row">
                <div className={`step-indicator ${getStepClass("PLACED", activeTrackingOrder.status)}`}>
                  <div className="step-inner-dot"></div>
                </div>
                <div className="step-info">
                  <span className={`step-title ${getStepClass("PLACED", activeTrackingOrder.status)}`}>Order Placed</span>
                  <span className="step-desc">We have received your food order.</span>
                </div>
              </div>

              {/* Step 2: Payment */}
              <div className="step-row">
                <div className={`step-indicator ${getStepClass("PAYMENT_PROCESSING", activeTrackingOrder.status)}`}>
                  <div className="step-inner-dot"></div>
                </div>
                <div className="step-info">
                  <span className={`step-title ${getStepClass("PAYMENT_PROCESSING", activeTrackingOrder.status)}`}>
                    {activeTrackingOrder.status === "PAYMENT_FAILED" ? "Payment Failed" : 
                     activeTrackingOrder.status === "CANCELLED" ? "Order Cancelled" : "Payment Verification"}
                  </span>
                  <span className="step-desc">
                    {activeTrackingOrder.status === "PAYMENT_FAILED" ? "Transaction was rejected by bank." :
                     activeTrackingOrder.status === "CANCELLED" ? "Order was cancelled due to failure." :
                     "Authorizing credit card transaction."}
                  </span>
                </div>
              </div>

              {/* Step 3: Kitchen preparation */}
              <div className="step-row">
                <div className={`step-indicator ${getStepClass("KITCHEN_PREPARATION", activeTrackingOrder.status)}`}>
                  <div className="step-inner-dot"></div>
                </div>
                <div className="step-info">
                  <span className={`step-title ${getStepClass("KITCHEN_PREPARATION", activeTrackingOrder.status)}`}>Kitchen Preparation</span>
                  <span className="step-desc">The chef is cooking your delicious meals.</span>
                </div>
              </div>

              {/* Step 4: Out for Delivery */}
              <div className="step-row">
                <div className={`step-indicator ${getStepClass("OUT_FOR_DELIVERY", activeTrackingOrder.status)}`}>
                  <div className="step-inner-dot"></div>
                </div>
                <div className="step-info">
                  <span className={`step-title ${getStepClass("OUT_FOR_DELIVERY", activeTrackingOrder.status)}`}>Out For Delivery</span>
                  <span className="step-desc">Courier rider is carrying your package.</span>
                </div>
              </div>

              {/* Step 5: Delivered */}
              <div className="step-row">
                <div className={`step-indicator ${getStepClass("DELIVERED", activeTrackingOrder.status)}`}>
                  <div className="step-inner-dot"></div>
                </div>
                <div className="step-info">
                  <span className={`step-title ${getStepClass("DELIVERED", activeTrackingOrder.status)}`}>Delivered</span>
                  <span className="step-desc">Enjoy your warm fresh food!</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyOrders;
