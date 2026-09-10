//compress: https://www.jyshare.com/front-end/51/
(() => {
    'use strict';

    const author = `[AUTHOR]`;

  function highlight() {
    const eleUsers = document.querySelectorAll('a.uname');
    for (const eleUser of eleUsers) {
      const userName = eleUser.textContent.trim();
      if (userName == author) {
        eleUser.style.color = "#00c853"
        // 找到楼主后降频兜底 (文档重建 / 加载更多时仍能高亮)
        if (!window.__purenga_slow) {
          window.__purenga_slow = 1;
          clearInterval(window.__purenga_hl);
          window.__purenga_hl = setInterval(highlight, 2000);
        }
      }
    }
  }

  highlight();

  // DOM 变化时立即高亮; NGA 渲染时可能整体重建 document, 旧目标的 observer 会失效,
  // 此时重新注册; 轮询作为兜底
  try {
    if (
      !window.__purenga_mo ||
      !window.__purenga_mo_t ||
      !window.__purenga_mo_t.isConnected
    ) {
      const target = document.documentElement || document.body;
      const mo = new MutationObserver(highlight);
      mo.observe(target, { childList: true, subtree: true });
      window.__purenga_mo = mo;
      window.__purenga_mo_t = target;
    }
  } catch (err) {
    // 忽略
  }

  if (!window.__purenga_hl) {
    window.__purenga_hl = setInterval(highlight, 100);
  }

    try{
        [CUSTOM_HS]
    }catch(err){
        alert("自定义脚本错误: " + err);
    }
})();
