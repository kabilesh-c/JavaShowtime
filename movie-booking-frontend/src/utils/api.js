import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  getCurrentUser: () => api.get('/auth/me'),
};

// Movie APIs
export const movieAPI = {
  getAllMovies: () => api.get('/movies'),
  getMovieById: (id) => api.get(`/movies/${id}`),
  searchMovies: (params) => api.get('/movies/search', { params }),
  getGenres: () => api.get('/movies/genres'),
  getLanguages: () => api.get('/movies/languages'),
};

// Theatre APIs
export const theatreAPI = {
  getAllTheatres: () => api.get('/theatres'),
  getTheatreById: (id) => api.get(`/theatres/${id}`),
  getTheatresByCity: (city) => api.get(`/theatres/city/${city}`),
  getCities: () => api.get('/theatres/cities'),
  getScreensByTheatre: (id) => api.get(`/theatres/${id}/screens`),
};

// Showtime APIs
export const showtimeAPI = {
  getShowtimesByMovie: (movieId, params) => api.get(`/showtimes/movie/${movieId}`, { params }),
  getShowtimeById: (id) => api.get(`/showtimes/${id}`),
  getAvailableSeats: (id) => api.get(`/showtimes/${id}/seats`),
};

// Booking APIs
export const bookingAPI = {
  createBooking: (data) => api.post('/bookings', data),
  getUserBookings: () => api.get('/bookings/user'),
  getBookingById: (id) => api.get(`/bookings/${id}`),
  cancelBooking: (id) => api.post(`/bookings/${id}/cancel`),
};

// Admin APIs
export const adminAPI = {
  // Movies
  createMovie: (data) => api.post('/admin/movies', data),
  updateMovie: (id, data) => api.put(`/admin/movies/${id}`, data),
  deleteMovie: (id) => api.delete(`/admin/movies/${id}`),
  
  // Theatres
  createTheatre: (data) => api.post('/admin/theatres', data),
  updateTheatre: (id, data) => api.put(`/admin/theatres/${id}`, data),
  deleteTheatre: (id) => api.delete(`/admin/theatres/${id}`),
  createScreen: (theatreId, data) => api.post(`/admin/theatres/${theatreId}/screens`, data),
  
  // Showtimes
  createShowtime: (data) => api.post('/admin/showtimes', data),
  deleteShowtime: (id) => api.delete(`/admin/showtimes/${id}`),
  
  // Bookings
  getAllBookings: () => api.get('/admin/bookings'),
  
  // Analytics
  getDashboardAnalytics: () => api.get('/admin/analytics/dashboard'),
  
  // Users
  getAllUsers: () => api.get('/admin/users'),
};

export default api;
