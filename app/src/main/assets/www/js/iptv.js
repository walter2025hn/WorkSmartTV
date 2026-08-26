// M3U Parser
function parseM3U(data) {
    const lines = data.split('\n');
    const channels = [];
    let current = null;
    for (let line of lines) {
        line = line.trim();
        if (line.startsWith('#EXTINF:')) {
            const match = line.match(/tvg-logo="([^"]*)"/);
            const logo = match ? match[1] : '';
            const name = line.split(',')[1] || 'Canal';
            current = { name, logo, group: 'Sin grupo' };
        } else if (line && !line.startsWith('#')) {
            if (current) {
                current.url = line;
                channels.push(current);
                current = null;
            }
        }
    }
    return channels;
}

// Xtreme Codes API
async function loadXtream(server, user, pass) {
    const base = `${server}/player_api.php?username=${user}&password=${pass}`;
    const categories = await fetch(`${base}&action=get_live_categories`).then(r=>r.json());
    const streams = await fetch(`${base}&action=get_live_streams`).then(r=>r.json());
    // Similar para películas y series
    return { categories, streams };
}
