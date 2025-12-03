// api/api.js
import axios from 'axios';
import { getToken, setToken, clearToken, handleUnauthorized } from '../auth';

// Create a reusable Axios instance for your backend API
export const api = axios.create({
  baseURL: 'http://localhost:9090',
  timeout: 10000,
});

// Attach JWT to every request if present
api.interceptors.request.use(
  (config) => {
    const token = getToken();                    // ✅ use helper instead of localStorage
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle 401 globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      const url = error.config?.url || '';

      // Avoid redirect loop when login/signup itself fails
      const isAuthEndpoint =
        url.includes('/auth/login') || url.includes('/user/register');

      if (!isAuthEndpoint) {
        handleUnauthorized();                   // ✅ global redirect + token clear
      } else {
        clearToken();
      }
    }
    return Promise.reject(error);
  }
);

/**
 * Get top questions
 * Calls: GET http://localhost:9090/question/top?count=10
 * Public endpoint – no login required.
 */
export function getTopQuestions(count = 10) {
  return api.get('/question/top', { params: { count } });
}

/**
 * Global search
 * Calls: GET http://localhost:9090/search/query?query=...
 * Public endpoint – no login required (assuming backend allows it).
 */
export function globalSearch(query) {
  return api.get('/search/query', { params: { query } });
}

/**
 * Login
 * Calls: POST http://localhost:9090/auth/login
 * Body: { username, password }
 * Returns: response.data (and stores token if present)
 */
export async function login(credentials) {
  try {
    console.log('Calling:', api.defaults.baseURL + '/auth/login');
    const response = await api.post('/auth/login', credentials);

    if (response.data && response.data.token) {
      setToken(response.data.token);            // ✅ use helper set token

      // if (response.data.user) {
      //   localStorage.setItem('user', JSON.stringify(response.data.user)); //set user name

      // }
    }

    return response.data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}

// Helper to log out
export function logout() {
  clearToken();                                 // ✅ use helper
}

export async function signup(credentials) {
  try {
    console.log('Calling:', api.defaults.baseURL + '/user/register');
    const response = await api.post('/user/register', credentials);
    return response;
  } catch (error) {
    console.error('Signup error:', error);
    return error;
  }
}

export async function postQuestion({ title, content, tags, file }) {
  try {
    console.log('Calling:', api.defaults.baseURL + '/question/post');

    const formData = new FormData();
    formData.append('title', title);
    formData.append('content', content);

    // tags can be an array of strings
    if (Array.isArray(tags)) {
      tags.forEach((t) => formData.append('tags', t));
    }

    if (file) {
      formData.append('file', file);
    }

    const response = await api.post('/question/post', formData);
    return response;
  } catch (error) {
    console.error('post question error:', error);
    return error;
  }
}


export async function getQuestionById(id) {
  try {
    console.log('Calling:', `${api.defaults.baseURL}/question/findById/${id}`);
    const response = await api.get(`/question/findById/${id}`);
    return response.data;                      // your JSON object
  } catch (error) {
    console.error('getQuestionById error:', error);
    throw error;
  }
}

export async function postAnswer({ questionId, answer, file }) {
  const formData = new FormData();
  formData.append('questionId', questionId);
  formData.append('answer', answer);
  if (file) {
    formData.append('file', file);
  }

  const res = await api.post('/answer/post', formData);
  return res.data;                             // this is your Answer object
}

export async function postComment({ answerId, comment }) {
  // Backend expects JSON:
  // { "answerId": 14, "comment": "python ishe acha hai" }
  const res = await api.post('/comment/post', {
    answerId,
    comment,
  });

  return res.data; // created Comment object
}

export async function upVote(postType, postId, userId) {
  const res = await api.post('/vote/upVote', null, {
    params: { postType, postId, userId }
  });
  return res.data;     // { votes, myVote }
}

export async function downVote(postType, postId, userId) {
  const res = await api.post('/vote/downVote', null, {
    params: { postType, postId, userId }
  });
  return res.data;     // { votes, myVote }
}



// Default export in case you want direct access to the Axios instance
export default api;
