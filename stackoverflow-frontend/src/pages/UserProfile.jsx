import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/api";

export default function UserProfile() {
  const user = JSON.parse(localStorage.getItem("user") || "null");
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user) return;

    async function loadProfile() {
      try {
        setLoading(true);
        setError("");
        const res = await api.get(`/users/${user.id}`);
        setQuestions(res.data.questions || []);
        setAnswers(res.data.answers || []);
      } catch (err) {
        console.error(err);
        setError("Failed to load profile data.");
      } finally {
        setLoading(false);
      }
    }

    loadProfile();
  }, [user?.id]);

  if (!user) {
    return (
      <div className="max-w-4xl mx-auto px-6 py-10">
        <div className="rounded-2xl border border-border-subtle/80 bg-surface-elevated/70 px-6 py-6 text-sm text-ink-300 shadow-[0_18px_60px_rgba(15,23,42,0.9)]">
          <h2 className="text-lg font-semibold text-ink-50">
            You’re not signed in
          </h2>
          <p className="mt-2 text-xs text-ink-400">
            Sign in to view your questions, answers, and activity.
          </p>
          <div className="mt-4 flex gap-3">
            <Link
              to="/login"
              className="inline-flex items-center rounded-lg bg-brand-500 px-4 py-2 text-xs font-medium text-white hover:bg-brand-400"
            >
              Go to Login
            </Link>
            <Link
              to="/signup"
              className="inline-flex items-center rounded-lg border border-border-subtle px-4 py-2 text-xs font-medium text-ink-200 hover:border-brand-500/70 hover:text-brand-300"
            >
              Create Account
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-6 py-8">
      {/* Header */}
      <div className="mb-6 flex items-center justify-between gap-4">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.25em] text-ink-500">
            Profile
          </p>
          <h1 className="mt-2 text-2xl font-semibold text-ink-50">
            {user.name || "Your account"}
          </h1>
          <p className="mt-1 text-xs text-ink-400">
            Overview of your questions, answers, and activity across the
            workspace.
          </p>
        </div>
      </div>

      {error && (
        <div className="mb-4 rounded-md border border-accent-red/50 bg-accent-red/10 px-4 py-3 text-xs text-accent-red">
          {error}
        </div>
      )}

      {loading ? (
        <div className="text-sm text-ink-400">Loading profile…</div>
      ) : (
        <div className="grid gap-5 lg:grid-cols-[280px,minmax(0,1fr)]">
          {/* Left: User summary */}
          <aside className="rounded-2xl border border-border-subtle/80 bg-surface-elevated/70 px-5 py-5 shadow-[0_18px_60px_rgba(15,23,42,0.9)]">
            <div className="flex items-center gap-4">
              <div className="flex h-12 w-12 items-center justify-center rounded-full .bg-gradient-to-br from-brand-500 to-accent-blue text-sm font-semibold text-white">
                {user.name
                  ? user.name
                      .split(" ")
                      .map((p) => p[0])
                      .join("")
                      .slice(0, 2)
                  : "U"}
              </div>
              <div>
                <div className="text-sm font-semibold text-ink-50">
                  {user.name}
                </div>
                <div className="text-xs text-ink-400">{user.email}</div>
              </div>
            </div>

            <div className="mt-5 space-y-3 border-t border-border-subtle/80 pt-4 text-xs text-ink-400">
              <div className="flex items-center justify-between">
                <span>Questions asked</span>
                <span className="text-ink-50 font-medium">
                  {questions.length}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span>Answers posted</span>
                <span className="text-ink-50 font-medium">
                  {answers.length}
                </span>
              </div>
            </div>
          </aside>

          {/* Right: Activity */}
          <main className="space-y-5">
            {/* Questions */}
            <section className="rounded-2xl border border-border-subtle/80 bg-surface-elevated/60 px-5 py-5 shadow-[0_18px_60px_rgba(15,23,42,0.9)]">
              <div className="flex items-center justify-between gap-2">
                <h2 className="text-sm font-semibold text-ink-50">
                  Questions
                </h2>
                <span className="rounded-full bg-surface-subtle px-2.5 py-0.5 text-[11px] text-ink-400">
                  {questions.length} total
                </span>
              </div>

              {questions.length === 0 ? (
                <p className="mt-3 text-xs text-ink-400">
                  You haven’t asked any questions yet.
                </p>
              ) : (
                <ul className="mt-3 space-y-2 text-sm">
                  {questions.map((q) => (
                    <li
                      key={q.id}
                      className="rounded-md border border-border-subtle/80 bg-surface-subtle px-3 py-2 text-ink-100 hover:border-brand-500/60 hover:bg-surface-subtle/70"
                    >
                      {q.title}
                    </li>
                  ))}
                </ul>
              )}
            </section>

            {/* Answers */}
            <section className="rounded-2xl border border-border-subtle/80 bg-surface-elevated/60 px-5 py-5 shadow-[0_18px_60px_rgba(15,23,42,0.9)]">
              <div className="flex items-center justify-between gap-2">
                <h2 className="text-sm font-semibold text-ink-50">
                  Answers
                </h2>
                <span className="rounded-full bg-surface-subtle px-2.5 py-0.5 text-[11px] text-ink-400">
                  {answers.length} total
                </span>
              </div>

              {answers.length === 0 ? (
                <p className="mt-3 text-xs text-ink-400">
                  You haven’t posted any answers yet.
                </p>
              ) : (
                <ul className="mt-3 space-y-3 text-xs text-ink-100">
                  {answers.map((a) => (
                    <li
                      key={a.id}
                      className="rounded-md border border-border-subtle/80 bg-surface-subtle px-3 py-2"
                      dangerouslySetInnerHTML={{ __html: a.content }}
                    />
                  ))}
                </ul>
              )}
            </section>
          </main>
        </div>
      )}
    </div>
  );
}
