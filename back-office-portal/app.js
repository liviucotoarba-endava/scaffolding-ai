const search = document.querySelector('#search');
const rows = [...document.querySelectorAll('tbody tr')];
const resultCount = document.querySelector('#result-count');
const emptyState = document.querySelector('#empty-state');

search.addEventListener('input', () => {
  const query = search.value.trim().toLowerCase();
  let count = 0;

  for (const row of rows) {
    const nameAndEmail = `${row.cells[0].textContent} ${row.cells[1].textContent}`;
    row.hidden = !nameAndEmail.toLowerCase().includes(query);
    if (!row.hidden) count += 1;
  }

  resultCount.textContent = `${count} ${count === 1 ? 'user' : 'users'}`;
  emptyState.hidden = count > 0;
});
