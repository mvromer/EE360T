import { hideAllGraph } from './utils';

const genCrossEdges = (edges) => {
  let resultStat = '';
  edges.forEach(edge => {
    resultStat += `${edge[0]} --> ${edge[1]} \n`;
  });
  return resultStat;
};

const genMethodGraph = (methodName, methodData) => {
  let resultStat = `subgraph ${methodName} \n`;
  methodData.cfgEdges.forEach(edge => {
    resultStat += `${edge[0]} --> ${edge[1]} \n`;
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

export default (system) => {
  const classView = document.querySelector('.class-view');
  const methodView = document.querySelector('.method-view');
  const methodTitle = document.querySelector('.method-view__title');

  let systemDiv = `<div class="mermaid systemDiv"> classDiagram\n`;
  let classArr = [];

  system.classRelations.forEach(relation => {
    systemDiv += `${relation[0]} ${getRelationStat(relation[2])} ${relation[1]}\n`;
  });

  Object.entries(system.classes).forEach(([classKey, value]) => {
    const className = classKey;
    classArr.push(className);
    const crossEdges = value.crossEdges;
    const methods = value.methods;
    const fields = value.fields;

    let classDiv = `<div class="mermaid" id="classDiv-${classKey}"> graph TD \n`;
    classDiv += genCrossEdges(crossEdges);

    fields && fields.forEach(field => {
      systemDiv += `${className} : ${field}\n`;
    });

    Object.entries(methods).forEach(([methodKey, value]) => {
      classDiv += genMethodGraph(methodKey, value);
      systemDiv += `${className} : ${methodKey}()\n`;
    });
    classDiv += `</div>`;
    methodView.insertAdjacentHTML('beforeend', classDiv);
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
        arrow.style.top = `${y}px`;
        arrow.style.display = `block`;
      });
    });
  }, 1000);
};