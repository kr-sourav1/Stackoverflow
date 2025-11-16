import React, { useState } from 'react'
import { api } from '../api/api'
import { useNavigate } from 'react-router-dom'


export default function AskQuestion(){
const [title, setTitle] = useState('')
const [content, setContent] = useState('')
const [tags, setTags] = useState('')
const navigate = useNavigate()


function submit(e){
e.preventDefault()
const payload = {
title,
content,
tags: tags.split(',').map(t => t.trim()).filter(Boolean),
userName: 'anonymous'
}
api.post('/questions', payload)
.then(res => navigate(`/question/${res.data.id}`))
.catch(err => console.error(err))
}


return (
<div className="max-w-3xl mx-auto">
<h1 className="text-2xl font-bold mb-4">Ask a public question</h1>
<form onSubmit={submit} className="space-y-4 bg-white p-4 rounded shadow-sm">
<div>
<label className="block text-sm font-medium">Title</label>
<input value={title} onChange={e=>setTitle(e.target.value)} className="w-full border p-2 rounded" placeholder="Be specific and imagine you’re asking a question to another person" />
</div>
<div>
<label className="block text-sm font-medium">Details</label>
<textarea value={content} onChange={e=>setContent(e.target.value)} className="w-full border p-2 rounded" rows={8} />
</div>
<div>
<label className="block text-sm font-medium">Tags (comma separated)</label>
<input value={tags} onChange={e=>setTags(e.target.value)} className="w-full border p-2 rounded" placeholder="javascript, react" />
</div>
<div>
<button className="px-4 py-2 bg-indigo-600 text-white rounded">Post Your Question</button>
</div>
</form>
</div>
)
}