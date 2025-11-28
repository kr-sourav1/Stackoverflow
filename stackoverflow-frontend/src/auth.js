// src/auth.js

// key used in localStorage
const TOKEN_KEY = 'token';

/** Save JWT token */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

/** Get JWT token */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

/** Remove JWT token */
export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

/** Is the user logged in? */
export function isLoggedIn() {
  return !!getToken();
}

/**
 * Handle 401 errors globally:
 * - clear token
 * - redirect to /login with ?redirect=<current-path>
 */
export function handleUnauthorized() {
  clearToken();

  // Get current URL so we can send user back after login
  const currentUrl = window.location.pathname + window.location.search;

  const redirectParam = encodeURIComponent(currentUrl);

  // Hard redirect so it works from anywhere (no need for useNavigate)
  window.location.href = `/login?redirect=${redirectParam}`;
}

/** Extract userId from JWT payload */
export function getCurrentUserId() {
  const token = getToken();
  if (!token) return null;

  try {
    const [, payload] = token.split('.');
    const decoded = JSON.parse(
      atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return decoded.userId ?? null;
  } catch (e) {
    console.error("Failed to decode JWT", e);
    return null;
  }
}

export function getCurrentUser() {
  const token = getToken();
  if (!token) return null;

  try {
    const [, payload] = token.split(".");
    const decoded = JSON.parse(
      atob(payload.replace(/-/g, "+").replace(/_/g, "/"))
    );

    return {
      id: decoded.userId,
      // email: decoded.sub,
      name: decoded.name
    };
  } catch (e) {
    console.error("Failed to decode JWT", e);
    return null;
  }
}


