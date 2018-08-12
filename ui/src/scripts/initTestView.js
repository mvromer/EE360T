import highlightTestPath from './highlightTestPath';
import { removeAllHighlight } from './utils';

export default (tests, globalIdMap) => {
  const dropdown = document.querySelector('.header__view-test__dd');
  const testSet = {};

  tests.forEach((test) => {
    const visitedClassSet = new Set();
    const visitedMethodSet = new Set();
    test.tracePath.forEach(node => {
      visitedClassSet.add(globalIdMap[node.toString()].className);
      visitedMethodSet.add(globalIdMap[node.toString()].globalMethodId);
    });

    testSet[test.testMethodName] = {
      methodNodes: test.tracePath,
      callEdges: test.callEdges,
      classes: [...visitedClassSet],
      methods: [...visitedMethodSet],
    };

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
          console.log(value);
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
