const API_URL = '/api/messages';

document.addEventListener('DOMContentLoaded', () => {
    loadMessages();

    document.getElementById('messageForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await submitMessage();
    });
});

async function submitMessage() {
    const name = document.getElementById('name').value.trim();
    const message = document.getElementById('message').value.trim();
    const statusEl = document.getElementById('formStatus');

    if (!name || !message) return;

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, message })
        });

        if (response.ok) {
            showStatus(statusEl, 'Message saved successfully!', 'success');
            document.getElementById('name').value = '';
            document.getElementById('message').value = '';
            await loadMessages();
        } else {
            showStatus(statusEl, 'Failed to save message. Please try again.', 'error');
        }
    } catch (err) {
        showStatus(statusEl, 'Unable to connect to the server.', 'error');
    }
}

async function loadMessages() {
    const listEl = document.getElementById('messagesList');
    listEl.innerHTML = '<p class="loading">Loading messages...</p>';

    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Failed to fetch');

        const messages = await response.json();

        if (messages.length === 0) {
            listEl.innerHTML = '<p class="empty">No messages yet. Be the first to send one!</p>';
            return;
        }

        listEl.innerHTML = messages
            .slice()
            .reverse()
            .map(msg => `
                <div class="message-item">
                    <div class="msg-name">${escapeHtml(msg.name)}</div>
                    <div class="msg-text">${escapeHtml(msg.message)}</div>
                    <div class="msg-time">${formatDate(msg.createdAt)}</div>
                </div>
            `).join('');
    } catch (err) {
        listEl.innerHTML = '<p class="empty">Unable to load messages. Check your connection.</p>';
    }
}

function showStatus(el, message, type) {
    el.textContent = message;
    el.className = `status-message ${type}`;
    setTimeout(() => { el.className = 'status-message hidden'; }, 4000);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleString();
}
