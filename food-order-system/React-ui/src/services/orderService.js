import api from "./api";

export const getOrders = async () => {
  const response = await api.get("/api/orders");
  return response.data;
};

export const getOrderById = async (id) => {
  const response = await api.get(`/api/orders/${id}`);
  return response.data;
};

export const createOrder = async (payload) => {
  const response = await api.post("/api/orders", payload);
  return response.data;
};
