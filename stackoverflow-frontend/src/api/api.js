// api/api.js
import axios from 'axios';

// Create a reusable Axios instance for your backend API
export const api = axios.create({
  baseURL: 'http://localhost:9090', // 👈 your backend base URL
  timeout: 10000,
});

// Attach JWT to every request if present
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token'); // or sessionStorage, etc.
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// (Optional) response interceptor to handle 401 globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token invalid/expired → log out user
      localStorage.removeItem('token');
      // You can also trigger a redirect to login here,
      // but it's usually better to handle that in your routing logic.
    }
    return Promise.reject(error);
  }
);

/**
 * Get top questions
 * Calls: GET http://localhost:9090/api/question/top?count=10
 */
export function getTopQuestions(count = 10) {
  return api.get('/question/top', { params: { count } });
}

/**
 * Global search
 * Calls: GET http://localhost:9090/api/search/query?query=...
 */
export function globalSearch(query) {
  return api.get('/search/query', { params: { query } });
}

/**
 * Login
 * Calls: POST http://localhost:9090/api/login
 * Body: { username, password }
 * Returns: response.data (and stores token if present)
 */
export async function login(credentials) {
  try {
    console.log('Calling:', api.defaults.baseURL + '/auth/login');
    const response = await api.post('/auth/login', credentials);

    if (response.data && response.data.token) {
      localStorage.setItem('token', response.data.token);
    }

    return response.data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}

// (Optional) helper to log out
export function logout() {
  localStorage.removeItem('token');
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

export async function postQuestion(question) {
  try {
    console.log('Calling:', api.defaults.baseURL + '/question/post');
    const response = await api.post('/question/post', question,);

    return response;
  } catch (error) {
    console.error('post question error:', error);
    return error
  }
}

// Default export in case you want direct access to the Axios instance
export default api;
