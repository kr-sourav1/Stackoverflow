// src/components/CommentCard.jsx
import React from "react";

export default function CommentCard({ comment }) {
  const authorName = comment.user?.name || "anonymous";

  return (
    <div className="flex items-start gap-2 py-1.5 text-xs">
      <p className="flex-1 text-[0.78rem] leading-snug text-ink-200">
        {comment.text}
        <span className="ml-2 text-[0.7rem] text-ink-500">
          —{" "}
          <span className="font-medium text-brand-300 hover:text-brand-200 cursor-pointer">
            {authorName}
          </span>
        </span>
      </p>
    </div>
  );
}
