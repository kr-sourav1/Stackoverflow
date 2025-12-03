import React, { useState } from "react";
import { postQuestion } from "../api/api";
import { useNavigate } from "react-router-dom";

export default function AskQuestion() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [tags, setTags] = useState("");
  const [file, setFile] = useState(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  async function submit(e) {
    e.preventDefault();
    setError("");

    const tagsArray = tags
      .split(",")
      .map((t) => t.trim())
      .filter(Boolean);

    try {
      const response = await postQuestion({
        title,
        content,
        tags: tagsArray,
        file,
      });

      if (response?.status === 201) {
        alert("Question Posted Successfully");
        navigate("/");
      } else if (
        response?.code === "ERR_BAD_REQUEST" &&
        response.response?.data
      ) {
        alert(response.response.data.message);
      } else {
        setError("Failed to post question");
      }
    } catch (err) {
      console.error(err);
      setError("Failed to post question");
    }
  }

  return (
    <div className="max-w-5xl mx-auto px-6 py-8">
      {/* Header */}
      <header className="mb-8">
        <p className="text-[11px] font-semibold uppercase tracking-[0.25em] text-ink-500">
          Ask a question
        </p>
        <h1 className="mt-2 text-3xl font-semibold text-ink-50">
          Ask a public question
        </h1>
        <p className="mt-2 text-sm text-ink-400 max-w-2xl">
          Get help from engineers by clearly describing your problem, what
          you’ve tried, and sharing any relevant code or screenshots.
        </p>
      </header>

      <div className="grid gap-6 md:grid-cols-[minmax(0,2fr)_minmax(0,1.2fr)]">
        {/* Form card */}
        <form
          onSubmit={submit}
          className="space-y-5 rounded-xl border border-border-subtle/80 bg-surface-elevated/60 p-6 shadow-[0_18px_60px_rgba(15,23,42,0.8)]"
        >
          {error && (
            <div className="mb-2 rounded-md border border-accent-red/50 bg-accent-red/10 px-3 py-2 text-sm text-accent-red">
              {error}
            </div>
          )}

          {/* Title */}
          <div className="space-y-1.5">
            <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
              Title <span className="text-accent-red">*</span>
            </label>
            <p className="text-[11px] text-ink-500">
              Be specific and imagine you’re asking a question to another
              engineer.
            </p>
            <input
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full rounded-lg border border-border-subtle bg-surface-subtle px-3 py-2 text-sm text-ink-50 placeholder:text-ink-500 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500/80"
              placeholder="e.g. Why is my Spring WebFlux endpoint returning 404 for POST requests?"
              required
            />
          </div>

          {/* Details */}
          <div className="space-y-1.5">
            <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
              Details <span className="text-accent-red">*</span>
            </label>
            <p className="text-[11px] text-ink-500">
              Include what you’re trying to do, what you’ve tried, error
              messages, and relevant code snippets.
            </p>
            <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              className="w-full rounded-lg border border-border-subtle bg-surface-subtle px-3 py-2 text-sm text-ink-50 placeholder:text-ink-500 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500/80"
              rows={10}
              placeholder={`Describe your problem in detail...

- What are you trying to achieve?
- What have you already tried?
- What did you expect to happen?
- What actually happened?`}
              required
            />
          </div>

          {/* Tags */}
          <div className="space-y-1.5">
            <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
              Tags
            </label>
            <p className="text-[11px] text-ink-500">
              Add up to 5 tags to describe what your question is about. Use
              commas to separate tags.
            </p>
            <input
              value={tags}
              onChange={(e) => setTags(e.target.value)}
              className="w-full rounded-lg border border-border-subtle bg-surface-subtle px-3 py-2 text-sm text-ink-50 placeholder:text-ink-500 focus:outline-none focus:ring-1 focus:ring-brand-500 focus:border-brand-500/80"
              placeholder="java, spring-boot, react, tailwindcss"
            />
          </div>

          {/* Media upload */}
          <div className="space-y-1.5">
            <label className="block text-xs font-medium uppercase tracking-wide text-ink-300">
              Attach media (optional)
            </label>
            <p className="text-[11px] text-ink-500">
              Screenshots or small clips that help explain your problem.
            </p>
            <input
              type="file"
              accept="image/*,video/*"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              className="block w-full text-xs text-ink-400 file:mr-3 file:rounded-md file:border-0 file:bg-brand-500/90 file:px-3 file:py-1.5 file:text-xs file:font-medium file:text-white hover:file:bg-brand-400/90"
            />
          </div>

          {/* Actions */}
          <div className="pt-2 flex items-center gap-3">
            <button
              type="submit"
              className="inline-flex items-center rounded-lg bg-brand-500 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-brand-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2 focus-visible:ring-offset-surface"
            >
              Post your question
            </button>
            <button
              type="button"
              onClick={() => navigate("/")}
              className="text-xs text-ink-400 hover:text-ink-200 underline-offset-2 hover:underline"
            >
              Cancel and go back
            </button>
          </div>
        </form>

        {/* Right panel: helper / guidelines */}
        <aside className="hidden md:block rounded-xl border border-border-subtle/50 bg-surface-subtle/60 p-5 text-sm text-ink-300">
          <h2 className="text-xs font-semibold uppercase tracking-[0.2em] text-ink-400">
            How to ask a great question
          </h2>
          <p className="mt-2 text-xs text-ink-400">
            Your question will be shared with other engineers across your
            organization. To get high-quality answers:
          </p>

          <ul className="mt-3 space-y-2 text-xs text-ink-300">
            <li>• Use a clear, searchable title.</li>
            <li>• Focus on a single, specific problem.</li>
            <li>• Include minimal reproducible code if possible.</li>
            <li>• Share inputs, expected output, and actual output.</li>
            <li>• Remove secrets, keys, and sensitive data.</li>
          </ul>

          <div className="mt-4 rounded-lg border border-brand-500/40 bg-brand-500/10 px-3 py-2 text-[11px] text-brand-100">
            Questions with clear context and examples get answered faster.
          </div>
        </aside>
      </div>
    </div>
  );
}
