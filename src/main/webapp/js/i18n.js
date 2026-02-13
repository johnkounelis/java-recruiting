// Internationalization (i18n) implementation
const i18n = {
    currentLang: localStorage.getItem('app_lang') || 'el',
    translations: {},
    _ready: false,

    init: async function () {
        await this.loadTranslations(this.currentLang);
        this._ready = true;
        this.applyTranslations();
        this.setupTogglers();
        // Dispatch event so JS components know translations are ready
        document.dispatchEvent(new CustomEvent('i18nReady', { detail: { lang: this.currentLang } }));
    },

    loadTranslations: async function (lang) {
        try {
            const basePath = window.location.pathname.includes('/admin/') ||
                window.location.pathname.includes('/recruiter/') ||
                window.location.pathname.includes('/candidate/') ? '../' : '';

            const response = await fetch(`${basePath}i18n/${lang}.json`);
            if (!response.ok) throw new Error('Translation file not found');
            this.translations = await response.json();
        } catch (error) {
            console.error('Error loading translations:', error);
        }
    },

    // Get a translated string by dot-notation key, with optional fallback
    t: function (key, fallback) {
        const keys = key.split('.');
        let value = this.translations;
        for (const k of keys) {
            if (value && value[k] !== undefined) {
                value = value[k];
            } else {
                return fallback || key;
            }
        }
        return (typeof value === 'string') ? value : (fallback || key);
    },

    applyTranslations: function () {
        document.querySelectorAll('[data-i18n]').forEach(element => {
            const key = element.getAttribute('data-i18n');
            const value = this.t(key, null);

            if (value && value !== key) {
                if (element.tagName === 'INPUT' && element.hasAttribute('placeholder')) {
                    element.placeholder = value;
                } else if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA') {
                    // For inputs with data-i18n on placeholder
                    if (element.getAttribute('data-i18n-attr') === 'placeholder') {
                        element.placeholder = value;
                    }
                } else {
                    element.textContent = value;
                }
            }
        });

        // Handle data-i18n-placeholder separately
        document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
            const key = element.getAttribute('data-i18n-placeholder');
            const value = this.t(key, null);
            if (value && value !== key) {
                element.placeholder = value;
            }
        });

        // Handle data-i18n-title
        document.querySelectorAll('[data-i18n-title]').forEach(element => {
            const key = element.getAttribute('data-i18n-title');
            const value = this.t(key, null);
            if (value && value !== key) {
                element.title = value;
            }
        });

        // Update active state of language buttons
        document.querySelectorAll('.lang-btn-el').forEach(btn => {
            btn.classList.toggle('active', this.currentLang === 'el');
        });
        document.querySelectorAll('.lang-btn-en').forEach(btn => {
            btn.classList.toggle('active', this.currentLang === 'en');
        });
    },

    setLang: function (lang) {
        if (lang === this.currentLang) return;
        localStorage.setItem('app_lang', lang);
        // Reload page to ensure all dynamic content picks up new language
        location.reload();
    },

    setupTogglers: function () {
        document.querySelectorAll('.lang-btn-el').forEach(btn => {
            btn.addEventListener('click', () => this.setLang('el'));
        });
        document.querySelectorAll('.lang-btn-en').forEach(btn => {
            btn.addEventListener('click', () => this.setLang('en'));
        });
    },

    // Check if translations are loaded
    isReady: function () {
        return this._ready;
    }
};

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    i18n.init();
});
