import React from 'react';

export default function CommentCard({ comment }) {
  const authorName = comment.user?.name || 'anonymous';

  return (
    <div className="text-xs text-gray-700 flex py-1 border-b border-gray-100 last:border-b-0">
      <p className="flex-1">
        {comment.text}{' '}
        <span className="text-[11px] text-gray-500 ml-2">
          —{' '}
          <span className="text-blue-600 hover:text-blue-800 cursor-pointer">
            {authorName}
          </span>
        </span>
      </p>
    </div>
  );
}
