import Split from 'split.js';

import '../styles/index.scss';
import genClassGraph from './genClassGraph';
import initTestView from './initTestView';
import {
  loadJSON
} from './utils';

// split screen
Split(['#class-view', '#method-view'], {
  sizes: [50, 50],
  direction: 'vertical'
});

// loading json data
loadJSON("results.json", (response) => {
  const data = JSON.parse(response);
  const {
    controlFlows,
    traceRecords,
    globalToLocalNodeId,
    callGraph,
  } = data;

  genClassGraph(controlFlows, callGraph);
  initTestView(traceRecords, globalToLocalNodeId);

  // mermaidAPI.initialize({
  //   // theme: 'forest',
  //   // themeCSS: '.label{color:#333}.node circle,.node ellipse,.node polygon,.node rect{fill:#a0dcff;stroke:#3666a0;stroke-width:1px}.node.clickable{cursor:pointer}.arrowheadPath{fill:#3666a0}.edgePath .path{stroke:#3666a0;stroke-width:1.5px}.edgeLabel{background-color:#8ab9b5}.cluster rect{fill:#c8c2ae!important;stroke:#c8c2ae!important;stroke-width:1px!important}.cluster text{fill:#0a1128}div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:trebuchet ms,verdana,arial;font-size:12px;background:#c8c2ae;border:1px solid #c8c2ae;border-radius:2px;pointer-events:none;z-index:100}.class{display:flex}.class__method{text-align:center}',
  //   logLevel: 3,
  //   flowchart: {
  //     curve: 'linear',
  //     useMaxWidth: true,
  //     htmlLabels: true
  //   },
  //   // sequenceDiagram: { actorMargin: 300 } // deprecated
  // });

  mermaid.init({
    noteMargin: 100
  }, ".systemDiv");
  mermaid.init({
    noteMargin: 100
  }, ".classDiv");
});


// fetch('example.json')
//   .then(res => res.json())
//   .then(res => {
//     genClassGraph(res.system);
//     tests = res.testRuns;
//     initTestView(tests);
//   })
//   .then(() => {
//     mermaid.initialize({
//       // theme: 'forest',
//       // themeCSS: '.label{color:#333}.node circle,.node ellipse,.node polygon,.node rect{fill:#a0dcff;stroke:#3666a0;stroke-width:1px}.node.clickable{cursor:pointer}.arrowheadPath{fill:#3666a0}.edgePath .path{stroke:#3666a0;stroke-width:1.5px}.edgeLabel{background-color:#8ab9b5}.cluster rect{fill:#c8c2ae!important;stroke:#c8c2ae!important;stroke-width:1px!important}.cluster text{fill:#0a1128}div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:trebuchet ms,verdana,arial;font-size:12px;background:#c8c2ae;border:1px solid #c8c2ae;border-radius:2px;pointer-events:none;z-index:100}.class{display:flex}.class__method{text-align:center}',
//       logLevel: 3,
//       flowchart: {
//         curve: 'linear',
//         useMaxWidth: false,
//         htmlLabels:true
//       },
//       // sequenceDiagram: { actorMargin: 300 } // deprecated
//     });

//     document.addEventListener('click', (evt) => {
//       const target = evt.target.className;
//       switch(target) {
//         case 'header__control-highlight':
//           highlightTestPath(tests);
//           break;
//         case 'header__control-restore':
//           removeAllHighlight();
//       }
//     });
//   });