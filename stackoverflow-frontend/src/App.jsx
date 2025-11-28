import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import QuestionDetails from './pages/QuestionDetails'
import AskQuestion from './pages/AskQuestion'
import Login from './pages/Login'
import Signup from './pages/Signup'
import UserProfile from './pages/UserProfile'
import ProtectedRoute from './components/ProtectedRoute'


export default function App() {
return (
<div className="min-h-screen bg-gray-50 w-full">
<Navbar />
<main className="w-full px-4 py-4">
<Routes>
<Route path="/" element={<Home />} />
<Route path="/question/:id" element={<QuestionDetails />} />
<Route path="/ask" element={<ProtectedRoute><AskQuestion /></ProtectedRoute>} />
<Route path="/login" element={<Login />} />
<Route path="/signup" element={<Signup />} />
<Route path="/profile" element={<ProtectedRoute><UserProfile /></ProtectedRoute>} />
<Route path="/question/findById/:id" element={<QuestionDetails />} />

</Routes>
</main>
</div>
)
}