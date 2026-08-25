/**
 * PlantPal Authentication & Navigation Manager
 */

let currentUser = null;

async function checkAuth(requiredRole = null) {
    try {
        const user = await apiRequest('/api/auth/me');
        currentUser = user;

        const path = window.location.pathname;
        const isAuthPage = path.endsWith('/login.html') || path.endsWith('/register.html') || path === '/' || path.endsWith('/index.html');

        if (isAuthPage) {
            if (user.role === 'ADMIN') {
                window.location.href = '/pages/admin/stats.html';
            } else {
                window.location.href = '/pages/dashboard.html';
            }
            return user;
        }

        if (requiredRole && user.role !== requiredRole) {
            showToast('Access denied: Admin role required.', 'error');
            setTimeout(() => {
                window.location.href = '/pages/dashboard.html';
            }, 1000);
            return null;
        }

        renderNavbar(user);
        return user;
    } catch (error) {
        currentUser = null;
        const path = window.location.pathname;
        const isPublicPage = path.endsWith('/login.html') || path.endsWith('/register.html') || path === '/' || path.endsWith('/index.html');

        if (!isPublicPage) {
            window.location.href = '/pages/login.html';
        }
        return null;
    }
}

function renderNavbar(user) {
    const navContainer = document.getElementById('main-navbar');
    if (!navContainer) return;

    const path = window.location.pathname;
    const isDashboard = path.includes('/dashboard.html');
    const isPlants = path.includes('/plants.html') || path.includes('/plant-detail.html');
    const isProfile = path.includes('/profile.html');
    const isAdminUsers = path.includes('/admin/users.html');
    const isAdminCategories = path.includes('/admin/categories.html');
    const isAdminStats = path.includes('/admin/stats.html');

    let adminLinks = '';
    if (user && user.role === 'ADMIN') {
        adminLinks = `
            <a href="/pages/admin/stats.html" class="nav-link ${isAdminStats ? 'active' : ''}">📊 Admin Stats</a>
            <a href="/pages/admin/categories.html" class="nav-link ${isAdminCategories ? 'active' : ''}">🏷️ Categories</a>
            <a href="/pages/admin/users.html" class="nav-link ${isAdminUsers ? 'active' : ''}">👥 Users</a>
        `;
    }

    navContainer.className = 'navbar-custom';
    navContainer.innerHTML = `
        <div class="navbar-container">
            <a href="${user?.role === 'ADMIN' ? '/pages/admin/stats.html' : '/pages/dashboard.html'}" class="navbar-brand">
                🌱 PlantPal
            </a>
            <div class="navbar-nav">
                ${user?.role !== 'ADMIN' ? `
                    <a href="/pages/dashboard.html" class="nav-link ${isDashboard ? 'active' : ''}">Dashboard</a>
                    <a href="/pages/plants.html" class="nav-link ${isPlants ? 'active' : ''}">My Plants</a>
                ` : ''}
                ${adminLinks}
                <a href="/pages/profile.html" class="nav-link ${isProfile ? 'active' : ''}">Profile</a>
                <span class="user-badge">
                    👤 ${escapeHtml(user?.fullName || 'User')} (${user?.role || 'USER'})
                </span>
                <button onclick="handleLogout()" class="btn-outline-custom btn-sm">Logout</button>
            </div>
        </div>
    `;
}

async function handleLogout() {
    try {
        await apiRequest('/api/auth/logout', { method: 'POST' });
        showToast('Logged out successfully', 'info');
        window.location.href = '/pages/login.html?loggedOut=true';
    } catch (error) {
        window.location.href = '/pages/login.html';
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}