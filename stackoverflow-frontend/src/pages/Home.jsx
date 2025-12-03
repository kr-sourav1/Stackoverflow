import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { api, getTopQuestions, globalSearch } from "../api/api";
import QuestionCard from "../components/QuestionCard";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

function extractQuestions(data) {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.questions)) return data.questions;
  return [];
}

export default function Home() {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const q = useQuery();
  const search = q.get("q") || "";
  const tag = q.get("tag") || "";

  useEffect(() => {
    setLoading(true);
    setError(null);

    console.log("Home effect run, search=", search, "tag=", tag);

    // 1) Text search → globalSearch
    if (search) {
      globalSearch(search)
        .then((res) => {
          console.log("globalSearch response:", res && res.data);
          setQuestions(extractQuestions(res?.data));
        })
        .catch((err) => {
          console.error(err);
          setError("Failed to load search results");
        })
        .finally(() => setLoading(false));
      return;
    }

    // 2) Tag filter
    if (tag) {
      const params = { tag };
      api
        .get("/questions", { params })
        .then((res) => setQuestions(extractQuestions(res?.data)))
        .catch((err) => {
          console.error(err);
          setError("Failed to load questions");
        })
        .finally(() => setLoading(false));
      return;
    }

    // 3) Default → top questions
    getTopQuestions(10)
      .then((res) => setQuestions(extractQuestions(res?.data)))
      .catch((err) => {
        console.error(err);
        setError("Failed to load top questions");
      })
      .finally(() => setLoading(false));
  }, [search, tag]);

  return (
    <div className="max-w-6xl mx-auto px-6 py-8">
      {/* Header / filters */}
      <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.25em] text-ink-500">
            Q&amp;A workspace
          </p>
          <h1 className="mt-1 text-3xl font-semibold text-ink-50">
            {search
              ? "Search results"
              : tag
              ? `Questions tagged “${tag}”`
              : "Top questions"}
          </h1>
          <p className="mt-2 text-sm text-ink-400">
            {search
              ? `Showing questions matching “${search}”.`
              : "Live questions from your engineering teams."}
          </p>
        </div>

        {(tag || search) && (
          <div className="inline-flex items-center gap-2 rounded-full border border-border-subtle/80 bg-surface-elevated/60 px-4 py-2 text-xs text-ink-300">
            <span className="rounded-full bg-brand-500/15 px-2 py-0.5 text-[11px] font-medium text-brand-200">
              {search ? "Search" : "Tag filter"}
            </span>
            <span className="truncate">
              {search ? `“${search}”` : `tag: ${tag}`}
            </span>
          </div>
        )}
      </div>

      {/* Content */}
      {loading ? (
        <div className="py-12 text-center text-sm text-ink-400">
          Loading questions…
        </div>
      ) : error ? (
        <div className="mb-4 rounded-md border border-accent-red/50 bg-accent-red/10 px-4 py-3 text-sm text-accent-red">
          {error}
        </div>
      ) : questions.length === 0 ? (
        <div className="py-12 text-center">
          <p className="mb-2 text-lg font-medium text-ink-50">
            No questions found
          </p>
          <p className="text-sm text-ink-400">
            Try a different search term or remove filters.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {questions.map((item) => (
            <QuestionCard key={item.id} q={item} />
          ))}
        </div>
      )}
    </div>
  );
}
