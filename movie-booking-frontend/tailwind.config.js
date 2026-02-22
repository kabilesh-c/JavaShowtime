/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        dark: {
          bg: '#0D1117',
          card: '#161B22',
          border: '#30363D',
        },
        light: {
          bg: '#FFFFFF',
          card: '#F6F8FA',
          border: '#D0D7DE',
        },
        primary: {
          DEFAULT: '#FF3C78',
          50: '#FFE5ED',
          100: '#FFB3CC',
          200: '#FF80AA',
          300: '#FF4D88',
          400: '#FF3C78',
          500: '#FF1A66',
          600: '#E6005C',
          700: '#B30047',
          800: '#800033',
          900: '#4D001F',
        },
        secondary: {
          DEFAULT: '#FFD166',
          50: '#FFF9E5',
          100: '#FFEDB3',
          200: '#FFE180',
          300: '#FFD166',
          400: '#FFC233',
          500: '#FFB300',
          600: '#E6A200',
          700: '#B37F00',
          800: '#805C00',
          900: '#4D3700',
        },
        text: {
          dark: '#E6EDF3',
          light: '#24292F',
          muted: '#7D8590',
        }
      },
      backgroundImage: {
        'gradient-primary': 'linear-gradient(135deg, #FF3C78, #FFD166)',
        'gradient-dark': 'linear-gradient(135deg, #0D1117, #161B22)',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        'glow': '0 0 20px rgba(255, 60, 120, 0.4)',
        'glow-lg': '0 0 40px rgba(255, 60, 120, 0.6)',
      },
      animation: {
        'float': 'float 3s ease-in-out infinite',
        'slide-up': 'slideUp 0.5s ease-out',
        'fade-in': 'fadeIn 0.3s ease-in',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-20px)' },
        },
        slideUp: {
          '0%': { transform: 'translateY(100px)', opacity: 0 },
          '100%': { transform: 'translateY(0)', opacity: 1 },
        },
        fadeIn: {
          '0%': { opacity: 0 },
          '100%': { opacity: 1 },
        },
      },
    },
  },
  plugins: [],
}
