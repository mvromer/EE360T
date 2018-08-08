import highlightTestPath from './highlightTestPath';
import { removeAllHighlight } from './utils';

export default (tests) => {
  const dropdown = document.querySelector('.header__view-test__dd');
  const testSet = [];

  Object.entries(tests).forEach(([testKey, data]) => {
    testSet.push(testKey);
    dropdown.insertAdjacentHTML('beforeend', `
      <option value="${testKey}">${testKey}</option>
    `);
  });

  dropdown.addEventListener('change', (evt) => {
    const value = evt.target.value;
    switch(value) {
      case '':
        removeAllHighlight();
        break;
      case 'all':
        removeAllHighlight();
        testSet.forEach(test => {
          highlightTestPath(tests[test]);
        });
        break;
      default:
        const test = tests[value];
        removeAllHighlight();
        highlightTestPath(test);
        break;
    }
  });
};
