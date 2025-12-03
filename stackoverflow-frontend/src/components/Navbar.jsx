import React, { useState, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { globalSearch } from "../api/api";
import { clearToken } from "../auth";

export default function Navbar() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "null");

  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const timerRef = useRef(null);

  function handleLogout() {
    clearToken();
    localStorage.removeItem("user");
    navigate("/");
  }

  function handleSelectSuggestion(item) {
    const text = item?.title || item?.name || item?.query || String(item);
    navigate(`/?q=${encodeURIComponent(text)}`);
    setShowSuggestions(false);
    setQuery("");
    setSuggestions([]);
  }

  function handleInputChange(e) {
    const v = e.target.value;
    setQuery(v);
    if (timerRef.current) clearTimeout(timerRef.current);

    if (!v) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }

    timerRef.current = setTimeout(() => {
      globalSearch(v)
        .then((res) => {
          setSuggestions(Array.isArray(res.data) ? res.data : []);
          setShowSuggestions(true);
        })
        .catch((err) => {
          console.error("globalSearch error", err);
          setSuggestions([]);
          setShowSuggestions(false);
        });
    }, 300);
  }

  return (
    <nav className="sticky top-0 z-40 border-b border-border-subtle/70 bg-surface-elevated/95 backdrop-blur-md">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3 md:px-6">
        {/* Left: Logo + search */}
        <div className="flex items-center gap-4 md:gap-6">
          {/* Logo / product name */}
          <Link
            to="/"
            className="flex items-center gap-2 text-sm md:text-base font-semibold tracking-[0.18em] uppercase text-ink-100"
          >
            <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-brand-500 text-xs font-bold shadow-[0_8px_24px_rgba(79,107,255,0.8)]">
              Q&A
            </span>
            <span className="hidden sm:inline">
              Stackoverflow
            </span>
            <span className="sm:hidden">Stack FE</span>
          </Link>

          {/* Global search */}
          <div className="relative hidden md:block">
            <div className="flex items-center gap-2 rounded-full border border-border-subtle bg-surface-subtle/90 px-3 py-1.5 text-xs text-ink-100 focus-within:border-brand-400 focus-within:ring-2 focus-within:ring-brand-500/60">
              <span className="text-ink-500 text-xs">⌘K</span>
              <input
                value={query}
                onChange={handleInputChange}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    navigate(`/?q=${encodeURIComponent(query)}`);
                  }
                }}
                placeholder="Search questions, tags, or owners…"
                className="w-72 bg-transparent text-xs outline-none placeholder:text-ink-500"
                onBlur={() => setTimeout(() => setShowSuggestions(false), 150)}
                onFocus={() => {
                  if (suggestions.length) setShowSuggestions(true);
                }}
              />
            </div>

            {showSuggestions && suggestions.length > 0 && (
              <div className="absolute left-0 mt-2 width-[22rem] max-h-72 overflow-auto rounded-xl border border-border-subtle/90 bg-surface-elevated/98 shadow-[0_18px_40px_rgba(15,23,42,0.9)]">
                <div className="border-b border-border-subtle/60 px-3 py-1.5 text-[0.65rem] uppercase tracking-[0.22em] text-ink-500">
                  Search results
                </div>
                {suggestions.map((s, idx) => (
                  <button
                    key={s.id || s.questionId || s.title || `${String(s)}-${idx}`}
                    type="button"
                    onMouseDown={() => handleSelectSuggestion(s)}
                    className="block w-full px-3 py-2 text-left text-xs text-ink-100 hover:bg-surface-subtle/80"
                  >
                    {s.title || s.name || s.query || String(s)}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right: actions */}
        <div className="flex items-center gap-2 md:gap-3">
          <Link
            to="/ask"
            className="inline-flex items-center rounded-full bg-brand-500 px-3 py-1.5 text-xs md:text-sm font-medium text-white shadow-[0_12px_32px_rgba(79,107,255,0.9)] hover:bg-brand-400 hover:shadow-[0_16px_40px_rgba(79,107,255,1)] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-300 focus-visible:ring-offset-2 focus-visible:ring-offset-surface"
          >
            Ask Question
          </Link>

          {user ? (
            <>
              <div
                // to="/profile"
                className="hidden sm:inline-flex items-center rounded-full border border-border-subtle bg-surface-subtle/80 px-3 py-1.5 text-xs md:text-sm text-ink-100 hover:border-border-strong hover:bg-surface-subtle/95"
              >
				{`Welcome, ${user.name || "Profile"}`}
              </div>
              <button
                type="button"
                onClick={handleLogout}
                className="inline-flex items-center rounded-full border border-border-subtle bg-surface-subtle/60 px-3 py-1.5 text-xs md:text-sm text-ink-300 hover:text-ink-100 hover:border-border-strong hover:bg-surface-subtle/90"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="inline-flex items-center rounded-full border border-border-subtle bg-surface-subtle/60 px-3 py-1.5 text-xs md:text-sm text-ink-100 hover:border-border-strong hover:bg-surface-subtle/90"
              >
                Login
              </Link>
              <Link
                to="/signup"
                className="hidden sm:inline-flex items-center rounded-full border border-brand-500/70 bg-transparent px-3 py-1.5 text-xs md:text-sm font-medium text-brand-300 hover:bg-brand-500/10"
              >
                Signup
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
