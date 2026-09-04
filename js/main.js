// Main JavaScript for Interactive & Aesthetic Effects
document.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize Icons
    if (window.lucide) {
        window.lucide.createIcons();
    }

    // 2. Set Current Year in Footer
    const yearElem = document.getElementById('year');
    if (yearElem) {
        yearElem.textContent = new Date().getFullYear();
    }

    // 3. Initialize Theme Management
    initTheme();

    // 4. Initialize Mobile Menu Drawer
    initMobileMenu();

    // 5. Initialize Navigation Scrollspy
    initScrollSpy();

    // 6. Typewriter Effect
    initTypewriter();

    // 7. Scroll Reveal Animation
    initScrollReveal();

    // 8. Animated Counters
    initCounters();
});

// Dark / Light Theme Logic
function initTheme() {
    const themeToggleBtn = document.getElementById('theme-toggle');
    const htmlElem = document.documentElement;

    const savedTheme = localStorage.getItem('theme') || 'dark';
    if (savedTheme === 'dark') {
        htmlElem.classList.add('dark');
    } else {
        htmlElem.classList.remove('dark');
    }

    if (themeToggleBtn) {
        themeToggleBtn.addEventListener('click', () => {
            const isDark = htmlElem.classList.toggle('dark');
            localStorage.setItem('theme', isDark ? 'dark' : 'light');
            if (window.lucide) {
                window.lucide.createIcons();
            }
        });
    }
}

// Typewriter Effect for Hero Subtitle
function initTypewriter() {
    const textElem = document.getElementById('typewriter-text');
    if (!textElem) return;

    const phrases = [
        'Full Stack Developer',
        'AI & RAG Applications Engineer',
        'Java 21 & Spring Boot Specialist',
        'FastAPI & Microservices Architect',
        'Distributed Systems Builder'
    ];

    let phraseIndex = 0;
    let charIndex = 0;
    let isDeleting = false;
    let typingSpeed = 90;

    function type() {
        const currentPhrase = phrases[phraseIndex];

        if (isDeleting) {
            textElem.textContent = currentPhrase.substring(0, charIndex - 1);
            charIndex--;
            typingSpeed = 45;
        } else {
            textElem.textContent = currentPhrase.substring(0, charIndex + 1);
            charIndex++;
            typingSpeed = 90;
        }

        if (!isDeleting && charIndex === currentPhrase.length) {
            isDeleting = true;
            typingSpeed = 1800; // Pause at end of phrase
        } else if (isDeleting && charIndex === 0) {
            isDeleting = false;
            phraseIndex = (phraseIndex + 1) % phrases.length;
            typingSpeed = 400;
        }

        setTimeout(type, typingSpeed);
    }

    type();
}

// Scroll Reveal with Intersection Observer
function initScrollReveal() {
    const revealElements = document.querySelectorAll('.reveal-on-scroll');
    if (!revealElements.length) return;

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('revealed');
            }
        });
    }, {
        threshold: 0.12,
        rootMargin: '0px 0px -40px 0px'
    });

    revealElements.forEach(el => observer.observe(el));
}

// Animated Numerical Counter for Stats
function initCounters() {
    const counters = document.querySelectorAll('.counter-val');
    if (!counters.length) return;

    let hasRun = false;
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && !hasRun) {
                hasRun = true;
                counters.forEach(counter => {
                    const target = parseFloat(counter.getAttribute('data-target'));
                    const suffix = counter.getAttribute('data-suffix') || '';
                    let count = 0;
                    const duration = 1200; // 1.2 seconds
                    const stepTime = 25;
                    const steps = duration / stepTime;
                    const increment = target / steps;

                    const timer = setInterval(() => {
                        count += increment;
                        if (count >= target) {
                            counter.textContent = target + suffix;
                            clearInterval(timer);
                        } else {
                            counter.textContent = (Number.isInteger(target) ? Math.floor(count) : count.toFixed(1)) + suffix;
                        }
                    }, stepTime);
                });
            }
        });
    }, { threshold: 0.3 });

    const statsSection = document.getElementById('stats-section');
    if (statsSection) {
        observer.observe(statsSection);
    }
}

// Mobile Menu Drawer Handler
function initMobileMenu() {
    const mobileMenuBtn = document.getElementById('mobile-menu-btn');
    const mobileMenu = document.getElementById('mobile-menu');

    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });

        const mobileLinks = mobileMenu.querySelectorAll('a');
        mobileLinks.forEach(link => {
            link.addEventListener('click', () => {
                mobileMenu.classList.add('hidden');
            });
        });
    }
}

// Scrollspy to highlight active nav link on scrolling
function initScrollSpy() {
    const sections = document.querySelectorAll('section[id]');
    const navLinks = document.querySelectorAll('header nav a[href^="#"]');

    window.addEventListener('scroll', () => {
        let currentSectionId = '';
        const scrollPosition = window.scrollY + 140;

        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.offsetHeight;
            if (scrollPosition >= sectionTop && scrollPosition < sectionTop + sectionHeight) {
                currentSectionId = section.getAttribute('id');
            }
        });

        navLinks.forEach(link => {
            link.classList.remove('nav-link-active');
            if (link.getAttribute('href') === `#${currentSectionId}`) {
                link.classList.add('nav-link-active');
            }
        });
    });
}

// Clipboard Copy Helper with Toast Notification
function copyToClipboard(text, customMessage = 'Copied to clipboard!') {
    navigator.clipboard.writeText(text).then(() => {
        showToast(customMessage);
    }).catch(err => {
        console.error('Failed to copy: ', err);
    });
}

// Toast display helper
function showToast(message) {
    const toast = document.getElementById('toast');
    const toastMsg = document.getElementById('toast-msg');

    if (toast && toastMsg) {
        toastMsg.textContent = message;
        toast.classList.remove('translate-y-[-100px]', 'opacity-0');
        toast.classList.add('translate-y-0', 'opacity-100');

        setTimeout(() => {
            toast.classList.add('translate-y-[-100px]', 'opacity-0');
            toast.classList.remove('translate-y-0', 'opacity-100');
        }, 2500);
    }
}
