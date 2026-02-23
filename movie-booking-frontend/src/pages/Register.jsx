import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, Link } from 'react-router-dom';
import { FaEnvelope, FaLock, FaUser, FaPhone, FaFilm } from 'react-icons/fa';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const Register = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    phone: '',
  });
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }

    if (formData.password.length < 6) {
      toast.error('Password must be at least 6 characters');
      return;
    }

    setLoading(true);

    const result = await register(
      formData.email,
      formData.password,
      formData.fullName,
      formData.phone
    );

    if (result.success) {
      toast.success('Registration successful!');
      navigate('/');
    } else {
      toast.error(result.error);
    }

    setLoading(false);
  };

  return (
    <div className="min-h-screen flex items-center justify-center py-12 px-4 relative overflow-hidden">
      {/* Animated Background */}
      <div className="absolute inset-0 overflow-hidden">
        <motion.div
          className="absolute top-0 left-0 w-96 h-96 bg-primary opacity-20 rounded-full filter blur-3xl"
          animate={{ x: [0, 100, 0], y: [0, 50, 0] }}
          transition={{ duration: 20, repeat: Infinity }}
        />
        <motion.div
          className="absolute bottom-0 right-0 w-96 h-96 bg-secondary opacity-20 rounded-full filter blur-3xl"
          animate={{ x: [0, -100, 0], y: [0, -50, 0] }}
          transition={{ duration: 15, repeat: Infinity }}
        />
      </div>

      <motion.div
        className="relative max-w-md w-full glass rounded-2xl shadow-2xl p-8"
        initial={{ opacity: 0, y: 50 }}
        animate={{ opacity: 1, y: 0 }}
      >
        {/* Logo */}
        <div className="text-center mb-8">
          <motion.div
            className="inline-flex items-center justify-center w-16 h-16 bg-gradient-primary rounded-full mb-4"
            whileHover={{ rotate: 360 }}
            transition={{ duration: 0.5 }}
          >
            <FaFilm className="text-3xl text-white" />
          </motion.div>
          <h2 className="text-3xl font-bold gradient-text">Create Account</h2>
          <p className="text-text-muted mt-2">Join TickeeHub today</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Full Name */}
          <div>
            <label className="block text-sm font-medium text-text-dark dark:text-text-dark mb-2">
              Full Name
            </label>
            <div className="relative">
              <FaUser className="absolute left-3 top-1/2 transform -translate-y-1/2 text-text-muted" />
              <input
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                required
                className="w-full pl-10 pr-4 py-3 bg-dark-card dark:bg-dark-card border border-dark-border dark:border-dark-border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-text-dark dark:text-text-dark"
                placeholder="John Doe"
              />
            </div>
          </div>

          {/* Email */}
          <div>
            <label className="block text-sm font-medium text-text-dark dark:text-text-dark mb-2">
              Email Address
            </label>
            <div className="relative">
              <FaEnvelope className="absolute left-3 top-1/2 transform -translate-y-1/2 text-text-muted" />
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                className="w-full pl-10 pr-4 py-3 bg-dark-card dark:bg-dark-card border border-dark-border dark:border-dark-border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-text-dark dark:text-text-dark"
                placeholder="your.email@example.com"
              />
            </div>
          </div>

          {/* Phone */}
          <div>
            <label className="block text-sm font-medium text-text-dark dark:text-text-dark mb-2">
              Phone Number
            </label>
            <div className="relative">
              <FaPhone className="absolute left-3 top-1/2 transform -translate-y-1/2 text-text-muted" />
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                required
                className="w-full pl-10 pr-4 py-3 bg-dark-card dark:bg-dark-card border border-dark-border dark:border-dark-border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-text-dark dark:text-text-dark"
                placeholder="+1 234 567 8900"
              />
            </div>
          </div>

          {/* Password */}
          <div>
            <label className="block text-sm font-medium text-text-dark dark:text-text-dark mb-2">
              Password
            </label>
            <div className="relative">
              <FaLock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-text-muted" />
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
                className="w-full pl-10 pr-4 py-3 bg-dark-card dark:bg-dark-card border border-dark-border dark:border-dark-border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-text-dark dark:text-text-dark"
                placeholder="••••••••"
              />
            </div>
          </div>

          {/* Confirm Password */}
          <div>
            <label className="block text-sm font-medium text-text-dark dark:text-text-dark mb-2">
              Confirm Password
            </label>
            <div className="relative">
              <FaLock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-text-muted" />
              <input
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
                className="w-full pl-10 pr-4 py-3 bg-dark-card dark:bg-dark-card border border-dark-border dark:border-dark-border rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none text-text-dark dark:text-text-dark"
                placeholder="••••••••"
              />
            </div>
          </div>

          {/* Submit Button */}
          <motion.button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-gradient-primary text-white font-bold rounded-lg btn-glow disabled:opacity-50 disabled:cursor-not-allowed"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            {loading ? (
              <div className="flex items-center justify-center">
                <div className="loading-spinner w-5 h-5 mr-2" />
                Creating Account...
              </div>
            ) : (
              'Create Account'
            )}
          </motion.button>
        </form>

        {/* Login Link */}
        <div className="mt-6 text-center space-y-2">
          <p className="text-text-muted">
            Already have an account?{' '}
            <Link to="/login" className="text-primary hover:text-primary-600 font-semibold">
              Sign In
            </Link>
          </p>
          <p className="text-text-muted">
            Are you an admin?{' '}
            <Link to="/admin/register" className="text-secondary hover:text-secondary-600 font-semibold">
              Admin Signup
            </Link>
          </p>
        </div>
      </motion.div>
    </div>
  );
};

export default Register;
