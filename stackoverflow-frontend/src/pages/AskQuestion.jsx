import React, { useState } from 'react';
import { postQuestion } from '../api/api';
import { useNavigate } from 'react-router-dom';

export default function AskQuestion() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [tags, setTags] = useState('');
  const [file, setFile] = useState(null);        // 👈 NEW
  const [error, setError] = useState('');
  const navigate = useNavigate();

  async function submit(e) {
    e.preventDefault();
    setError('');

    const tagsArray = tags
      .split(',')
      .map((t) => t.trim())
      .filter(Boolean);

    const response = await postQuestion({
      title,
      content,
      tags: tagsArray,
      file,                                      // 👈 pass file
    });

    if (response?.status === 201) {
      alert('Question Posted Successfully');
      navigate('/');
    } else if (response?.code === 'ERR_BAD_REQUEST' && response.response?.data) {
      alert(response.response.data.message);
    } else {
      setError('Failed to post question');
    }
  }

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">Ask a public question</h1>

      {error && (
        <div className="mb-4 p-2 bg-red-100 text-red-700 rounded">
          {error}
        </div>
      )}

      <form
        onSubmit={submit}
        className="space-y-4 bg-white p-4 rounded shadow-sm"
      >
        <div>
          <label className="block text-sm font-medium">Title</label>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full border p-2 rounded"
            placeholder="Be specific and imagine you’re asking a question to another person"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium">Details</label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            className="w-full border p-2 rounded"
            rows={8}
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium">
            Tags (comma separated)
          </label>
          <input
            value={tags}
            onChange={(e) => setTags(e.target.value)}
            className="w-full border p-2 rounded"
            placeholder="javascript, react"
          />
        </div>

        {/* 👇 NEW: media upload */}
        <div>
          <label className="block text-sm font-medium mb-1">
            Attach media (optional)
          </label>
          <input
            type="file"
            accept="image/*,video/*"
            onChange={(e) => setFile(e.target.files?.[0] || null)}
            className="text-sm"
          />
          <p className="text-xs text-gray-500 mt-1">
            You can attach screenshots or small clips to clarify your question.
          </p>
        </div>

        <div>
          <button
            type="submit"
            style={{ backgroundColor: '#0095FF' }}
            className="px-4 py-2 bg-indigo-600 text-white rounded"
          >
            Post Your Question
          </button>
        </div>
      </form>
    </div>
  );
}
