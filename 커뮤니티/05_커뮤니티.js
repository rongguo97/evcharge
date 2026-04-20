/* =============================================
   EV DRIVE - 커뮤니티 페이지 JavaScript
============================================= */

// ── 전역 상태 ──────────────────────────────
let postIdCounter = 7;
let openPostId = null;

// ── 토스트 알림 ────────────────────────────
function showToast(msg) {
  let toast = document.getElementById('toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'toast';
    toast.className = 'toast';
    document.body.appendChild(toast);
  }
  toast.textContent = msg;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2500);
}

// ── 글쓰기 폼 토글 ─────────────────────────
function toggleWriteForm() {
  const form = document.getElementById('writeForm');
  if (form.style.display === 'none') {
    form.style.display = 'block';
    form.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  } else {
    form.style.display = 'none';
  }
}

// ── 게시글 등록 ────────────────────────────
function submitPost() {
  const title   = document.getElementById('postTitle').value.trim();
  const content = document.getElementById('postContent').value.trim();
  const author  = document.getElementById('postAuthor').value.trim() || '익명';
  const cat     = document.getElementById('postCategory').value;

  if (!title) { showToast('제목을 입력해주세요.'); return; }
  if (!content) { showToast('내용을 입력해주세요.'); return; }

  const id = postIdCounter++;
  const today = new Date();
  const dateStr = `${today.getFullYear()}.${String(today.getMonth()+1).padStart(2,'0')}.${String(today.getDate()).padStart(2,'0')}`;
  const catClass = { '자유': 'cat-free', '후기': 'cat-review', '팁': 'cat-tip' }[cat] || 'cat-free';
  const avatarChar = author.charAt(0);

  const card = document.createElement('div');
  card.className = 'post-card';
  card.dataset.id = id;
  card.dataset.likes = '0';
  card.dataset.comments = '0';
  card.dataset.date = today.toISOString().slice(0,10);

  card.innerHTML = `
    <div class="post-header">
      <div class="post-meta-left">
        <span class="post-category ${catClass}">${cat}</span>
        <span class="post-badge new">NEW</span>
      </div>
      <div class="post-actions-top">
        <button class="like-btn" onclick="toggleLike(this, ${id})">
          <span class="heart">🤍</span> <span class="like-count">0</span>
        </button>
      </div>
    </div>
    <h3 class="post-title" onclick="togglePost(${id})">${escapeHtml(title)}</h3>
    <p class="post-preview">${escapeHtml(content)}</p>
    <div class="post-footer">
      <div class="post-author-info">
        <div class="avatar">${avatarChar}</div>
        <span class="author-name">${escapeHtml(author)}</span>
        <span class="post-date">${dateStr}</span>
      </div>
      <div class="post-stats">
        <span>👁 1</span>
        <span>💬 <span id="cCount-${id}">0</span></span>
      </div>
    </div>
    <div class="comment-section" id="comments-${id}" style="display:none;">
      <div class="comment-list" id="commentList-${id}"></div>
      <div class="comment-write">
        <input type="text" class="comment-author-input" placeholder="닉네임" id="cAuthor-${id}" />
        <div class="comment-input-row">
          <input type="text" class="comment-input" placeholder="댓글을 입력하세요..."
                 id="cInput-${id}" onkeypress="if(event.key==='Enter') addComment(${id})" />
          <button class="comment-submit" onclick="addComment(${id})">등록</button>
        </div>
      </div>
    </div>
  `;

  const postList = document.getElementById('postList');
  postList.insertBefore(card, postList.firstChild);

  // 폼 초기화
  document.getElementById('postTitle').value = '';
  document.getElementById('postContent').value = '';
  document.getElementById('postAuthor').value = '';
  toggleWriteForm();
  updatePostCount();
  showToast('✅ 게시글이 등록되었습니다!');
}

// ── 게시글 펼치기/접기 ──────────────────────
function togglePost(id) {
  const commentSection = document.getElementById(`comments-${id}`);
  if (!commentSection) return;

  if (openPostId === id) {
    commentSection.style.display = 'none';
    openPostId = null;
  } else {
    if (openPostId !== null) {
      const prev = document.getElementById(`comments-${openPostId}`);
      if (prev) prev.style.display = 'none';
    }
    commentSection.style.display = 'block';
    commentSection.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    openPostId = id;
  }
}

// ── 좋아요 토글 ────────────────────────────
function toggleLike(btn, id) {
  const heart = btn.querySelector('.heart');
  const count = btn.querySelector('.like-count');
  const card  = btn.closest('.post-card');
  let n = parseInt(count.textContent);

  if (btn.classList.contains('liked')) {
    btn.classList.remove('liked');
    heart.textContent = '🤍';
    count.textContent = n - 1;
    card.dataset.likes = n - 1;
  } else {
    btn.classList.add('liked');
    heart.textContent = '❤️';
    count.textContent = n + 1;
    card.dataset.likes = n + 1;
    showToast('❤️ 좋아요를 눌렀습니다!');
  }
}

// ── 댓글 좋아요 토글 ───────────────────────
function toggleCommentLike(btn) {
  const text = btn.textContent;
  const match = text.match(/(\d+)/);
  if (!match) return;
  let n = parseInt(match[1]);

  if (btn.dataset.liked === '1') {
    btn.dataset.liked = '0';
    btn.textContent = `👍 ${n - 1}`;
    btn.style.color = '';
  } else {
    btn.dataset.liked = '1';
    btn.textContent = `👍 ${n + 1}`;
    btn.style.color = 'var(--primary-purple)';
  }
}

