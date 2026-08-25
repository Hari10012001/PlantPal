/**
 * PlantPal API Client & Utility Functions
 */

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

async function apiRequest(url, options = {}) {
    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    const method = (options.method || 'GET').toUpperCase();
    if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
        const csrfToken = getCookie('XSRF-TOKEN');
        if (csrfToken) {
            defaultHeaders['X-XSRF-TOKEN'] = csrfToken;
        }
    }

    const config = {
        ...options,
        method: method,
        headers: {
            ...defaultHeaders,
            ...options.headers
        }
    };

    try {
        const response = await fetch(url, config);

        // 204 No Content
        if (response.status === 204) {
            return null;
        }

        // Handle 401 Unauthorized
        if (response.status === 401) {
            const isAuthPage = window.location.pathname.includes('/login.html') || window.location.pathname.includes('/register.html');
            if (!isAuthPage) {
                window.location.href = '/pages/login.html?expired=true';
            }
            throw { status: 401, message: 'Session expired or unauthenticated.' };
        }

        let data = null;
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            const text = await response.text();
            data = text ? { message: text } : null;
        }

        if (!response.ok) {
            const errorObj = {
                status: response.status,
                message: data?.message || `Request failed with status ${response.status}`,
                errors: data?.errors || null
            };
            throw errorObj;
        }

        return data;
    } catch (error) {
        if (error.status) {
            throw error;
        }
        throw { status: 0, message: error.message || 'Network error occurred. Please check server.' };
    }
}

function showToast(message, type = 'info', duration = 4000) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <span>${message}</span>
        <button style="background:none;border:none;cursor:pointer;font-size:1.1rem;margin-left:0.5rem;" onclick="this.parentElement.remove()">✕</button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        if (toast.parentElement) {
            toast.remove();
        }
    }, duration);
}

function formatDate(dateStr) {
    if (!dateStr) return 'Not Set';
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return 'Not Set';
    const date = new Date(dateTimeStr);
    return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' }) + ' ' +
           date.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' });
}

function getStatusBadge(status) {
    switch (status) {
        case 'HEALTHY':
            return '<span class="badge-status badge-healthy">🌿 Healthy</span>';
        case 'NEEDS_ATTENTION':
            return '<span class="badge-status badge-needs-attention">⚠️ Needs Attention</span>';
        case 'INACTIVE':
            return '<span class="badge-status badge-inactive">💤 Inactive</span>';
        default:
            return `<span class="badge-status badge-inactive">${status || 'Unknown'}</span>`;
    }
}

function getWateringBadge(status) {
    switch (status) {
        case 'WATER_OVERDUE':
            return '<span class="badge-status badge-overdue">🚨 Overdue</span>';
        case 'WATER_TODAY':
            return '<span class="badge-status badge-today">💧 Water Today</span>';
        case 'WATER_UPCOMING':
            return '<span class="badge-status badge-upcoming">🌱 Upcoming</span>';
        case 'NOT_SET':
            return '<span class="badge-status badge-notset">⏳ Not Set</span>';
        default:
            return `<span class="badge-status badge-notset">${status || 'Not Set'}</span>`;
    }
}