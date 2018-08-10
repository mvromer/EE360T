import { hideAllGraph, HtmlEncode } from './utils';

const genCrossEdges = (edges) => {
  let resultStat = '';
  edges.forEach(edge => {
    resultStat += `${edge[0]} --> ${edge[1]} \n`;
  });
  return resultStat;
};

const genMethodGraph = (methodData) => {
  let resultStat = `subgraph ${methodData.methodName.replace(/[^a-zA-Z0-9 ]/g, "")}-${methodData.methodDescriptor.replace(/[^a-zA-Z0-9 ]/g, "")} \n`;
  Object.entries(methodData.nodes).forEach(([nodeKey, nodeValue]) => {
    resultStat += `m${nodeKey}("${nodeValue}") \n`;
  });
  Object.entries(methodData.edges).forEach(([edgeKey, edgeValues]) => {
    edgeValues.forEach(edgeValue => {
      resultStat += `m${edgeKey} --> m${edgeValue} \n`;
    });
  });
  resultStat += 'end \n';
  return resultStat;
};

const getRelationStat = (relation) => {
  const map = {
    extension: '--|>',
    composition: '--*',
    aggregation: '--o',
    dependency: '-->',
  };

  if (!relation) {
    return `..`;
  } else {
    return map[relation];
  }
};

export default (controlFlows) => {
  const classView = document.querySelector('.class-view');
  const methodView = document.querySelector('.method-view');
  const methodTitle = document.querySelector('.method-view__title');

  let systemDiv = `<div class="mermaid systemDiv"> graph TD\n`;
  let classArr = [];

  // controlFlows.classRelations.forEach(relation => {
  //   systemDiv += `${relation[0]} ${getRelationStat(relation[2])} ${relation[1]}\n`;
  // });

  Object.entries(controlFlows).forEach(([classKey, value]) => {
    systemDiv += `subgraph ${value.classDisplayName.replace(/[^a-zA-Z0-9\/ ]/g, "")} \n`;
    const className = value.classDisplayName;
    classArr.push(className);
    // const crossEdges = value.crossEdges;
    const methods = value.methods;
    let classDiv = `<div class="mermaid" id="classDiv-${className}"> graph LR\n`;
    //classDiv += genCrossEdges(crossEdges);

    methods.forEach(method => {
      classDiv += genMethodGraph(method);
      systemDiv += `${method.methodId}["${method.methodName.replace(/[^a-zA-Z0-9\/ ]/g, "")}${method.methodDescriptor}"]\n`;
    });
    classDiv += `</div>`;
    methodView.insertAdjacentHTML('beforeend', classDiv);

    systemDiv += 'end \n';

  });

  systemDiv += `</div>`;

  classView.insertAdjacentHTML('beforeend', systemDiv);

  setTimeout(() => {
    classArr.forEach(classDivId => {
      const classElem = document.getElementById(classDivId);
      classElem.addEventListener('click', (evt) => {
        const arrow = document.querySelector('.arrow');
        hideAllGraph();
        document.getElementById(`classDiv-${classDivId}`).classList.add('show');
        methodTitle.textContent = `Class: ${classDivId}`;
        const y = evt.target.getBoundingClientRect().y;
        // arrow.style.top = `${y}px`;
        // arrow.style.display = `block`;
      });
    });
  }, 1000);
};