const { defineConfig } = require('vite');

const arenaPreview = process.env.ARENA_PREVIEW === '1';

module.exports = defineConfig({
  root: 'www',
  plugins: [
    {
      name: 'tracker-preview-live-reload',
      transformIndexHtml(html) {
        return html
          .replace('<script src="app.js"></script>', '<script type="module" src="/app.js"></script>')
          .replace('</head>', '<script>window.__TRACKER_DEV__ = true;</script></head>');
      },
    },
  ],
  server: {
    host: '0.0.0.0',
    port: 5173,
    strictPort: true,
    allowedHosts: true,
    hmr: arenaPreview ? { protocol: 'wss', clientPort: 443 } : undefined,
  },
});
