import React, { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { api, getTopQuestions, globalSearch } from '../api/api'
import QuestionCard from '../components/QuestionCard'

function useQuery(){
	return new URLSearchParams(useLocation().search)
}

export default function Home(){
	const [questions, setQuestions] = useState([])
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(null)
	const q = useQuery()
	const search = q.get('q') || ''
	const tag = q.get('tag') || ''

	useEffect(()=>{
		setLoading(true)
		setError(null)

		console.log('Home effect run, search=', search, 'tag=', tag)

		// If there is a search query, call the global search endpoint (9090 service)
		if (search) {
			globalSearch(search)
				.then(res => {
					console.log('globalSearch response:', res && res.data)
					setQuestions(res.data)
				})
				.catch(err => {
					console.error(err)
					setError('Failed to load search results')
				})
				.finally(()=> setLoading(false))
			return
		}

		// If there is a tag filter (but no free-text search), use the existing /questions endpoint
		if (tag) {
			const params = { tag }
			api.get('/questions', { params })
				.then(res => setQuestions(res.data))
				.catch(err => {
					console.error(err)
					setError('Failed to load questions')
				})
				.finally(()=> setLoading(false))
			return
		}

		// Otherwise fetch top questions from the provided endpoint
		getTopQuestions(10)
			.then(res => setQuestions(res.data))
			.catch(err => {
				console.error(err)
				setError('Failed to load top questions')
			})
			.finally(()=> setLoading(false))
	}, [search, tag])

	return (
		<div className="w-full">
			<div className="flex items-center justify-between mb-8">
				<div>
					<h1 className="text-3xl font-bold text-gray-900 mb-1">Top Questions</h1>
					<p className="text-gray-600">Browse and search questions from the community</p>
				</div>
				{tag && (
					<div className="px-4 py-2 bg-blue-50 border border-blue-200 rounded-md text-sm font-medium text-blue-700">
						Filtered by: <span className="font-semibold">{tag}</span>
					</div>
				)}
			</div>

			{loading ? (
				<div className="text-center py-12">
					<div className="text-gray-600">Loading questions...</div>
				</div>
			) : error ? (
				<div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-md mb-4">
					{error}
				</div>
			) : (
				<div className="w-full">
					{questions.length === 0 ? (
						<div className="text-center py-12 text-gray-600">
							<p className="text-lg font-medium mb-2">No questions found</p>
							<p className="text-sm">Try a different search or browse all questions</p>
						</div>
					) : (
						<div className="space-y-3">
							{questions.questions.map(item => <QuestionCard key={item.id} q={item} />)}
						</div>
					)}
				</div>
			)}
		</div>
	)
}