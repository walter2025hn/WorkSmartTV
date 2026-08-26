// Comunicación con el nativo
function playStream(url) {
    document.getElementById('playerContainer').style.display = 'block';
    Android.play(url);
}

function changeScale(mode) {
    Android.setScaleMode(mode);
}

function changeResolution(level) {
    Android.setResolution(level);
}

// Carga de lista desde configuración
async function loadPlaylist() {
    const m3uUrl = document.getElementById('m3uUrl').value;
    if (m3uUrl) {
        const resp = await fetch(m3uUrl);
        const data = await resp.text();
        const channels = parseM3U(data);
        renderChannels(channels);
    }
    // Si se ingresaron credenciales Xtream
    const server = document.getElementById('xtreamServer').value;
    const user = document.getElementById('xtreamUser').value;
    const pass = document.getElementById('xtreamPass').value;
    if (server && user && pass) {
        const data = await loadXtream(server, user, pass);
        // renderizar categorías y canales
    }
    closeSettings();
}

function renderChannels(list) {
    const grid = document.getElementById('grid');
    grid.innerHTML = '';
    list.forEach(item => {
        const card = document.createElement('div');
        card.className = 'item-card';
        card.innerHTML = `
            <img src="${item.logo || 'img/default.png'}" alt="${item.name}">
            <div class="title">${item.name}</div>
        `;
        card.onclick = () => playStream(item.url);
        grid.appendChild(card);
    });
}

// Menú activo
document.querySelectorAll('.menu-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.menu-btn').forEach(b=>b.classList.remove('active'));
        this.classList.add('active');
        const cat = this.dataset.category;
        // Filtrar y mostrar según categoría
        filterCategory(cat);
    });
});

// ... más funciones (favoritos, guía, etc.)
