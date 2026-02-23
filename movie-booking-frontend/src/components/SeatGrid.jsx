import React from 'react';
import { motion } from 'framer-motion';

const SeatGrid = ({ seats, selectedSeats, onSeatSelect, basePrice }) => {
  // Group seats by row
  const seatsByRow = seats.reduce((acc, seat) => {
    const row = seat.rowNumber;
    if (!acc[row]) {
      acc[row] = [];
    }
    acc[row].push(seat);
    return acc;
  }, {});

  const handleSeatClick = (seat) => {
    if (seat.isBooked) return;
    onSeatSelect(seat);
  };

  const getSeatClass = (seat) => {
    if (seat.isBooked) return 'seat seat-booked';
    if (selectedSeats.some(s => s.id === seat.id)) return 'seat seat-selected';
    return 'seat seat-available';
  };

  const getSeatPrice = (seat) => {
    return (basePrice * (seat.priceMultiplier || 1)).toFixed(2);
  };

  return (
    <div className="w-full">
      {/* Screen */}
      <div className="mb-8">
        <div className="w-full h-2 bg-gradient-primary rounded-full mb-2" />
        <p className="text-center text-text-muted text-sm">Screen this way</p>
      </div>

      {/* Legend */}
      <div className="flex justify-center gap-6 mb-6 flex-wrap">
        <div className="flex items-center space-x-2">
          <div className="seat seat-available" />
          <span className="text-sm text-text-muted">Available</span>
        </div>
        <div className="flex items-center space-x-2">
          <div className="seat seat-selected" />
          <span className="text-sm text-text-muted">Selected</span>
        </div>
        <div className="flex items-center space-x-2">
          <div className="seat seat-booked" />
          <span className="text-sm text-text-muted">Booked</span>
        </div>
      </div>

      {/* Seat Grid */}
      <div className="max-w-4xl mx-auto">
        {Object.keys(seatsByRow).sort().map((row) => (
          <motion.div
            key={row}
            className="flex items-center justify-center mb-3"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: parseInt(row.charCodeAt(0) - 65) * 0.05 }}
          >
            {/* Row Label */}
            <span className="w-8 text-center font-bold text-text-muted mr-4">
              {row}
            </span>

            {/* Seats */}
            <div className="flex gap-2">
              {seatsByRow[row]
                .sort((a, b) => a.columnNumber - b.columnNumber)
                .map((seat, index) => (
                  <motion.div
                    key={seat.id}
                    className={getSeatClass(seat)}
                    onClick={() => handleSeatClick(seat)}
                    whileHover={!seat.isBooked ? { scale: 1.1 } : {}}
                    whileTap={!seat.isBooked ? { scale: 0.95 } : {}}
                    title={`${seat.seatNumber} - $${getSeatPrice(seat)}`}
                  >
                    <span className="text-xs text-white opacity-0 hover:opacity-100 transition-opacity">
                      {seat.columnNumber}
                    </span>
                  </motion.div>
                ))}
            </div>
          </motion.div>
        ))}
      </div>

      {/* Seat Type Pricing */}
      <div className="mt-8 flex justify-center gap-6 flex-wrap text-sm text-text-muted">
        <div>Normal: ${basePrice.toFixed(2)}</div>
        <div>Premium: ${(basePrice * 1.5).toFixed(2)}</div>
        <div>VIP: ${(basePrice * 2).toFixed(2)}</div>
      </div>
    </div>
  );
};

export default SeatGrid;
