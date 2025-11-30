import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { signup } from '../api/api'


export default function Signup() {
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate()


    async function submit(e) {
        e.preventDefault()
        // No real backend signup; store locally
        try {
            const response = await signup({
                name: name,
                email: email,
                password: password
            })
            
            alert(response.data.name + " Successfully Register")
            console.log("SignUp successfully:", response);
            navigate('/login');
        }
        catch (err) {
            alert("Registeration failed")
        }
        // No real backend signup; store locally
        const user = await signup({
            name: name,
            email: email,
            password: password
        })
        console.log("Sugnu success:", response);
        navigate('/');
    }

    return (
        <div className="max-w-md mx-auto bg-white p-6 rounded shadow-sm">
            <h2 className="text-xl font-bold mb-4">Sign up</h2>
            <form onSubmit={submit} className="space-y-3">
                <input value={name} onChange={e => setName(e.target.value)} placeholder="Your name" className="w-full border p-2 rounded" />
                <input value={email} onChange={e => setEmail(e.target.value)} placeholder="Email" className="w-full border p-2 rounded" />
                <input value={password} onChange={e => setPassword(e.target.value)} type="password" placeholder="Password" className="w-full border p-2 rounded" />
                <div className="flex justify-center">
                    <button className="px-4 py-2 bg-indigo-600 text-white rounded">Sign up</button>
                </div>
            </form>
        </div>
    )
}