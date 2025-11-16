import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'


export default function Signup(){
const [name, setName] = useState('')
const [email, setEmail] = useState('')
const [password, setPassword] = useState('')
const navigate = useNavigate()


function submit(e){
e.preventDefault()
// No real backend signup; store locally
const user = { id: Date.now(), name, email }
localStorage.setItem('user', JSON.stringify(user))
navigate('/')
}


return (
<div className="max-w-md mx-auto bg-white p-6 rounded shadow-sm">
<h2 className="text-xl font-bold mb-4">Sign up</h2>
<form onSubmit={submit} className="space-y-3">
<input value={name} onChange={e=>setName(e.target.value)} placeholder="Your name" className="w-full border p-2 rounded" />
<input value={email} onChange={e=>setEmail(e.target.value)} placeholder="Email" className="w-full border p-2 rounded" />
<input value={password} onChange={e=>setPassword(e.target.value)} type="password" placeholder="Password" className="w-full border p-2 rounded" />
<div className="flex justify-end">
<button className="px-4 py-2 bg-indigo-600 text-white rounded">Sign up</button>
</div>
</form>
</div>
)
}