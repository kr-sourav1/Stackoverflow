// src/pages/QuestionDetails.jsx
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getQuestionById, postAnswer, postComment } from "../api/api";
import AnswerCard from "../components/AnswerCard";
import QuestionCard from "../components/QuestionCard";

export default function QuestionDetails() {
  const { id } = useParams();

  const [question, setQuestion] = useState(null);
  const [answerText, setAnswerText] = useState("");
  const [answerFile, setAnswerFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Load question by ID
  useEffect(() => {
    async function loadQuestion() {
      setLoading(true);
      setError(null);

      try {
        const data = await getQuestionById(id);

        if (!data) {
          throw new Error("No question found");
        }

        setQuestion(data);
      } catch (err) {
        console.error("Error fetching question:", err);
        setError("Failed to load question");
      } finally {
        setLoading(false);
      }
    }

    loadQuestion();
  }, [id]);

  // Submit answer
  async function submitAnswer(e) {
    e.preventDefault();

    try {
      await postAnswer({
        questionId: id,
        answer: answerText,
        file: answerFile,
      });

      setAnswerText("");
      setAnswerFile(null);

      const data = await getQuestionById(id);
      setQuestion(data);
    } catch (err) {
      console.error("Error posting answer:", err);
      alert("Failed to post answer");
    }
  }

  async function handleAddComment(answerId, text) {
    try {
      await postComment({ answerId, comment: text });

      const data = await getQuestionById(id);
      setQuestion(data);
    } catch (err) {
      console.error("Error posting comment:", err);
      alert("Failed to post comment");
    }
  }

  // UI states
  if (loading)
    return (
      <div className="max-w-6xl mx-auto px-6 py-8 text-sm text-ink-400">
        Loading question…
      </div>
    );

  if (error)
    return (
      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="rounded-md border border-accent-red/50 bg-accent-red/10 px-4 py-3 text-sm text-accent-red">
          {error}
        </div>
      </div>
    );

  if (!question)
    return (
      <div className="max-w-6xl mx-auto px-6 py-8 text-sm text-ink-400">
        Question not found.
      </div>
    );

  return (
    <div className="max-w-6xl mx-auto px-6 py-8 space-y-6">
      {/* Question header */}
      <header className="space-y-2">
        <p className="text-[11px] font-semibold uppercase tracking-[0.25em] text-ink-500">
          Question
        </p>
        <h1 className="text-2xl font-semibold text-ink-50">
          {question.title}
        </h1>
      </header>

      {/* Question card with voting + full body */}
      <QuestionCard q={question} showVoting showFullContent />

      {/* Answers section */}
      <section className="space-y-4">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-ink-50">
            Answers ({(question.answers || []).length})
          </h3>
        </div>

        <div className="space-y-3">
          {(question.answers || []).map((a) => (
            <AnswerCard key={a.id} a={a} onAddComment={handleAddComment} />
          ))}
          {(!question.answers || question.answers.length === 0) && (
            <p className="text-sm text-ink-400">
              No answers yet. Be the first to help.
            </p>
          )}
        </div>
      </section>

      {/* Add answer */}
      <section className="mt-6">
        <div className="rounded-xl border border-border-subtle/80 bg-surface-elevated/60 p-5 shadow-[0_18px_60px_rgba(15,23,42,0.8)]">
          <h3 className="mb-3 text-sm font-semibold text-ink-50">
            Your Answer
          </h3>

          <form onSubmit={submitAnswer} className="space-y-4">
            <div className="space-y-1.5">
              <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
                Details
              </label>
              <textarea
                value={answerText}
                onChange={(e) => setAnswerText(e.target.value)}
                className="w-full rounded-lg border border-border-subtle bg-surface-subtle px-3 py-2 text-sm text-ink-50 placeholder:text-ink-500 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500/80"
                rows={8}
                placeholder="Share your solution, reasoning, and any relevant code snippets…"
              />
            </div>

            <div className="space-y-1.5">
              <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
                Attach file (optional)
              </label>
              <input
                type="file"
                onChange={(e) => setAnswerFile(e.target.files[0] || null)}
                className="block w-full text-xs text-ink-400 file:mr-3 file:rounded-md file:border-0 file:bg-brand-500/90 file:px-3 file:py-1.5 file:text-xs file:font-medium file:text-white hover:file:bg-brand-400/90"
              />
              <p className="text-[11px] text-ink-500">
                Attach screenshots or logs that support your answer.
              </p>
            </div>

            <div className="flex gap-3 pt-1">
              <button
                type="submit"
                className="inline-flex items-center rounded-lg bg-brand-500 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-brand-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2 focus-visible:ring-offset-surface"
              >
                Post Answer
              </button>
            </div>
          </form>
        </div>
      </section>
    </div>
  );
}
