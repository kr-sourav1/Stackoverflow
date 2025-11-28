import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getQuestionById, postAnswer, postComment } from '../api/api';
import AnswerCard from '../components/AnswerCard';
import Tag from '../components/Tag';


export default function QuestionDetails() {
    const { id } = useParams();

    const [question, setQuestion] = useState(null);
    const [answerText, setAnswerText] = useState('');
    const [answerFile, setAnswerFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);          // 👈 you were using this without defining


    // ✅ Load question by ID
    useEffect(() => {
        async function loadQuestion() {
            setLoading(true);
            setError(null);

            try {
                const data = await getQuestionById(id);      // calls /question/findById/{id} in api.js

                if (!data) {
                    throw new Error('No question found');
                }

                setQuestion(data);
            } catch (err) {
                console.error('Error fetching question:', err);
                setError('Failed to load question');
            } finally {
                setLoading(false);
            }
        }

        loadQuestion();
    }, [id]);

    // ✅ Submit answer
    async function submitAnswer(e) {
        e.preventDefault();

        try {
            await postAnswer({
                questionId: id,
                answer: answerText,
                file: answerFile,
            });

            setAnswerText('');
            setAnswerFile(null);

            const data = await getQuestionById(id);
            setQuestion(data);
        } catch (err) {
            console.error('Error posting answer:', err);
            alert('Failed to post answer');
        }
    }

    async function handleAddComment(answerId, text) {
        try {
            await postComment({ answerId, comment: text }); // calls /comment/post

            // reload updated question so new comment appears
            const data = await getQuestionById(id);
            setQuestion(data);
        } catch (err) {
            console.error('Error posting comment:', err);
            alert('Failed to post comment');
        }
    }



    // ✅ UI states
    if (loading) return <div className="p-4">Loading...</div>;
    if (error) return <div className="p-4 text-red-600">{error}</div>;
    if (!question) return <div className="p-4">Question not found</div>;
    
    return (
        <div className="space-y-4">
            <div className="bg-white p-4 rounded shadow-sm">
                <h2 className="text-2xl font-bold">{question.title}</h2>

                <div
                    className="mt-2 prose"
                    dangerouslySetInnerHTML={{ __html: question.content }}
                />

                <div className="mt-3 flex gap-2 flex-wrap">
                    {(question.tags || []).map((t) => (
                        // 👇 backend gives { id, name }, not raw strings
                        <Tag key={t.id} name={t.name} />
                    ))}
                </div>
            </div>

            <section className="space-y-2">
                <h3 className="text-xl">
                    Answers ({(question.answers || []).length})
                </h3>

                <div className="space-y-2">
                    {(question.answers || []).map((a) => (
                        <AnswerCard
                            key={a.id}
                            a={a}
                            onAddComment={handleAddComment}
                        />
                    ))}
                </div>

                <form
                    onSubmit={submitAnswer}
                    className="bg-white p-4 rounded space-y-2"
                >
                    <label className="block font-medium">Your Answer</label>
                    <textarea
                        value={answerText}
                        onChange={(e) => setAnswerText(e.target.value)}
                        className="w-full border p-2 rounded"
                        rows={6}
                    />
                    {/* ✅ NEW: file input (optional) */}
                    <div>
                        <label className="block text-sm mb-1">Attach file (optional)</label>
                        <input
                            type="file"
                            onChange={(e) => setAnswerFile(e.target.files[0] || null)}  // ✅ CHANGED HERE
                            className="text-sm"
                        />
                    </div>
                    <div className="flex gap-2">
                        <button className="px-4 py-2 bg-indigo-600 text-white rounded">
                            Post Answer
                        </button>
                    </div>
                </form>
            </section>
        </div>
    );
}

