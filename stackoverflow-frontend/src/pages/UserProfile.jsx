import React, { useEffect, useState } from 'react'
import { api } from '../api/api'


export default function UserProfile(){
const user = JSON.parse(localStorage.getItem('user') || 'null')
const [questions, setQuestions] = useState([])
const [answers, setAnswers] = useState([])


useEffect(()=>{
if(!user) return
// Backend endpoints: fetch user's questions & answers
api.get(`/users/${user.id}`)
.then(res => {
setQuestions(res.data.questions || [])
setAnswers(res.data.answers || [])
})
.catch(err => console.error(err))
}, [])


if(!user) return <div>Please login</div>


return (
<div className="grid grid-cols-3 gap-4">
<div className="col-span-1 bg-white p-4 rounded shadow-sm">
<h3 className="font-bold">{user.name}</h3>
<div className="text-sm text-gray-600">{user.email}</div>
</div>
<div className="col-span-2 space-y-4">
<div className="bg-white p-4 rounded shadow-sm">
<h4 className="font-bold">Questions</h4>
<ul className="mt-2 space-y-2">
{questions.map(q=> <li key={q.id}>{q.title}</li>)}
</ul>
</div>
<div className="bg-white p-4 rounded shadow-sm">
<h4 className="font-bold">Answers</h4>
<ul className="mt-2 space-y-2">
{answers.map(a=> <li key={a.id} dangerouslySetInnerHTML={{__html: a.content}} />)}
</ul>
</div>
</div>
</div>
)
}