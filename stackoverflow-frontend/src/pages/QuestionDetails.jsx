import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api/api'
import AnswerCard from '../components/AnswerCard'
import Tag from '../components/Tag'


export default function QuestionDetails(){
const { id } = useParams()
const [question, setQuestion] = useState(null)
const [answerText, setAnswerText] = useState('')
const [loading, setLoading] = useState(false)


useEffect(()=>{
setLoading(true)
api.get(`/questions/${id}`)
.then(res => setQuestion(res.data))
.catch(err => console.error(err))
.finally(()=> setLoading(false))
}, [id])

function submitAnswer(e){
e.preventDefault()
const payload = {
questionId: id,
content: answerText,
mediaUrl: null,
userName: 'anonymous'
}
api.post('/answers', payload)
.then(()=>{
setAnswerText('')
return api.get(`/questions/${id}`)
})
.then(res => setQuestion(res.data))
.catch(err => console.error(err))
}


if(loading) return <div>Loading...</div>
if(!question) return <div>Question not found.</div>


return (
<div className="space-y-4">
<div className="bg-white p-4 rounded shadow-sm">
<h2 className="text-2xl font-bold">{question.title}</h2>
<div className="mt-2 prose" dangerouslySetInnerHTML={{__html: question.content}} />
<div className="mt-3 flex gap-2">{(question.tags||[]).map(t=> <Tag key={t} name={t} />)}</div>
</div>


<section className="space-y-2">
<h3 className="text-xl">Answers ({(question.answers||[]).length})</h3>
<div className="space-y-2">
{(question.answers||[]).map(a => <AnswerCard key={a.id} a={a} />)}
</div>


<form onSubmit={submitAnswer} className="bg-white p-4 rounded space-y-2">
<label className="block font-medium">Your Answer</label>
<textarea value={answerText} onChange={e=>setAnswerText(e.target.value)} className="w-full border p-2 rounded" rows={6} />
<div className="flex gap-2">
<button className="px-4 py-2 bg-indigo-600 text-white rounded">Post Answer</button>
</div>
</form>
</section>
</div>
)
}