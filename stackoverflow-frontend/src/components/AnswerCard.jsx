// import React, { useState } from 'react';
// import CommentCard from './CommentCard';
// import { S3_BASE_URL } from '../config/s3';

// export default function AnswerCard({ a, onAddComment }) {
//   const [commentText, setCommentText] = useState("");

//   // Build full image URL if mediaUrl is present
//   const imageUrl = a.mediaUrl
//     ? `${S3_BASE_URL}${encodeURIComponent(a.mediaUrl)}`
//     : null;

//   async function submitComment(e) {
//     e.preventDefault();
//     if (!commentText.trim()) return;

//     try {
//       // call parent function (in QuestionDetails)
//       if (onAddComment) {
//         await onAddComment(a.id, commentText);
//       }

//       setCommentText(''); // clear input
//     } catch (err) {
//       console.error("Error posting comment:", err);
//       alert("Failed to post comment");
//     }
//   }

//   return (
//     <div className="bg-white p-3 rounded shadow-sm">

//       {/* TOP ROW: votes + content + comment count */}
//       <div className="flex gap-4">

//         {/* Votes */}
//         <div className="w-16 text-center">
//           <div className="text-xl font-bold">{a.votes ?? 0}</div>
//           <div className="text-gray-500 text-sm">votes</div>
//         </div>

//         {/* Answer content + media */}
//         <div className="flex-1">
//           <div
//             className="prose"
//             dangerouslySetInnerHTML={{ __html: a.content }}
//           />

//           {/* Image from S3 (if any) */}
//           {imageUrl && (
//             <div className="mt-2">
//               <img
//                 src={imageUrl}
//                 alt="answer attachment"
//                 className="max-h-64 rounded border border-gray-200"
//               />
//             </div>
//           )}

//           <div className="text-xs text-gray-500 mt-2">
//             answered by: {a.user?.name || 'anonymous'}
//           </div>
//         </div>

//         {/* Comment count */}
//         <div className="text-center">
//           <div className="text-sm text-gray-500">comments</div>
//           <div className="text-xl font-semibold text-gray-900">
//             {(a.comments || []).length}
//           </div>
//         </div>
//       </div>

//       {/* COMMENTS SECTION */}
//       {(a.comments || []).length > 0 && (
//         <div className="mt-2 border-l border-gray-200 pl-3">
//           {a.comments.map((c) => (
//             <CommentCard key={c.id} comment={c} />
//           ))}
//         </div>
//       )}

//       {/* ADD COMMENT FORM */}
//       <form onSubmit={submitComment} className="mt-2 flex gap-2 text-sm">
//         <input
//           value={commentText}
//           onChange={(e) => setCommentText(e.target.value)}
//           className="flex-1 border rounded px-2 py-1"
//           placeholder="Add a comment..."
//         />
//         <button
//           type="submit"
//           className="px-3 py-1 rounded bg-gray-100 hover:bg-gray-200 text-gray-800"
//         >
//           Add
//         </button>
//       </form>
//     </div>
//   );
// }


// src/components/AnswerCard.jsx
import React, { useState, useEffect } from 'react';
import CommentCard from './CommentCard';
import { S3_BASE_URL } from '../config/s3';
import { upVote, downVote } from '../api/api';
import { getCurrentUserId } from '../auth';

export default function AnswerCard({ a, onAddComment }) {
  const [commentText, setCommentText] = useState('');
  const [votes, setVotes] = useState(a.votes ?? 0);

  useEffect(() => {
    setVotes(a.votes ?? 0);
  }, [a.votes]);

  const imageUrl = a.mediaUrl
    ? `${S3_BASE_URL}${encodeURIComponent(a.mediaUrl)}`
    : null;

  async function handleUpVote() {
    const userId = getCurrentUserId();
    if (!userId) {
      // redirect to login and come back to this question details page
      window.location.href = `/login?redirect=${window.location.pathname}${window.location.search}`;
      return;
    }

    try {
      const newVotes = await upVote('ANSWER', a.id, userId);
      setVotes(newVotes);
    } catch (err) {
      console.error('Failed to upvote answer:', err);
      alert('Failed to vote');
    }
  }

  async function handleDownVote() {
    const userId = getCurrentUserId();
    if (!userId) {
      window.location.href = `/login?redirect=${window.location.pathname}${window.location.search}`;
      return;
    }

    try {
      const newVotes = await downVote('ANSWER', a.id, userId);
      setVotes(newVotes);
    } catch (err) {
      console.error('Failed to downvote answer:', err);
      alert('Failed to vote');
    }
  }

  async function submitComment(e) {
    e.preventDefault();
    if (!commentText.trim()) return;

    try {
      if (onAddComment) {
        await onAddComment(a.id, commentText);
      }
      setCommentText('');
    } catch (err) {
      console.error('Error posting comment:', err);
      alert('Failed to post comment');
    }
  }

  return (
    <div className="bg-white p-3 rounded shadow-sm">
      <div className="flex gap-4">
        {/* LEFT: Vote controls */}
        <div className="flex flex-col items-center w-12 flex-shrink-0">
          <button
            type="button"
            onClick={handleUpVote}
            className="text-gray-500 hover:text-orange-500"
            title="Upvote"
          >
            ▲
          </button>
          <div className="text-xl font-bold">{votes}</div>
          <button
            type="button"
            onClick={handleDownVote}
            className="text-gray-500 hover:text-blue-500"
            title="Downvote"
          >
            ▼
          </button>
        </div>

        {/* RIGHT: content + comments */}
        <div className="flex-1">
          <div
            className="prose"
            dangerouslySetInnerHTML={{ __html: a.content }}
          />

          {imageUrl && (
            <div className="mt-2">
              <img
                src={imageUrl}
                alt="answer attachment"
                className="max-h-64 rounded border border-gray-200"
              />
            </div>
          )}

          <div className="text-xs text-gray-500 mt-2">
            answered by: {a.user?.name || 'anonymous'}
          </div>

          {/* Existing comments */}
          {(a.comments || []).length > 0 && (
            <div className="mt-2 border-l border-gray-200 pl-3">
              {(a.comments || []).map((c) => (
                <CommentCard key={c.id} comment={c} />
              ))}
            </div>
          )}

          {/* Add comment */}
          <form onSubmit={submitComment} className="mt-2 flex gap-2 text-sm">
            <input
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              className="flex-1 border rounded px-2 py-1"
              placeholder="Add a comment..."
            />
            <button
              type="submit"
              className="px-3 py-1 rounded bg-gray-100 hover:bg-gray-200 text-gray-800"
            >
              Add
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
