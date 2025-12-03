import React, { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { login } from "../api/api";
import { getCurrentUser } from "../auth";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // redirect param or fallback to homepage
  const redirectTo = searchParams.get("redirect") || "/";

  async function submit(e) {
    e.preventDefault();
    setError("");

    try {
      await login({
        username: email,
        password: password,
      });

      const user = getCurrentUser(); // decode JWT payload

      if (user) {
        localStorage.setItem("user", JSON.stringify(user));
      }

      navigate(redirectTo, { replace: true });
    } catch (err) {
      console.error("Login failed:", err);
      setError("Invalid email or password. Please try again.");
    }
  }

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        {/* Header */}
        <div className="mb-6 text-center">
          <p className="text-[11px] font-semibold uppercase tracking-[0.25em] text-ink-500">
            Welcome back
          </p>
          <h1 className="mt-2 text-2xl font-semibold text-ink-50">
            Sign in to your workspace
          </h1>
          <p className="mt-2 text-xs text-ink-400">
            Use your registered email and password to access StackOverflow.
          </p>
        </div>

        {/* Card */}
        <div className="rounded-2xl border border-border-subtle/80 bg-surface-elevated/70 px-6 py-6 shadow-[0_18px_60px_rgba(15,23,42,0.9)]">
          {error && (
            <div className="mb-4 rounded-md border border-accent-red/50 bg-accent-red/10 px-3 py-2 text-xs text-accent-red">
              {error}
            </div>
          )}

          <form onSubmit={submit} className="space-y-4">
            {/* Email */}
            <div className="space-y-1.5">
              <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
                Email
              </label>
              <input
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your username"
                className="w-full rounded-lg border border-border-subtle bg-surface-subtle px-3 py-2 text-sm text-ink-50 placeholder:text-ink-500 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500/80"
              />
            </div>

            {/* Password */}
            <div className="space-y-1.5">
              <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
                Password
              </label>
              <input
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                type="password"
                placeholder="Enter your password"
                className="w-full rounded-lg border border-border-subtle bg-surface-subtle px-3 py-2 text-sm text-ink-50 placeholder:text-ink-500 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500/80"
              />
            </div>

            {/* Actions */}
            <button
              type="submit"
              className="mt-2 inline-flex w-full items-center justify-center rounded-lg bg-brand-500 px-4 py-2.5 text-sm font-medium text-white shadow-sm hover:bg-brand-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2 focus-visible:ring-offset-surface"
            >
              Sign in
            </button>
          </form>

          <p className="mt-4 text-center text-xs text-ink-400">
            Don’t have an account?{" "}
            <a
              href="/signup"
              className="font-medium text-accent-blue hover:text-accent-blue/80"
            >
              Create one
            </a>
          </p>
        </div>
      </div>
    </div>
  );
}
