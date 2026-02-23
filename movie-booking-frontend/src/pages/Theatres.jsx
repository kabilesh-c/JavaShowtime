import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { FaMapMarkerAlt, FaFilm, FaTheaterMasks } from 'react-icons/fa';
import { theatreAPI } from '../utils/api';
import toast from 'react-hot-toast';

const Theatres = () => {
  const [theatres, setTheatres] = useState([]);
  const [cities, setCities] = useState([]);
  const [selectedCity, setSelectedCity] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (selectedCity) {
      fetchTheatresByCity();
    }
  }, [selectedCity]);

  const fetchData = async () => {
    try {
      const [theatresRes, citiesRes] = await Promise.all([
        theatreAPI.getAllTheatres(),
        theatreAPI.getCities(),
      ]);

      setTheatres(theatresRes.data);
      setCities(citiesRes.data);
      if (citiesRes.data.length > 0) {
        setSelectedCity(citiesRes.data[0]);
      }
      setLoading(false);
    } catch (error) {
      toast.error('Failed to load theatres');
      setLoading(false);
    }
  };

  const fetchTheatresByCity = async () => {
    try {
      const response = await theatreAPI.getTheatresByCity(selectedCity);
      setTheatres(response.data);
    } catch (error) {
      console.error('Failed to load theatres');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="loading-spinner" />
      </div>
    );
  }

  return (
    <div className="min-h-screen py-8">
      <div className="container mx-auto px-4">
        <motion.div
          className="mb-8"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <h1 className="text-4xl font-bold gradient-text mb-2">Theatres</h1>
          <p className="text-text-muted">Find theatres near you</p>
        </motion.div>

        {/* City Filter */}
        {/* REVIEW 2: DISABLED - City filter functionality */}
        <div className="mb-8 opacity-60">
          <select
            value={selectedCity}
            onChange={(e) => toast.error('City filter will be available in Review 3')}
            disabled
            className="px-4 py-2 bg-dark-card dark:bg-dark-card border border-dark-border dark:border-dark-border rounded-lg outline-none text-text-dark dark:text-text-dark cursor-not-allowed"
          >
            <option value="">All Cities (Available in Review 3)</option>
          </select>
        </div>

        {/* Theatres List */}
        {theatres.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {theatres.map(theatre => (
              <TheatreCard key={theatre.id} theatre={theatre} />
            ))}
          </div>
        ) : (
          <div className="text-center py-20">
            <FaTheaterMasks className="text-6xl text-text-muted mx-auto mb-4" />
            <p className="text-2xl text-text-muted">No theatres found</p>
          </div>
        )}
      </div>
    </div>
  );
};

const TheatreCard = ({ theatre }) => {
  return (
    <motion.div
      className="glass rounded-xl p-6 card-hover"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
    >
      <div className="flex items-start justify-between mb-4">
        <FaTheaterMasks className="text-4xl text-primary" />
        <span className="px-3 py-1 bg-green-500 bg-opacity-20 text-green-500 text-xs rounded-full font-semibold">
          Active
        </span>
      </div>

      <h3 className="text-xl font-bold mb-2">{theatre.name}</h3>

      <div className="space-y-2 text-sm text-text-muted">
        <div className="flex items-start space-x-2">
          <FaMapMarkerAlt className="text-primary mt-1 flex-shrink-0" />
          <p>{theatre.location}, {theatre.city}</p>
        </div>

        <div className="flex items-center space-x-2">
          <FaFilm className="text-primary" />
          <p>{theatre.totalScreens} Screens</p>
        </div>

        {theatre.facilities && (
          <div className="mt-4 pt-4 border-t border-dark-border dark:border-dark-border">
            <p className="text-xs text-text-muted">Facilities</p>
            <p className="text-sm">{theatre.facilities}</p>
          </div>
        )}
      </div>
    </motion.div>
  );
};

export default Theatres;
