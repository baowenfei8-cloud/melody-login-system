/* ==========================================
   ♪ MelodyLogin - 交互脚本
   ========================================== */
(function() {
    'use strict';

    // === 1. 音乐粒子 ===
    function initParticles() {
        var c = document.querySelector('.music-particles');
        if (!c) return;
        var notes = ['♪','♫','♬','♩','🎵','🎶'];
        for (var i = 0; i < 18; i++) {
            var n = document.createElement('span');
            n.className = 'music-note';
            n.textContent = notes[i % notes.length];
            n.style.left = Math.random() * 100 + '%';
            n.style.fontSize = (12 + Math.random() * 16) + 'px';
            n.style.animationDuration = (14 + Math.random() * 16) + 's';
            n.style.animationDelay = (Math.random() * 18) + 's';
            c.appendChild(n);
        }
    }

    // === 2. 声波动画 ===
    function initWaves() {
        var c = document.querySelector('.sound-waves');
        if (!c) return;
        for (var i = 0; i < 40; i++) {
            var b = document.createElement('div');
            b.className = 'sound-bar';
            b.style.animationDuration = (0.6 + Math.random() * 1.2) + 's';
            b.style.animationDelay = (Math.random() * 2) + 's';
            b.style.height = (15 + Math.random() * 70) + 'px';
            c.appendChild(b);
        }
    }

    // === 3. 密码可见切换 ===
    window.togglePwd = function(btn) {
        var input = btn.parentElement.querySelector('input');
        if (!input) return;
        var t = input.type === 'password' ? 'text' : 'password';
        input.type = t;
        btn.textContent = t === 'password' ? '👁️' : '👁️‍🗨️';
    };

    // === 4. 黑胶点击加速 ===
    function initVinyl() {
        var v = document.querySelector('.vinyl-logo');
        if (!v) return;
        v.addEventListener('click', function() {
            var d = this.querySelector('.vinyl-disc');
            if (!d) return;
            d.style.animation = 'none';
            d.offsetHeight;
            d.style.animation = 'spin 0.5s cubic-bezier(0.25,0.46,0.45,0.94)';
            setTimeout(function() { d.style.animation = 'spin 8s linear infinite'; }, 600);
        });
    }

    // === 5. 数字滚动 ===
    function initCountUp() {
        var els = document.querySelectorAll('.cv[data-num]');
        if (!els.length) return;
        els.forEach(function(el) {
            var target = parseInt(el.getAttribute('data-num')) || 0;
            var cur = 0;
            var step = Math.ceil(target / 35);
            var t = setInterval(function() {
                cur += step;
                if (cur >= target) { clearInterval(t); el.textContent = target.toLocaleString(); }
                else { el.textContent = cur.toLocaleString(); }
            }, 30);
        });
    }

    // === 6. 密码强度检测（注册页） ===
    function initPwdStrength() {
        var pwd = document.getElementById('regPwd');
        if (!pwd) return;
        pwd.addEventListener('input', function() {
            var v = this.value;
            var s = 0;
            if (v.length >= 6) s++;
            if (v.length >= 10) s++;
            if (/[a-z]/.test(v) && /[A-Z]/.test(v)) s++;
            if (/\d/.test(v)) s++;
            if (/[^a-zA-Z0-9]/.test(v)) s++;
            var fill = document.getElementById('pwdFill');
            var labels = ['','#ff6b6b','#ffa94d','#ffd43b','#69db7c','#51cf66'];
            fill.style.width = (s * 20) + '%';
            fill.style.background = labels[s] || '';
        });
    }

    // === 7. Profile 页交互（AJAX） ===
    function initProfile() {
        var toggleBtn = document.getElementById('togglePwdBtn');
        var pwdForm = document.getElementById('pwdForm');
        if (toggleBtn && pwdForm) {
            toggleBtn.addEventListener('click', function() {
                var vis = pwdForm.style.display !== 'none';
                pwdForm.style.display = vis ? 'none' : 'block';
                toggleBtn.textContent = vis ? '修改密码' : '取消修改密码';
            });
        }

        // 保存资料
        var pf = document.getElementById('profileForm');
        if (pf) {
            pf.addEventListener('submit', function(e) {
                e.preventDefault();
                var btn = document.getElementById('saveBtn');
                btn.disabled = true; btn.textContent = '保存中...';
                var fd = new FormData();
                fd.append('nickname', document.getElementById('nickname').value);
                fd.append('email', document.getElementById('email').value);
                fd.append('phone', document.getElementById('phone').value);
                fetch('/api/profile/update', { method:'POST', body: new URLSearchParams(fd) })
                .then(function(r) { return r.json(); })
                .then(function(d) {
                    btn.disabled = false; btn.textContent = '✎ 保存修改';
                    showToast(d.msg, d.success);
                })
                .catch(function() { btn.disabled = false; btn.textContent = '✎ 保存修改'; showToast('网络错误', false); });
            });
        }

        // 修改密码
        var cpf = document.getElementById('changePwdForm');
        if (cpf) {
            cpf.addEventListener('submit', function(e) {
                e.preventDefault();
                var oldPwd = document.getElementById('oldPwd').value;
                var newPwd = document.getElementById('newPwd').value;
                var confirmPwd = document.getElementById('confirmPwd').value;
                if (newPwd !== confirmPwd) { showToast('两次新密码不一致', false); return; }
                var btn = cpf.querySelector('.btn-primary');
                btn.disabled = true; btn.textContent = '更新中...';
                fetch('/api/password/change', {
                    method:'POST',
                    headers:{'Content-Type':'application/x-www-form-urlencoded'},
                    body:'oldPassword='+encodeURIComponent(oldPwd)+'&newPassword='+encodeURIComponent(newPwd)
                })
                .then(function(r) { return r.json(); })
                .then(function(d) {
                    btn.disabled = false; btn.textContent = '✓ 更新密码';
                    showToast(d.msg, d.success);
                    if (d.success) { document.getElementById('oldPwd').value=''; document.getElementById('newPwd').value=''; document.getElementById('confirmPwd').value=''; }
                })
                .catch(function() { btn.disabled = false; btn.textContent = '✓ 更新密码'; showToast('网络错误', false); });
            });
        }
    }

    // === 8. Toast ===
    function showToast(msg, success) {
        var c = document.getElementById('toastContainer');
        if (!c) { c = document.createElement('div'); c.id='toastContainer'; c.style.cssText='position:fixed;top:20px;right:20px;z-index:10000;display:flex;flex-direction:column;gap:8px;'; document.body.appendChild(c); }
        var t = document.createElement('div');
        t.style.cssText='padding:12px 18px;border-radius:10px;font-size:13px;font-family:var(--font-body);animation:slideDown 0.3s ease;display:flex;align-items:center;gap:8px;max-width:340px;';
        t.style.background = success ? 'rgba(81,207,102,0.1)' : 'rgba(255,107,107,0.1)';
        t.style.border = '1px solid ' + (success ? 'rgba(81,207,102,0.2)' : 'rgba(255,107,107,0.2)');
        t.style.color = success ? '#51cf66' : '#ff6b6b';
        t.innerHTML = '<span style="font-weight:700">' + (success ? '✓' : '✕') + '</span><span>' + msg + '</span>';
        c.appendChild(t);
        setTimeout(function() { t.style.opacity='0'; t.style.transform='translateX(20px)'; t.style.transition='all 0.3s'; setTimeout(function(){t.remove()},300); }, 3000);
    }

    // 初始化
    initParticles();
    initWaves();
    initVinyl();
    initCountUp();
    initPwdStrength();
    initProfile();
})();
