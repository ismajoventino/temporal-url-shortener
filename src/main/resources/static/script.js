const API = 'http://localhost:8080';

function setLoading(btn, isLoading) {
  if (isLoading) {
    btn.classList.add('loading');
    btn.disabled = true;
  } else {
    btn.classList.remove('loading');
    btn.disabled = false;
  }
}

function showError(el, message) {
  el.style.display = 'block';
  el.innerHTML = `
    <div class="error-box">
      <div class="error-label">Error</div>
      <div class="error-message">${escHtml(message)}</div>
    </div>`;
}

function clearFeedback(...els) {
  els.forEach(el => { el.style.display = 'none'; el.innerHTML = ''; });
}

function escHtml(str) {
  const d = document.createElement('div');
  d.textContent = str;
  return d.innerHTML;
}

function formatDate(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleString(undefined, {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}

async function handleShorten() {
  const btn = document.getElementById('shorten-btn');
  const errEl = document.getElementById('shorten-error');
  const resEl = document.getElementById('shorten-result');
  const url = document.getElementById('long-url').value.trim();
  const mins = parseInt(document.getElementById('expiry').value, 10);

  clearFeedback(errEl, resEl);

  if (!url) { showError(errEl, 'Please enter a URL.'); return; }

  setLoading(btn, true);

  try {
    const res = await fetch(`${API}/api/urls/shorten`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ originalUrl: url, expirationInMinutes: mins })
    });

    const data = await res.json();

    if (res.status === 201) {
      const shortUrl = data.shortUrl || '';
      const expiresAt = formatDate(data.expiresAt);

      resEl.style.display = 'block';
      resEl.innerHTML = `
        <div class="short-url-result">
          <div class="short-url-header">
            <a class="short-url-link" href="${escHtml(shortUrl)}" target="_blank" rel="noopener">${escHtml(shortUrl)}</a>
            <button class="btn btn-ghost" id="copy-btn" onclick="copyLink('${escHtml(shortUrl)}', this)">
              Copy
            </button>
          </div>
          <div class="short-url-meta">
            <span>Expires</span>
            <span class="meta-dot"></span>
            <span>${expiresAt}</span>
          </div>
        </div>`;
    } else {
      showError(errEl, data.message || 'Something went wrong.');
    }
  } catch (e) {
    showError(errEl, 'Could not reach the server. Is the API running?');
  }

  setLoading(btn, false);
}

async function handleLookup() {
  const btn = document.getElementById('lookup-btn');
  const errEl = document.getElementById('lookup-error');
  const resEl = document.getElementById('lookup-result');
  let code = document.getElementById('short-code').value.trim();
  code = code.split('/').pop();

  clearFeedback(errEl, resEl);

  if (!code) { showError(errEl, 'Please enter a short code.'); return; }

  setLoading(btn, true);

  try {
    const res = await fetch(`${API}/api/urls/${encodeURIComponent(code)}`);
    const data = await res.json();

    if (res.ok) {
      resEl.style.display = 'block';
      const shortUrl = `${API}/${code}`;
      resEl.innerHTML = `
        <div class="lookup-result">
          <div class="lookup-row">
            <span class="lookup-key">Original</span>
            <span class="lookup-value">
              <a href="${escHtml(data.originalUrl)}" target="_blank" rel="noopener">${escHtml(data.originalUrl)}</a>
            </span>
          </div>
          <div class="lookup-row">
            <span class="lookup-key">Short URL</span>
            <span class="lookup-value mono">
              <a href="${escHtml(shortUrl)}" target="_blank" rel="noopener">${escHtml(shortUrl)}</a>
            </span>
          </div>
          <div class="lookup-row">
            <span class="lookup-key">Expires</span>
            <span class="lookup-value">${formatDate(data.expiresAt)}</span>
          </div>
          <div class="lookup-row">
            <span class="lookup-key">Clicks</span>
            <span class="lookup-value">
              <span class="click-count">
                <span class="click-badge">${data.clickCount ?? 0}</span>
              </span>
            </span>
          </div>
        </div>`;
    } else {
      showError(errEl, data.message || 'Not found.');
    }
  } catch (e) {
    showError(errEl, 'Could not reach the server. Is the API running?');
  }

  setLoading(btn, false);
}

function copyLink(url, btn) {
  navigator.clipboard.writeText(url).then(() => {
    btn.textContent = 'Copied!';
    btn.classList.add('copied');
    setTimeout(() => {
      btn.textContent = 'Copy';
      btn.classList.remove('copied');
    }, 2000);
  }).catch(() => {
    btn.textContent = 'Failed';
    setTimeout(() => { btn.textContent = 'Copy'; }, 2000);
  });
}

// Enter key support
document.getElementById('long-url').addEventListener('keydown', e => { if (e.key === 'Enter') handleShorten(); });
document.getElementById('short-code').addEventListener('keydown', e => { if (e.key === 'Enter') handleLookup(); });
