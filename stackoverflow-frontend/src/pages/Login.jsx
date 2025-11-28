import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { login } from '../api/api';
import { getCurrentUser } from "../auth";

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // 👇 get redirect param or fallback to homepage
  const redirectTo = searchParams.get('redirect') || '/';

  async function submit(e) {
    e.preventDefault();

    try {
      const response = await login({
        username: email,
        password: password,
      });

      // if (response.user) {
      //   localStorage.setItem('user', JSON.stringify(response.user));
      // }
      const user = getCurrentUser();  // 👈 decode JWT payload

      if (user) {
        localStorage.setItem("user", JSON.stringify(user));
      }

      // 👇 redirect user back to where they came from
      navigate(redirectTo, { replace: true });
    } catch (err) {
      console.error('Login failed:', err);
      alert('Invalid credentials');
    }
  }

  return (
    <div className="max-w-md mx-auto bg-white p-6 rounded shadow-sm">
      <h2 className="text-xl font-bold mb-4">Login</h2>

      <form onSubmit={submit} className="space-y-3">
        <input
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
          className="w-full border p-2 rounded"
        />

        <input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
          placeholder="Password"
          className="w-full border p-2 rounded"
        />

        <div className="flex justify-items">
          <button className="px-4 py-2 bg-indigo-600 text-white rounded">
            Login
          </button>
        </div>
      </form>
    </div>
  );
}
