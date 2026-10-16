const { createServer } = require('node:http');
const { readFile } = require('node:fs');
const { join } = require('node:path');

const port = Number(process.env.PORT || 3000);
const files = {
  '/index.html': 'text/html; charset=utf-8',
  '/styles.css': 'text/css; charset=utf-8',
  '/app.js': 'text/javascript; charset=utf-8',
  '/assets/logo.svg': 'image/svg+xml',
};

createServer((request, response) => {
  const pathname = new URL(request.url, 'http://localhost').pathname;
  const file = pathname === '/' ? '/index.html' : pathname;

  if (!Object.hasOwn(files, file)) {
    response.writeHead(404).end('Not found');
    return;
  }

  readFile(join(__dirname, file), (error, content) => {
    if (error) {
      response.writeHead(500).end('Could not read file');
      return;
    }
    response.writeHead(200, { 'Content-Type': files[file] }).end(content);
  });
}).listen(port, '127.0.0.1', () => {
  console.log(`Back Office Portal: http://localhost:${port}`);
});
