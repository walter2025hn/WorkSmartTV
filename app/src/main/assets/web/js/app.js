// Estado de la aplicación
let playlists = [];
let channels = [];
let favorites = [];

// Inicialización
document.addEventListener('DOMContentLoaded', () => {
    loadPlaylists();
    setupNavigation();
    setupCategories();
});

// Navegación
function setupNavigation() {
    const menuItems = document.querySelectorAll('.menu li');
    const sections = document.querySelectorAll('.section');
    
    menuItems.forEach(item => {
        item.addEventListener('click', () => {
            const section = item.dataset.section;
            
            // Actualizar menú
            menuItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            
            // Mostrar sección
            sections.forEach(s => s.classList.remove('active'));
            document.getElementById(section).classList.add('active');
        });
    });
}

// Cargar playlists desde Android
function loadPlaylists() {
    if (window.Android) {
        const data = Android.getPlaylists();
        const parsed = JSON.parse(data);
        
        // Cargar M3U
        parsed.m3u.forEach(item => {
            const [name, url] = item.split('|');
            playlists.push({ name, url, type: 'm3u' });
        });
        
        // Cargar Xtream
        parsed.xtream.forEach(item => {
            const [name, server, username, password] = item.split('|');
            playlists.push({ name, server, username, password, type: 'xtream' });
        });
        
        renderChannels();
    }
}

// Mostrar modal
function showAddPlaylist() {
    document.getElementById('playlistModal').classList.add('active');
}

function closeModal() {
    document.getElementById('playlistModal').classList.remove('active');
}

// Cambiar tabs
function switchTab(tab) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    event.target.classList.add('active');
    document.getElementById(tab + 'Tab').classList.add('active');
}

// Añadir M3U
function addM3U() {
    const name = document.getElementById('m3uName').value;
    const url = document.getElementById('m3uUrl').value;
    
    if (name && url) {
        if (window.Android) {
            Android.addM3UPlaylist(url, name);
        }
        
        playlists.push({ name, url, type: 'm3u' });
        closeModal();
        renderChannels();
        
        // Limpiar
        document.getElementById('m3uName').value = '';
        document.getElementById('m3uUrl').value = '';
    }
}

// Añadir Xtream Codes
function addXtream() {
    const name = document.getElementById('xtreamName').value;
    const server = document.getElementById('xtreamServer').value;
    const username = document.getElementById('xtreamUsername').value;
    const password = document.getElementById('xtreamPassword').value;
    
    if (name && server && username && password) {
        if (window.Android) {
            Android.addXtreamCodes(server, username, password, name);
        }
        
        playlists.push({ name, server, username, password, type: 'xtream' });
        closeModal();
        renderChannels();
        
        // Limpiar
        document.getElementById('xtreamName').value = '';
        document.getElementById('xtreamServer').value = '';
        document.getElementById('xtreamUsername').value = '';
        document.getElementById('xtreamPassword').value = '';
    }
}

// Renderizar canales
function renderChannels() {
    const grid = document.getElementById('liveChannels');
    grid.innerHTML = '';
    
    channels.forEach(channel => {
        const item = document.createElement('div');
        item.className = 'channel-item';
        item.innerHTML = `
            <img src="${channel.logo || 'placeholder.png'}" alt="${channel.name}">
            <h4>${channel.name}</h4>
        `;
        item.onclick = () => playChannel(channel);
        grid.appendChild(item);
    });
}

// Reproducir canal
function playChannel(channel) {
    if (window.Android) {
        Android.playStream(channel.url, channel.name);
    }
}

// Setup categorías
function setupCategories() {
    document.querySelectorAll('.category-card').forEach(card => {
        card.addEventListener('click', () => {
            const category = card.dataset.category;
            filterByCategory(category);
        });
    });
}

function filterByCategory(category) {
    // Filtrar canales por categoría
    console.log('Filtrar por:', category);
}

// Búsqueda
document.getElementById('searchInput')?.addEventListener('input', (e) => {
    const query = e.target.value.toLowerCase();
    const filtered = channels.filter(c => 
        c.name.toLowerCase().includes(query)
    );
    renderFilteredChannels(filtered);
});

function renderFilteredChannels(filtered) {
    const grid = document.getElementById('liveChannels');
    grid.innerHTML = '';
    
    filtered.forEach(channel => {
        const item = document.createElement('div');
        item.className = 'channel-item';
        item.innerHTML = `
            <img src="${channel.logo || 'placeholder.png'}" alt="${channel.name}">
            <h4>${channel.name}</h4>
        `;
        item.onclick = () => playChannel(channel);
        grid.appendChild(item);
    });
}
