import { hideAllGraph, disselectALL } from './utils';

const genIntraClassEdges = (edges) => {
  let resultStat = '';
  Object.entries(edges).forEach(([edgeFrom, edgesTo]) => {
    edgesTo.forEach(edge => {
      resultStat += `m${edgeFrom} --> m${edge} \n`;
    });
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

  let systemDiv = `<div class=" systemDiv"> graph TD\n`;
  let classArr = [];

  // controlFlows.classRelations.forEach(relation => {
  //   systemDiv += `${relation[0]} ${getRelationStat(relation[2])} ${relation[1]}\n`;
  // });

  Object.entries(controlFlows).forEach(([classKey, value]) => {
    systemDiv += `subgraph ${value.classDisplayName.replace(/[^a-zA-Z0-9\/ ]/g, "")} \n`;
    const className = value.classDisplayName;
    classArr.push(className);
    const methods = value.methods;
    let classDiv = `<div class="classDiv" id="classDiv-${className}"> graph LR\n`;

    const intraClassEdges = value.intraclassEdges;
    if (Object.keys(intraClassEdges).length !== 0 && intraClassEdges.constructor === Object) {
      classDiv += genIntraClassEdges(intraClassEdges);
    }

    methods.forEach(method => {
      classDiv += genMethodGraph(method);
      systemDiv += `${method.globalMethodId}["${method.methodName.replace(/[^a-zA-Z0-9\/ ]/g, "")}${method.methodDescriptor}"]\n`;
    });
    classDiv += `</div>`;
    //console.log(classDiv);
    methodView.insertAdjacentHTML('beforeend', classDiv);

    systemDiv += 'end \n';

  });

  systemDiv += `</div>`;

  classView.insertAdjacentHTML('beforeend', systemDiv);

  setTimeout(() => {
    classArr.forEach(classDivId => {
      const classElem = document.getElementById(classDivId);
      classElem.addEventListener('click', (evt) => {
        hideAllGraph();
        disselectALL();
        classElem.classList.add('selected');
        document.getElementById(`classDiv-${classDivId}`).classList.add('show');
        methodTitle.textContent = `Class: ${classDivId}`;
      });
    });
    methodTitle.textContent = `Select a class to view its methods`;
  }, 0);

  // zoom fucntion
  const zoomIns = document.querySelectorAll('.zoom__in');
  const zoomOuts = document.querySelectorAll('.zoom__out');

  [...zoomIns].forEach(zoomIn => {
    zoomIn.addEventListener('click', (evt) => {
      const viewContainerName = evt.currentTarget.parentNode.dataset.view;
      const rate = evt.currentTarget.parentNode.querySelector('.zoom__rate');
      const viewContainerSvgs = document.querySelectorAll(`.${viewContainerName} svg`);
      [...viewContainerSvgs].forEach(viewContainerSvg => {
        const maxWidth = parseInt(viewContainerSvg.style.maxWidth.replace('px', ''));
        const width = parseInt(viewContainerSvg.getAttribute('width').replace('%', ''));
        viewContainerSvg.setAttribute('width', `${width+10}%`);
        viewContainerSvg.style.maxWidth = `${maxWidth+100}px`;
        rate.textContent = `${width+10}%`;
      });

    }, true);
  });

  [...zoomOuts].forEach(zoomOut => {
    zoomOut.addEventListener('click', (evt) => {
      const viewContainerName = evt.currentTarget.parentNode.dataset.view;
      const rate = evt.currentTarget.parentNode.querySelector('.zoom__rate');
      const viewContainerSvgs = document.querySelectorAll(`.${viewContainerName} svg`);
      [...viewContainerSvgs].forEach(viewContainerSvg => {
        const maxWidth = parseInt(viewContainerSvg.style.maxWidth.replace('px', ''));
        const width = parseInt(viewContainerSvg.getAttribute('width').replace('%', ''));
        viewContainerSvg.setAttribute('width', `${width-10}%`);
        viewContainerSvg.style.maxWidth = `${maxWidth-100}px`;
        rate.textContent = `${width-10}%`;
      });
    }, true);
  });


};