const performHighlight = (idArr, type, classDivName = '') => {
  const classDiv = classDivName ? document.getElementById(`classDiv-${classDivName}`) : document;
  idArr.forEach(id => {
    const elem = classDiv.querySelector(`#${id}`);
    elem.classList.add('highlighted');
  });
};

const getPathName = (edges, result) => {
  if (edges.length >= 2) {
    result.push(`${edges[0].toString()}-${edges[1].toString()}`);
    return getPathName(edges.slice(1), result);
  } else {
    return result;
  }
};

const highlightMethod = (nodesArr) => {
  nodesArr.forEach(nodeData => {
    const classOfNodes = nodeData.class;
    const nodesOfEdges = nodeData.nodes;
    const edgeIds = getPathName(nodesOfEdges, []);
    performHighlight(nodesOfEdges, 'method', classOfNodes);
    performHighlight(edgeIds, 'method', classOfNodes);
  });
};

const highlightClass = (classArr) => {
  const edgeIds = getPathName(classArr, []);
  performHighlight(classArr, 'class');
  performHighlight(edgeIds, 'class');
};

export default (tests) => {
  const classedVisited = tests.classesVisited;
  const nodesVisited = tests.nodesVisited;
  highlightClass(classedVisited);
  highlightMethod(nodesVisited);
};