// ── 댓글 추가 ──────────────────────────────
function addComment(postId) {
  const input  = document.getElementById(`cInput-${postId}`);
  const authorInput = document.getElementById(`cAuthor-${postId}`);
  const text   = input.value.trim();
  const author = (authorInput && authorInput.value.trim()) || '익명';

  if (!text) { showToast('댓글 내용을 입력해주세요.'); return; }

  const today = new Date();
  const dateStr = `${today.getFullYear()}.${String(today.getMonth()+1).padStart(2,'0')}.${String(today.getDate()).padStart(2,'0')}`;
  const avatarChar = author.charAt(0);

  const item = document.createElement('div');
  item.className = 'comment-item';
  item.innerHTML = `
    <div class="comment-avatar">${avatarChar}</div>
    <div class="comment-body">
      <div class="comment-author-row">
        <span class="comment-author">${escapeHtml(author)}</span>
        <span class="comment-date">${dateStr}</span>
      </div>
      <p class="comment-text">${escapeHtml(text)}</p>
      <button class="comment-like-btn" onclick="toggleCommentLike(this)">👍 0</button>
    </div>
  `;

  const list = document.getElementById(`commentList-${postId}`);
  list.appendChild(item);
  input.value = '';
  if (authorInput) authorInput.value = '';

  // 댓글 수 업데이트
  const card = document.querySelector(`.post-card[data-id="${postId}"]`);
  if (card) {
    let cnt = parseInt(card.dataset.comments || 0) + 1;
    card.dataset.comments = cnt;
    const cCountEl = card.querySelector(`#cCount-${postId}`);
    if (cCountEl) cCountEl.textContent = cnt;
    const statsSpans = card.querySelectorAll('.post-stats span');
    statsSpans.forEach(s => {
      if (s.textContent.includes('💬')) s.innerHTML = `💬 ${cnt}`;
    });
  }
  showToast('💬 댓글이 등록되었습니다!');
}

// ── 검색 ───────────────────────────────────
function searchPosts() {
  const keyword = document.getElementById('searchInput').value.trim().toLowerCase();
  const cards = document.querySelectorAll('.post-card');
  let visible = 0;

  cards.forEach(card => {
    const title   = card.querySelector('.post-title')?.textContent.toLowerCase() || '';
    const preview = card.querySelector('.post-preview')?.textContent.toLowerCase() || '';
    if (!keyword || title.includes(keyword) || preview.includes(keyword)) {
      card.style.display = '';
      visible++;
    } else {
      card.style.display = 'none';
    }
  });
  updatePostCount(visible);
  if (keyword) showToast(`🔍 "${keyword}" 검색 결과: ${visible}건`);
}

// 검색창 Enter 키 지원
document.addEventListener('DOMContentLoaded', () => {
  const searchInput = document.getElementById('searchInput');
  if (searchInput) {
    searchInput.addEventListener('keypress', e => {
      if (e.key === 'Enter') searchPosts();
    });
  }
});

// ── 정렬 ───────────────────────────────────
function sortPosts(type, btn) {
  document.querySelectorAll('.sort-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');

  const list = document.getElementById('postList');
  const cards = Array.from(list.querySelectorAll('.post-card'));

  cards.sort((a, b) => {
    if (type === 'latest') {
      return new Date(b.dataset.date) - new Date(a.dataset.date);
    } else if (type === 'likes') {
      return parseInt(b.dataset.likes) - parseInt(a.dataset.likes);
    } else if (type === 'comments') {
      return parseInt(b.dataset.comments) - parseInt(a.dataset.comments);
    }
    return 0;
  });

  cards.forEach(card => list.appendChild(card));
  showToast(`정렬 방식이 변경되었습니다.`);
}

// ── 카테고리 필터 ──────────────────────────
function filterCategory(cat, el) {
  document.querySelectorAll('.cat-item').forEach(c => c.classList.remove('active-cat'));
  el.classList.add('active-cat');

  const cards = document.querySelectorAll('.post-card');
  let visible = 0;

  cards.forEach(card => {
    if (cat === 'all') {
      card.style.display = '';
      visible++;
    } else {
      const catEl = card.querySelector('.post-category');
      if (catEl && catEl.textContent === cat) {
        card.style.display = '';
        visible++;
      } else {
        card.style.display = 'none';
      }
    }
  });
  updatePostCount(visible);
}

// ── 탭 메뉴 ────────────────────────────────
document.querySelectorAll('.tab-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    showToast(`${btn.textContent} 탭으로 이동했습니다.`);
  });
});

// ── 게시글 수 업데이트 ─────────────────────
function updatePostCount(n) {
  const el = document.getElementById('postCount');
  if (!el) return;
  const count = n !== undefined ? n : document.querySelectorAll('.post-card:not([style*="display: none"])').length;
  el.innerHTML = `전체 <strong>${count}</strong>개의 게시글`;
}

// ── 페이지네이션 ───────────────────────────
document.querySelectorAll('.page-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.page-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
  });
});

// ── HTML 이스케이프 ────────────────────────
function escapeHtml(str) {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}