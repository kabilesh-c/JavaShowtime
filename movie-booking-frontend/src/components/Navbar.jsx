import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { FaFilm, FaUser, FaTicketAlt, FaSignOutAlt } from 'react-icons/fa';
import { MdDashboard } from 'react-icons/md';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const Navbar = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="sticky top-0 z-50 glass backdrop-blur-md border-b border-dark-border dark:border-dark-border">
      <div className="container mx-auto px-4 py-3">
        <div className="flex items-center justify-between">
          {/* Logo */}
          <Link to="/">
            <motion.div 
              className="flex items-center space-x-2"
              whileHover={{ scale: 1.05 }}
            >
              <FaFilm className="text-3xl text-primary" />
              <span className="text-2xl font-bold gradient-text">TickeeHub</span>
            </motion.div>
          </Link>

          {/* Navigation Links */}
          <div className="hidden md:flex items-center space-x-8">
            <NavLink to="/">Home</NavLink>
            <NavLink to="/movies">Movies</NavLink>
            <NavLink to="/theatres">Theatres</NavLink>
            {isAuthenticated() && (
              <NavLink to="/my-bookings">
                <FaTicketAlt className="inline mr-1" />
                My Bookings
              </NavLink>
            )}
            {isAdmin() && (
              <NavLink to="/admin">
                <MdDashboard className="inline mr-1" />
                Dashboard
              </NavLink>
            )}
          </div>

          {/* Right Side Actions */}
          <div className="flex items-center space-x-4">
            {/* Auth Buttons */}
            {isAuthenticated() ? (
              <div className="flex items-center space-x-3">
                {/* REVIEW 2: DISABLED - Profile Link */}
                <motion.div 
                  onClick={() => toast.error('Profile feature will be available in Review 3')}
                  className="flex items-center space-x-2 px-4 py-2 rounded-lg bg-dark-card dark:bg-dark-card cursor-not-allowed opacity-60"
                  whileHover={{ scale: 1.02 }}
                  title="Available in Review 3"
                >
                  <FaUser className="text-gray-500" />
                  <span className="text-sm font-medium text-gray-500">{user?.fullName}</span>
                </motion.div>
                <motion.button
                  onClick={handleLogout}
                  className="p-2 rounded-lg hover:bg-red-500 hover:bg-opacity-20 text-red-500 transition-all"
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.95 }}
                >
                  <FaSignOutAlt className="text-xl" />
                </motion.button>
              </div>
            ) : (
              <div className="flex items-center space-x-3">
                <Link to="/login">
                  <motion.button
                    className="px-4 py-2 rounded-lg font-medium hover:bg-dark-card dark:hover:bg-dark-card transition-all"
                    whileHover={{ scale: 1.05 }}
                  >
                    Login
                  </motion.button>
                </Link>
                <Link to="/register">
                  <motion.button
                    className="px-6 py-2 rounded-lg font-medium bg-gradient-primary text-white btn-glow"
                    whileHover={{ scale: 1.05 }}
                  >
                    Sign Up
                  </motion.button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

const NavLink = ({ to, children }) => {
  return (
    <Link to={to}>
      <motion.span
        className="text-text-dark dark:text-text-dark hover:text-primary transition-colors font-medium"
        whileHover={{ y: -2 }}
      >
        {children}
      </motion.span>
    </Link>
  );
};

export default Navbar;
