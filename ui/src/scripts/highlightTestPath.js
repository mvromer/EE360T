const performHighlight = (idArr) => {
  idArr.forEach(id => {
    const elem = document.getElementById(`${id}`);
    elem && elem.classList.add('highlighted');
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

const highlightMethodNodes = (nodesArr) => {
  const nodesOfEdges = nodesArr.map((node => 'm' + node));
  const edgeIds = getPathName(nodesOfEdges, []);
  performHighlight(nodesOfEdges);
  performHighlight(edgeIds);
};

const highlightCallEdges = (callEdges) => {
  Object.entries(callEdges).forEach(([key, values]) => {
    values.forEach(value => {
      performHighlight([`${key}-${value}`]);
    });
  });
};

export default (tests) => {
  performHighlight(tests.classes);
  performHighlight(tests.methods);
  highlightCallEdges(tests.callEdges);
  highlightMethodNodes(tests.methodNodes);
};
