// module.exports = {
//   content: [
//     "./index.html",
//     "./src/**/*.{js,jsx,ts,tsx}"
//   ],
//   theme: {
//     extend: {
//       colors: {
//         searchBar: "#646cffaa",
//       }
//     },
//   },
//   plugins: [],
// }


// tailwind.config.cjs
/** @type {import('tailwindcss').Config} */
module.exports = {
  // with Tailwind v4 + @tailwindcss/postcss you don't need `content` here
  theme: {
    extend: {
      fontFamily: {
        sans: [
          "system-ui",
          "-apple-system",
          "BlinkMacSystemFont",
          "Segoe UI",
          "Roboto",
          "sans-serif",
        ],
      },
      colors: {
        brand: {
          50: "#f4f7ff",
          100: "#e4ebff",
          200: "#c7d4ff",
          300: "#9fb3ff",
          400: "#7490ff",
          500: "#4f6bff", // main CTA
          600: "#394fe0",
          700: "#2838b0",
          800: "#1f2b83",
          900: "#192366",
        },
        ink: {
          50: "#f9fafb",
          100: "#f3f4f6",
          200: "#e5e7eb",
          300: "#d1d5db",
          400: "#9ca3af",
          500: "#6b7280",
          600: "#4b5563",
          700: "#374151",
          800: "#1f2933",
          900: "#111827",
        },
        surface: {
          subtle: "#020617",
          DEFAULT: "#020617", // this is what `bg-surface` uses
          elevated: "#0b1220",
        },
        card: {
          DEFAULT: "#020617",
          soft: "#0b1220",
        },
        border: {
          subtle: "rgba(148, 163, 184, 0.35)",
          strong: "rgba(148, 163, 184, 0.65)",
        },
        accent: {
          blue: "#38bdf8",
          green: "#22c55e",
          amber: "#f59e0b",
          red: "#ef4444",
        },
        // your existing custom color from before
        searchBar: "#646cffaa",
      },
      boxShadow: {
        card: "0 22px 45px rgba(15, 23, 42, 0.45)",
        soft: "0 18px 30px rgba(15, 23, 42, 0.35)",
        subtle: "0 10px 18px rgba(15, 23, 42, 0.25)",
      },
      borderRadius: {
        lgx: "0.9rem",
        xl: "1.25rem",
        "2xl": "1.5rem",
      },
      backdropBlur: {
        soft: "18px",
      },
      spacing: {
        "4.5": "1.125rem",
      },
    },
    container: {
      center: true,
      padding: "1.5rem",
      screens: {
        sm: "640px",
        md: "768px",
        lg: "1024px",
        xl: "1160px",
        "2xl": "1320px",
      },
    },
  },
  plugins: [],
};
