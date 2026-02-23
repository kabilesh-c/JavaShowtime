import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa';
import { showtimeAPI } from '../utils/api';
import SeatGrid from '../components/SeatGrid';
import toast from 'react-hot-toast';

const SeatSelection = () => {
  const { showtimeId } = useParams();
  const navigate = useNavigate();
  const [showtime, setShowtime] = useState(null);
  const [seats, setSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [showtimeId]);

  const fetchData = async () => {
    try {
      const [showtimeRes, seatsRes] = await Promise.all([
        showtimeAPI.getShowtimeById(showtimeId),
        showtimeAPI.getAvailableSeats(showtimeId),
      ]);

      setShowtime(showtimeRes.data);
      setSeats(seatsRes.data);
      setLoading(false);
    } catch (error) {
      toast.error('Failed to load seat information');
      setLoading(false);
    }
  };

  const handleSeatSelect = (seat) => {
    if (selectedSeats.some(s => s.id === seat.id)) {
      setSelectedSeats(selectedSeats.filter(s => s.id !== seat.id));
    } else {
      if (selectedSeats.length >= 10) {
        toast.error('Maximum 10 seats can be selected');
        return;
      }
      setSelectedSeats([...selectedSeats, seat]);
    }
  };

  const calculateTotal = () => {
    return selectedSeats.reduce((total, seat) => {
      return total + (showtime.price * (seat.priceMultiplier || 1));
    }, 0);
  };

  const handleProceed = () => {
    if (selectedSeats.length === 0) {
      toast.error('Please select at least one seat');
      return;
    }

    // Store booking data in session storage
    sessionStorage.setItem('bookingData', JSON.stringify({
      showtimeId,
      showtime,
      selectedSeats,
      totalAmount: calculateTotal(),
    }));

    navigate('/booking-summary');
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="loading-spinner" />
      </div>
    );
  }

  if (!showtime) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-2xl text-text-muted">Showtime not found</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center space-x-2 text-primary hover:text-primary-600 mb-4"
          >
            <FaArrowLeft />
            <span>Back to Movie</span>
          </button>

          <div className="glass rounded-xl p-6">
            <h1 className="text-3xl font-bold mb-2">{showtime.movieTitle}</h1>
            <div className="flex flex-wrap gap-4 text-text-muted">
              <span>{showtime.theatreName}</span>
              <span>•</span>
              <span>{showtime.showDate}</span>
              <span>•</span>
              <span>{showtime.showTime}</span>
              <span>•</span>
              <span>{showtime.city}</span>
            </div>
          </div>
        </div>

        {/* Seat Selection */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Seat Grid */}
          <div className="lg:col-span-2">
            <div className="glass rounded-xl p-6">
              <h2 className="text-2xl font-bold mb-6">Select Seats</h2>
              <SeatGrid
                seats={seats}
                selectedSeats={selectedSeats}
                onSeatSelect={handleSeatSelect}
                basePrice={showtime.price}
              />
            </div>
          </div>

          {/* Booking Summary */}
          <div className="lg:col-span-1">
            <div className="glass rounded-xl p-6 sticky top-24">
              <h2 className="text-2xl font-bold mb-6">Booking Summary</h2>

              <div className="space-y-4 mb-6">
                <div>
                  <p className="text-text-muted text-sm">Selected Seats</p>
                  <p className="font-bold">
                    {selectedSeats.length > 0
                      ? selectedSeats.map(s => s.seatNumber).join(', ')
                      : 'No seats selected'}
                  </p>
                </div>

                <div>
                  <p className="text-text-muted text-sm">Number of Seats</p>
                  <p className="font-bold">{selectedSeats.length}</p>
                </div>

                <div className="border-t border-dark-border dark:border-dark-border pt-4">
                  <div className="flex justify-between mb-2">
                    <span className="text-text-muted">Subtotal</span>
                    <span className="font-semibold">${calculateTotal().toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between mb-2">
                    <span className="text-text-muted">Convenience Fee</span>
                    <span className="font-semibold">$2.00</span>
                  </div>
                  <div className="flex justify-between text-xl font-bold border-t border-dark-border dark:border-dark-border pt-2 mt-2">
                    <span>Total</span>
                    <span className="text-primary">${(calculateTotal() + 2).toFixed(2)}</span>
                  </div>
                </div>
              </div>

              <motion.button
                onClick={handleProceed}
                disabled={selectedSeats.length === 0}
                className="w-full py-3 bg-gradient-primary text-white font-bold rounded-lg btn-glow disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
                whileHover={{ scale: selectedSeats.length > 0 ? 1.02 : 1 }}
                whileTap={{ scale: selectedSeats.length > 0 ? 0.98 : 1 }}
              >
                <span>Proceed to Payment</span>
                <FaArrowRight />
              </motion.button>

              {selectedSeats.length === 0 && (
                <p className="text-center text-text-muted text-sm mt-2">
                  Select seats to continue
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SeatSelection;
