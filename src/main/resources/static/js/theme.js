/**
 * PlantPal Theme Manager (Light / Dark Mode with Persistence)
 * Zero external dependencies, immediate flash-free initialization
 */

(function () {
    const STORAGE_KEY = 'plantpal_theme';

    function getPreferredTheme() {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored === 'dark' || stored === 'light') {
            return stored;
        }
        return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    function applyTheme(theme, save = false) {
        const root = document.documentElement;
        root.setAttribute('data-theme', theme);
        if (theme === 'dark') {
            root.classList.add('dark-theme');
            if (document.body) document.body.classList.add('dark-theme');
        } else {
            root.classList.remove('dark-theme');
            if (document.body) document.body.classList.remove('dark-theme');
        }

        if (save) {
            localStorage.setItem(STORAGE_KEY, theme);
        }

        updateToggleButtons(theme);
    }

    function updateToggleButtons(theme) {
        const currentTheme = theme || document.documentElement.getAttribute('data-theme') || 'light';
        const buttons = document.querySelectorAll('.theme-toggle-btn');
        buttons.forEach(btn => {
            const isDark = currentTheme === 'dark';
            btn.innerHTML = isDark
                ? '<span class="theme-icon">☀️</span> <span class="theme-label">Light</span>'
                : '<span class="theme-icon">🌙</span> <span class="theme-label">Dark</span>';
            btn.setAttribute('title', isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode');
            btn.setAttribute('aria-label', isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode');
            btn.setAttribute('data-current-theme', currentTheme);
        });
    }

    // Immediate execution on load to prevent light/dark flash
    const initialTheme = getPreferredTheme();
    applyTheme(initialTheme, false);

    // Global toggle function
    window.togglePlantPalTheme = function () {
        const current = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
        const next = current === 'dark' ? 'light' : 'dark';
        applyTheme(next, true);
    };

    window.syncPlantPalTheme = function () {
        const current = document.documentElement.getAttribute('data-theme') || initialTheme;
        applyTheme(current, false);
    };

    // When DOM is ready, sync buttons and body class
    document.addEventListener('DOMContentLoaded', () => {
        window.syncPlantPalTheme();
    });

    // Also observe DOM additions so dynamic navbars get synced automatically
    if (window.MutationObserver) {
        const observer = new MutationObserver(() => {
            const uninitialized = document.querySelectorAll('.theme-toggle-btn:not([data-current-theme])');
            if (uninitialized.length > 0) {
                window.syncPlantPalTheme();
            }
        });
        observer.observe(document.documentElement, { childList: true, subtree: true });
    }
})();
