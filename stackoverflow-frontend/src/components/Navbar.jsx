import React, { useState, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { globalSearch } from '../api/api'


export default function Navbar() {
const navigate = useNavigate()
const user = JSON.parse(localStorage.getItem('user') || 'null')

const [query, setQuery] = useState('')
const [suggestions, setSuggestions] = useState([])
const [showSuggestions, setShowSuggestions] = useState(false)
const timerRef = useRef(null)

function handleLogout(){
localStorage.removeItem('user')
navigate('/')
}

function handleSelectSuggestion(item){
	// Always navigate to the dashboard (Home) with the clicked suggestion as the query
	// This ensures the Home page will fetch and display matching questions.
	const text = item?.title || item?.name || item?.query || String(item)
	navigate(`/?q=${encodeURIComponent(text)}`)
	setShowSuggestions(false)
	setQuery('')
	setSuggestions([])
}

function handleInputChange(e){
	const v = e.target.value
	setQuery(v)
	if (timerRef.current) clearTimeout(timerRef.current)

	if (!v) {
		setSuggestions([])
		setShowSuggestions(false)
		return
	}

	// debounce
	timerRef.current = setTimeout(()=>{
		globalSearch(v)
			.then(res => {
				// Expect res.data to be an array of suggestions (questions/tags/strings)
				setSuggestions(Array.isArray(res.data) ? res.data : [])
				setShowSuggestions(true)
			})
			.catch(err => {
				console.error('globalSearch error', err)
				setSuggestions([])
				setShowSuggestions(false)
			})
	}, 300)
}


return (
<nav className="bg-white border-b">
<div className="max-w-6xl mx-auto flex items-center justify-between p-3">
<div className="flex items-center gap-4">
<Link to="/" className="text-2xl font-bold text-indigo-600">StackOverFlow</Link>
				<div className="relative">
					<input
						value={query}
						onChange={handleInputChange}
						onKeyDown={(e)=>{ if(e.key==='Enter') navigate(`/?q=${encodeURIComponent(query)}`) }}
						placeholder="Search questions or tags..."
						className="px-3 py-2 border rounded-md w-80"
						onBlur={()=> setTimeout(()=> setShowSuggestions(false), 150)}
						onFocus={()=> { if (suggestions.length) setShowSuggestions(true) }}
					/>

					{showSuggestions && suggestions.length > 0 && (
						<div className="absolute left-0 mt-1 w-80 bg-white border rounded shadow z-50 max-h-64 overflow-auto">
							{suggestions.map((s, idx) => (
								<div
									key={s.id || s.questionId || s.title || `${String(s)}-${idx}`}
									onMouseDown={() => handleSelectSuggestion(s)}
									className="px-3 py-2 hover:bg-gray-100 cursor-pointer text-sm"
								>
									{s.title || s.name || s.query || String(s)}
								</div>
							))}
						</div>
					)}
				</div>
</div>


<div className="flex items-center gap-3">
<Link to="/ask" className="px-3 py-2 bg-indigo-600 text-white rounded">Ask Question</Link>
{user ? (
<>
<Link to="/profile" className="px-3 py-2 border rounded">{user.name || 'Profile'}</Link>
<button onClick={handleLogout} className="px-3 py-2 border rounded">Logout</button>
</>
) : (
<>
<Link to="/login" className="px-3 py-2 border rounded">Login</Link>
<Link to="/signup" className="px-3 py-2 border rounded">Signup</Link>
</>
)}
</div>
</div>
</nav>
)
}