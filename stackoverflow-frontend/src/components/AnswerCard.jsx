// src/components/AnswerCard.jsx
import React, { useState, useEffect } from "react";
import CommentCard from "./CommentCard";
import { S3_BASE_URL } from "../config/s3";
import { upVote, downVote } from "../api/api";
import { getCurrentUserId } from "../auth";

export default function AnswerCard({ a, onAddComment }) {
  const [commentText, setCommentText] = useState("");
  const [votes, setVotes] = useState(a.votes ?? 0);
  const [myVote, setMyVote] = useState(a.myVote ?? 0); // 👈 track my vote

  useEffect(() => {
    setVotes(a.votes ?? 0);
    setMyVote(a.myVote ?? 0); // if backend sends it
  }, [a.votes, a.myVote]);

  const upvoted = myVote === 1;
  const downvoted = myVote === -1;

  const imageUrl = a.mediaUrl
    ? `${S3_BASE_URL}${encodeURIComponent(a.mediaUrl)}`
    : null;

  async function handleUpVote() {
    const userId = getCurrentUserId();
    if (!userId) {
      window.location.href = `/login?redirect=${window.location.pathname}${window.location.search}`;
      return;
    }

    try {
      const { votes: updatedVotes, myVote: newMyVote } = await upVote(
        "ANSWER",
        a.id,
        userId
      );
      setVotes(updatedVotes);
      setMyVote(newMyVote);
    } catch (err) {
      console.error("Failed to upvote answer:", err);
      alert("Failed to vote");
    }
  }

  async function handleDownVote() {
    const userId = getCurrentUserId();
    if (!userId) {
      window.location.href = `/login?redirect=${window.location.pathname}${window.location.search}`;
      return;
    }

    try {
      const { votes: updatedVotes, myVote: newMyVote } = await downVote(
        "ANSWER",
        a.id,
        userId
      );
      setVotes(updatedVotes);
      setMyVote(newMyVote);
    } catch (err) {
      console.error("Failed to downvote answer:", err);
      alert("Failed to vote");
    }
  }

  async function submitComment(e) {
    e.preventDefault();
    if (!commentText.trim()) return;

    try {
      if (onAddComment) {
        await onAddComment(a.id, commentText);
      }
      setCommentText("");
    } catch (err) {
      console.error("Error posting comment:", err);
      alert("Failed to post comment");
    }
  }

  const authorName = a.user?.name || "anonymous";

  return (
    <div className="rounded-2xl border border-border-subtle bg-surface-elevated/85 px-4 py-3 md:px-5 md:py-4 shadow-[0_18px_40px_rgba(15,23,42,0.55)] hover:border-brand-400/70 transition-colors duration-200">
      <div className="flex gap-4">
        {/* LEFT: Vote controls */}
        <div className="flex flex-col items-center gap-1 w-10 md:w-12 .flex-shrink-0 pt-1">
          <button
            type="button"
            onClick={handleUpVote}
            title="Upvote"
            className={
              "inline-flex h-7 w-7 items-center justify-center rounded-full border text-xs transition-colors " +
              (upvoted
                ? "border-brand-400 bg-brand-500 text-white shadow-md"
                : "border-border-subtle bg-surface-subtle text-ink-400 hover:text-brand-300 hover:border-brand-400 hover:bg-surface-subtle/80")
            }
          >
            ▲
          </button>

          <div className="mt-1 text-sm md:text-base font-semibold text-ink-50">
            {votes}
          </div>

          <button
            type="button"
            onClick={handleDownVote}
            title="Downvote"
            className={
              "inline-flex h-7 w-7 items-center justify-center rounded-full border text-xs transition-colors " +
              (downvoted
                ? "border-brand-400 bg-brand-500 text-white shadow-md"
                : "border-border-subtle bg-surface-subtle text-ink-400 hover:text-ink-300 hover:border-border-strong hover:bg-surface-subtle/80")
            }
          >
            ▼
          </button>
        </div>

        {/* RIGHT: content + comments */}
        <div className="flex-1 text-sm text-ink-100 space-y-3">
          <div
            className="leading-relaxed"
            dangerouslySetInnerHTML={{ __html: a.content }}
          />

          {imageUrl && (
            <div className="mt-1 inline-block rounded-xl border border-border-subtle/80 bg-surface-subtle/50 p-2">
              <img
                src={imageUrl}
                alt="answer attachment"
                className="max-h-64 rounded-lg object-contain"
              />
            </div>
          )}

          <div className="pt-1 text-[0.7rem] uppercase tracking-[0.18em] text-ink-500">
            Answered by{" "}
            <span className="text-ink-200 font-medium">{authorName}</span>
          </div>

          {/* Existing comments */}
          {(a.comments || []).length > 0 && (
            <div className="mt-2 border-l border-border-subtle/80 pl-3 space-y-1.5">
              {(a.comments || []).map((c) => (
                <CommentCard key={c.id} comment={c} />
              ))}
            </div>
          )}

          {/* Add comment */}
          <form
            onSubmit={submitComment}
            className="mt-2 flex items-center gap-2 text-xs md:text-sm"
          >
            <input
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              className="flex-1 rounded-full border border-border-subtle bg-surface-subtle/70 px-3 py-1.5 text-ink-50 placeholder:text-ink-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-400 focus-visible:ring-offset-2 focus-visible:ring-offset-surface focus-visible:border-border-strong"
              placeholder="Add a comment for your team…"
            />
            <button
              type="submit"
              className="inline-flex items-center rounded-full bg-brand-500 px-3 py-1.5 text-xs font-medium text-white shadow-[0_10px_28px_rgba(79,107,255,0.65)] hover:bg-brand-400 hover:shadow-[0_14px_40px_rgba(79,107,255,0.85)] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-300 focus-visible:ring-offset-2 focus-visible:ring-offset-surface"
            >
              Add
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
