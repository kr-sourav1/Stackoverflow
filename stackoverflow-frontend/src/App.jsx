import React from "react";
import { Routes, Route } from "react-router-dom";

import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import QuestionDetails from "./pages/QuestionDetails";
import AskQuestion from "./pages/AskQuestion";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import UserProfile from "./pages/UserProfile";
import ProtectedRoute from "./components/ProtectedRoute";

export default function App() {
  return (
    <div className="app-shell">
      {/* TOP BAR / NAVBAR */}
      <header className="app-header">
        <div className="app-header-inner">
          {/* Your existing navbar (search, ask question, auth buttons, etc.) */}
          <Navbar />
        </div>
      </header>

      {/* MAIN LAYOUT */}
      <main className="app-main">
        <div className="app-main-inner">
          {/* LEFT COLUMN – main content / routes */}
          <section className="space-y-4">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/question/:id" element={<QuestionDetails />} />
              <Route
                path="/question/findById/:id"
                element={<QuestionDetails />}
              />
              <Route
                path="/ask"
                element={
                  <ProtectedRoute>
                    <AskQuestion />
                  </ProtectedRoute>
                }
              />
              <Route path="/login" element={<Login />} />
              <Route path="/signup" element={<Signup />} />
              <Route
                path="/profile"
                element={
                  <ProtectedRoute>
                    <UserProfile />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </section>

          {/* RIGHT COLUMN – enterprise-style sidebar */}
          
        </div>
      </main>

      {/* FOOTER */}
      <footer className="app-footer">
        <div className="app-footer-inner flex items-center justify-end .min-h-[80px]">
          <span className="items-center justify-center">
            © {new Date().getFullYear()} Stackoverflow Application
          </span>
        </div>
      </footer>
    </div>
  );
}
