import axios from 'axios';


export const api = axios.create({
baseURL: 'http://localhost:8080/api',
timeout: 10000,
});


// Optional: add interceptors here (e.g., to attach auth token)

// Helper for the external top questions endpoint (different host/route)
export function getTopQuestions(count = 10) {
	// Use a relative path so the dev server can proxy the request and avoid CORS
	return axios.get('/question/top', { params: { count } })
}

// Search endpoint on the top-questions/search service
export function globalSearch(query) {
	// Use a relative path so the dev server can proxy the request and avoid CORS
	return axios.get('/search/query', { params: { query } })
}