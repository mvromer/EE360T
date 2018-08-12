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

const genCallGraph = (methods) => {
  let resultStat = '';
  Object.entries(methods).forEach(([methodFrom, methodsTo]) => {
    methodsTo.forEach(method => {
      resultStat += `${methodFrom} --> ${method} \n`;
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

export default (controlFlows, callGraph) => {
  const classView = document.querySelector('.class-view');
  const methodView = document.querySelector('.method-view');
  const methodTitle = document.querySelector('.method-view__title');

  let systemDiv = `<div class=" systemDiv"> graph TD\n`;
  let classArr = [];

  systemDiv += genCallGraph(callGraph);

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

  const zoomFunc = (zoomBtn, isIncre) => {
    const viewContainerName = zoomBtn.parentNode.dataset.view;
    const rate = zoomBtn.parentNode.querySelector('.zoom__rate');
    const viewContainerSvgs = document.querySelectorAll(`.${viewContainerName} svg`);
    [...viewContainerSvgs].forEach(viewContainerSvg => {
      let maxWidth = parseInt(viewContainerSvg.style.maxWidth.replace('px', ''));
      let width = parseInt(viewContainerSvg.getAttribute('width').replace('%', ''));
      let maxHeight = width - 20;
      console.log(viewContainerSvg.style);
      if (isIncre) {
        maxWidth += 100;
        maxHeight += 10;
        width += 10;
      } else {
        maxWidth -= 100;
        maxHeight -= 10;
        width -= 10;
      }
      viewContainerSvg.setAttribute('width', `${width}%`);
      viewContainerSvg.style.maxWidth = `${maxWidth}px`;
      viewContainerSvg.style.maxHeight = `${maxHeight}%`;
      rate.textContent = `${width}%`;
    });
  };

  [...zoomIns].forEach(zoomIn => {
    zoomIn.addEventListener('click', (evt) => {
      zoomFunc(evt.currentTarget, true);
    });
  });

  [...zoomOuts].forEach(zoomOut => {
    zoomOut.addEventListener('click', (evt) => {
      zoomFunc(evt.currentTarget, false);
    });
  });


};