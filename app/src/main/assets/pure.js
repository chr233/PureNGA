//compress: https://www.jyshare.com/front-end/51/
(() => {
    'use strict';

    const author = `[AUTHOR]`;
    const eleUsers = document.querySelectorAll('a.uname');

    for (const eleUser of eleUsers) {
        const userName = eleUser.textContent.trim();
        if (userName == author) {
            eleUser.style.color = "#00c853"
        }
    }

    try{
        [CUSTOM_HS]
    }catch(err){
        alert("自定义脚本错误: " + err);
    }
})();