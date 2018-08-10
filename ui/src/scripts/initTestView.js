import highlightTestPath from './highlightTestPath';
import { removeAllHighlight } from './utils';

export default (tests) => {
  const dropdown = document.querySelector('.header__view-test__dd');
  const testSet = {};

  tests.forEach((test) => {
    testSet[test.testMethodName] = test.tracePath;
    dropdown.insertAdjacentHTML('beforeend', `
      <option value="${test.testMethodName}">${test.testMethodName}</option>
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
        Object.entries(testSet).forEach(([key, value]) => {
          highlightTestPath(value);
        });
        break;
      default:
        const test = testSet[value];
        removeAllHighlight();
        highlightTestPath(test);
        break;
    }
  });
};
