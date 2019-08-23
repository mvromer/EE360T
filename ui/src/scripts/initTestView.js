import highlightTestPath from './highlightTestPath';
import { removeAllHighlight, setPercentage } from './utils';

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
    let classCoveredCount = 0;
    let methodCoveredCount = 0;
    let nodeCoveredCount = 0;
    switch(value) {
      case '':
        removeAllHighlight();
        window.count = {
          ...window.count,
          classCoveredCount,
          methodCoveredCount,
          nodeCoveredCount,
        }
        break;

      case 'all':
        removeAllHighlight();
        const totalClassSet = new Set();
        const totalMethodSet = new Set();
        const totalNodeSet = new Set();
        Object.entries(testSet).forEach(([key, test]) => {
          highlightTestPath(test);
          test.classes.forEach(klass => totalClassSet.add(klass));
          test.methods.forEach(method => totalMethodSet.add(method));
          test.methodNodes.forEach(node => totalNodeSet.add(node));
        });

        window.count = {
          ...window.count,
          classCoveredCount: totalClassSet.size,
          methodCoveredCount: totalMethodSet.size,
          nodeCoveredCount: totalNodeSet.size
        };
        break;

      default:
        const test = testSet[value];
        removeAllHighlight();
        highlightTestPath(test);
        window.count = {
          ...window.count,
          classCoveredCount: test.classes.length,
          methodCoveredCount: test.methods.length,
          nodeCoveredCount: test.methodNodes.length,
        };
        break;
    }
    setPercentage(window.count);
  });
};
