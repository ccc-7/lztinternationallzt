document.addEventListener("DOMContentLoaded", function () {
  const flash = document.querySelector(".flash");
  if (flash) {
    setTimeout(() => {
      flash.classList.add("fade-out");
      setTimeout(() => flash.remove(), 500);
    }, 1400);
  }

  wireSidebarToggles();

  trackRecentNavigation();
  renderRecentPages();

  const tabButtons = document.querySelectorAll("[data-tab]");
  const hiddenRole = document.querySelector("#roleInput");
  if (tabButtons.length > 0 && hiddenRole) {
    tabButtons.forEach(btn => {
      btn.addEventListener("click", () => {
        tabButtons.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");
        hiddenRole.value = btn.dataset.tab;
      });
    });
  }
});

function wireSidebarToggles() {
  const toggles = document.querySelectorAll(".sidebar-toggle");
  if (!toggles || toggles.length === 0) return;

  toggles.forEach(toggle => {
    toggle.addEventListener("click", e => {
      e.preventDefault();
      const layout = toggle.closest(".layout");
      const sidebar = layout ? layout.querySelector(".sidebar") : document.querySelector(".sidebar");
      if (sidebar) sidebar.classList.toggle("collapsed");
    });
  });
}

function trackRecentNavigation() {
  const navLinks = document.querySelectorAll(".sidebar-nav a[href]");
  if (!navLinks || navLinks.length === 0) return;

  navLinks.forEach(link => {
    link.addEventListener("click", () => {
      const href = link.getAttribute("href");
      if (!href) return;
      const label = (link.textContent || "").replace(/\s+/g, " ").trim();
      if (!label) return;

      try {
        const key = "recentPages";
        const existing = JSON.parse(localStorage.getItem(key) || "[]");
        const next = [
          { href, label, ts: Date.now() },
          ...existing.filter(item => item && item.href !== href).slice(0, 4),
        ];
        localStorage.setItem(key, JSON.stringify(next));
      } catch (e) {
        // ignore localStorage issues
      }
    });
  });
}

function renderRecentPages() {
  const container = document.querySelector("[data-recent-container]");
  if (!container) return;

  try {
    const items = JSON.parse(localStorage.getItem("recentPages") || "[]");
    if (!items || items.length === 0) {
      container.innerHTML = '<div class="empty-state-small">No recent activity</div>';
      return;
    }

    const html = items
      .filter(i => i && i.href && i.label)
      .slice(0, 5)
      .map(i => `<a href="${escapeHtml(i.href)}">${escapeHtml(i.label)}</a>`)
      .join("");
    container.innerHTML = html || '<div class="empty-state-small">No recent activity</div>';
  } catch (e) {
    container.innerHTML = '<div class="empty-state-small">No recent activity</div>';
  }
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}