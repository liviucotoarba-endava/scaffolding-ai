const test = require('node:test');
const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const { join } = require('node:path');
const { runInNewContext } = require('node:vm');

const source = readFileSync(join(__dirname, '..', 'app.js'), 'utf8');

function createApp() {
  const search = new EventTarget();
  const resultCount = { textContent: '3 users' };
  const emptyState = { hidden: true };
  const rows = [
    ['Ada Lovelace', 'ada@example.com', 'Admin'],
    ['Grace Hopper', 'grace@example.com', 'Editor'],
    ['Alan Turing', 'alan@example.com', 'Viewer'],
  ].map(values => ({
    cells: values.map(textContent => ({ textContent })),
    hidden: false,
  }));
  const elements = {
    '#search': search,
    '#result-count': resultCount,
    '#empty-state': emptyState,
  };

  runInNewContext(source, {
    document: {
      querySelector: selector => elements[selector],
      querySelectorAll: selector => selector === 'tbody tr' ? rows : [],
    },
  });

  return query => {
    search.value = query;
    search.dispatchEvent(new Event('input'));
    return {
      names: rows.filter(row => !row.hidden).map(row => row.cells[0].textContent),
      count: resultCount.textContent,
      empty: !emptyState.hidden,
    };
  };
}

const allNames = ['Ada Lovelace', 'Grace Hopper', 'Alan Turing'];

const cases = [
  ['matches part of a name, ignoring case and surrounding spaces', '  HOPP  ', ['Grace Hopper'], '1 user'],
  ['matches an email address', 'ada@', ['Ada Lovelace'], '1 user'],
  ['keeps multiple matching users visible', 'la', ['Ada Lovelace', 'Alan Turing'], '2 users'],
  ['shows an empty state when nothing matches', 'missing', [], '0 users'],
  ['does not search the role column', 'Editor', [], '0 users'],
  ['shows every user for an empty query', '', allNames, '3 users'],
  ['shows every user for a whitespace query', '   ', allNames, '3 users'],
];

for (const [description, query, names, count] of cases) {
  test(description, () => {
    const search = createApp();
    assert.deepEqual(search(query), { names, count, empty: names.length === 0 });
  });
}

test('clearing a search restores all users and hides the empty state', () => {
  const search = createApp();
  assert.deepEqual(search('missing'), { names: [], count: '0 users', empty: true });
  assert.deepEqual(search(''), { names: allNames, count: '3 users', empty: false });
});
