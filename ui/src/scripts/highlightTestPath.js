const performHighlight = (idArr, type, classDivName = '') => {
  const classDiv = classDivName ? document.getElementById(`classDiv-${classDivName}`) : document;
  idArr.forEach(id => {
    const elem = classDiv.querySelector(`#${id}`);
    console.log(elem);
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
  const nodesOfEdges = nodesArr.map((node => 'm' + node));
  const edgeIds = getPathName(nodesOfEdges, []);
  console.log(nodesOfEdges);
  console.log(edgeIds);
  performHighlight(nodesOfEdges, 'method');
  performHighlight(edgeIds, 'method');
};

const highlightClass = (classArr) => {
  const edgeIds = getPathName(classArr, []);
  performHighlight(classArr, 'class');
  performHighlight(edgeIds, 'class');
};

export default (tests) => {
  // highlightClass(tests);
  highlightMethod(tests);
};