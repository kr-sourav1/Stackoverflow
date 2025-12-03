// src/components/QuestionCard.jsx
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Tag from "./Tag";
import { upVote, downVote } from "../api/api";
import { getCurrentUserId } from "../auth";
import { S3_BASE_URL } from "../config/s3";

export default function QuestionCard({
  q,
  showVoting = false, // detail page: show ▲▼
  showFullContent = false, // detail vs list
}) {
  const createdDate = q.createdAt
    ? new Date(q.createdAt).toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric",
      })
    : "unknown";

  const [votes, setVotes] = useState(q.votes ?? 0);
  const [myVote, setMyVote] = useState(q.myVote ?? 0); // 👈 track my vote

  useEffect(() => {
    setVotes(q.votes ?? 0);
    setMyVote(q.myVote ?? 0); // if backend sends it on initial load
  }, [q.votes, q.myVote]);

  const upvoted = myVote === 1;
  const downvoted = myVote === -1;

  const rawContent =
    q?.content ?? q?.body ?? q?.description ?? q?.contentText ?? "";
  const contentStr =
    typeof rawContent === "string"
      ? rawContent
      : rawContent
      ? String(rawContent)
      : "";
  const contentTrimmed = contentStr.trim();

  const mediaUrl = q.mediaUrl
    ? q.mediaUrl.startsWith("http")
      ? q.mediaUrl
      : `${S3_BASE_URL}${q.mediaUrl}`
    : null;

  async function handleUpVote() {
    const userId = getCurrentUserId();
    if (!userId) {
      window.location.href = `/login?redirect=/question/findById/${q.id}`;
      return;
    }

    try {
      // 👇 backend returns { votes, myVote }
      const { votes: updatedVotes, myVote: newMyVote } = await upVote(
        "QUESTION",
        q.id,
        userId
      );
      setVotes(updatedVotes);
      setMyVote(newMyVote);
    } catch (err) {
      console.error("Failed to upvote question:", err);
      alert("Failed to vote");
    }
  }

  async function handleDownVote() {
    const userId = getCurrentUserId();
    if (!userId) {
      window.location.href = `/login?redirect=/question/findById/${q.id}`;
      return;
    }

    try {
      const { votes: updatedVotes, myVote: newMyVote } = await downVote(
        "QUESTION",
        q.id,
        userId
      );
      setVotes(updatedVotes);
      setMyVote(newMyVote);
    } catch (err) {
      console.error("Failed to downvote question:", err);
      alert("Failed to vote");
    }
  }

  return (
    <article className="group rounded-2xl border border-border-subtle/80 bg-surface-elevated/60 px-4 py-4 shadow-[0_18px_40px_rgba(15,23,42,0.75)] transition-all .hover:-translate-y-[1px] hover:border-border-strong/80 hover:bg-surface-elevated/95">
      <div className="flex gap-4">
        {/* LEFT: votes / answers */}
        {showVoting ? (
          <div className="flex w-16 flex-col items-center gap-3 pt-1 text-[0.7rem] text-ink-400">
            <button
              type="button"
              onClick={handleUpVote}
              title="Upvote"
              className={
                "flex h-7 w-7 items-center justify-center rounded-full border text-xs transition-colors " +
                (upvoted
                  ? "border-brand-400 bg-brand-500 text-white shadow-md"
                  : "border-border-subtle bg-surface-subtle/70 text-ink-200 hover:border-brand-400 hover:bg-brand-500/10 hover:text-brand-300")
              }
            >
              ▲
            </button>

            <div className="text-center">
              <div className="uppercase tracking-[0.18em] text-[0.6rem]">
                votes
              </div>
              <div className="text-lg font-semibold text-ink-50">
                {votes}
              </div>
            </div>

            <button
              type="button"
              onClick={handleDownVote}
              title="Downvote"
              className={
                "flex h-7 w-7 items-center justify-center rounded-full border text-xs transition-colors " +
                (downvoted
                  ? "border-brand-400 bg-brand-500 text-white shadow-md"
                  : "border-border-subtle bg-surface-subtle/70 text-ink-200 hover:border-brand-400 hover:bg-brand-500/10 hover:text-brand-300")
              }
            >
              ▼
            </button>

            <div className="mt-2 text-center">
              <div className="uppercase tracking-[0.18em] text-[0.6rem]">
                answers
              </div>
              <div className="text-lg font-semibold text-ink-50">
                {(q.answers || []).length}
              </div>
            </div>
          </div>
        ) : (
          <div className="flex w-24 flex-col gap-2 pt-1 text-[0.7rem] text-ink-400">
            <div className="rounded-xl border border-border-subtle/70 bg-surface-subtle/40 px-2 py-1 text-center">
              <div className="uppercase tracking-[0.18em] text-[0.6rem]">
                votes
              </div>
              <div className="text-lg font-semibold text-ink-50">
                {votes}
              </div>
            </div>
            <div className="rounded-xl border border-border-subtle/70 bg-surface-subtle/40 px-2 py-1 text-center">
              <div className="uppercase tracking-[0.18em] text-[0.6rem]">
                answers
              </div>
              <div className="text-lg font-semibold text-ink-50">
                {(q.answers || []).length}
              </div>
            </div>
          </div>
        )}

        {/* CENTER: title + body + tags */}
        <div className="flex-1">
          {showFullContent ? (
            <h2 className="mb-1 text-lg md:text-2xl font-semibold tracking-tight text-ink-50">
              {q.title}
            </h2>
          ) : (
            <Link
              to={`/question/findById/${q.id}`}
              className="mb-1 line-clamp-2 text-base md:text-lg font-semibold tracking-tight text-brand-200 hover:text-brand-100"
            >
              {q.title}
            </Link>
          )}

          {showFullContent ? (
            <div
              className="mt-2 prose prose-invert max-w-none text-sm leading-relaxed text-ink-100"
              dangerouslySetInnerHTML={{ __html: contentTrimmed }}
            />
          ) : (
            <p className="mb-3 line-clamp-2 text-xs md:text-sm leading-relaxed text-ink-300">
              {contentTrimmed
                ? contentTrimmed.slice(0, 260)
                : "No content available"}
            </p>
          )}

          {/* Media preview */}
          {mediaUrl && (
            <div className="mb-3 flex flex-col gap-1">
              <img
                src={mediaUrl}
                alt="attachment"
                style={{ maxWidth: "50%", height: "auto" }}
                className="rounded-lg border border-border-subtle/80 bg-surface-subtle/30 object-contain"
                onError={(e) => {
                  e.target.style.display = "none";
                }}
              />
              {showFullContent && (
                <a
                  href={mediaUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="text-[0.7rem] text-brand-300 hover:text-brand-200 hover:underline"
                >
                  Open attachment in new tab
                </a>
              )}
            </div>
          )}

          {/* Tags */}
          <div className="mt-2 flex flex-wrap gap-2">
            {(q.tags || []).map((t) => {
              const tagName =
                typeof t === "string" ? t : t.name || String(t);
              const key = typeof t === "string" ? t : t.id ?? tagName;
              return <Tag key={key} name={tagName} />;
            })}
          </div>
        </div>

        {/* RIGHT: owner + date */}
        <div className="flex flex-col items-end justify-between gap-2 text-right text-[0.7rem] text-ink-400">
          <div>
            <div className="text-[0.7rem] uppercase tracking-[0.22em] text-ink-500">
              asked by
            </div>
            <div className="text-xs font-medium text-ink-100">
              {q.user?.name || q.userName || "anonymous"}
            </div>
            <div className="text-[0.7rem] text-ink-500">{createdDate}</div>
          </div>
        </div>
      </div>
    </article>
  );
}
