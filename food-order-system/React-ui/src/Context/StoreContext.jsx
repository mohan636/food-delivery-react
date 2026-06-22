import { createContext, useState } from "react";
import { food_list, menu_list } from "../assets/assets";
import { createOrder } from "../services/orderService";

export const StoreContext = createContext(null);

const StoreContextProvider = (props) => {
  const [cartItems, setCartItems] = useState({});
  const [ordersData, setOrdersData] = useState([]);
  const [isPlacingOrder, setIsPlacingOrder] = useState(false);
  const [orderError, setOrderError] = useState("");
  const [search, setSearch] = useState("");

  const addToCart = (itemId) => {
    if (!cartItems[itemId]) {
      setCartItems((prev) => ({ ...prev, [itemId]: 1 }));
    } else {
      setCartItems((prev) => ({ ...prev, [itemId]: prev[itemId] + 1 }));
    }
  };

  const removeFromCart = (itemId) => {
    setCartItems((prev) => {
      if (prev[itemId] <= 1) {
        const updated = { ...prev };
        delete updated[itemId];
        return updated;
      }

      return {
        ...prev,
        [itemId]: prev[itemId] - 1,
      };
    });
  };

  const getTotalCartAmount = () => {
    let totalAmount = 0;
    for (const item in cartItems) {
      if (cartItems[item] > 0) {
        let itemInfo = food_list.find(
          (product) => product.food_id === Number(item),
        );
        totalAmount += itemInfo.food_price * cartItems[item];
      }
    }
    return totalAmount;
  };

  const placeOrder = async (deliveryData) => {
    setOrderError("");
    setIsPlacingOrder(true);

    try {
      const payload = {
        customerName: deliveryData.firstName,
        item: "Food order",
        deliveryAddress:
          `${deliveryData.street || ""}, ${deliveryData.city || ""}`.trim(),
        amount: getTotalCartAmount(),
      };

      console.log("Payload Sent:", payload);

      const createdOrder = await createOrder(payload);
      setOrdersData((prev) => [createdOrder, ...prev]);
      return createdOrder;
    } catch (error) {
      setOrderError(
        error?.response?.data?.message || "Unable to place order right now.",
      );
      return null;
    } finally {
      setIsPlacingOrder(false);
    }
  };

  const clearCart = () => {
    setCartItems({});
  };

  const contextValue = {
    food_list,
    menu_list,
    cartItems,
    ordersData,
    isPlacingOrder,
    orderError,
    addToCart,
    removeFromCart,
    getTotalCartAmount,
    placeOrder,
    clearCart,
    search,
    setSearch,
  };

  return (
    <StoreContext.Provider value={contextValue}>
      {props.children}
    </StoreContext.Provider>
  );
};

export default StoreContextProvider;
