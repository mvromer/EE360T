import '../styles/index.scss';
import genClassGraph from './genClassGraph';
import initTestView from './initTestView';
import {
  removeAllHighlight
} from './utils';

const loadJSON = (file, callback) => {
  var xobj = new XMLHttpRequest();
  xobj.overrideMimeType("application/json");
  xobj.open('GET', file, true); // Replace 'my_data' with the path to your file
  xobj.onreadystatechange = function () {
    if (xobj.readyState == 4 && xobj.status == "200") {
      // Required use of an anonymous callback as .open will NOT return a value but simply returns undefined in asynchronous mode
      callback(xobj.responseText);
    }
  };
  xobj.send(null);
};

loadJSON("results.json", (response) => {
  const data = JSON.parse(response);
  const { controlFlows, traceRecords, globalIdToNodeId } = data;

  genClassGraph(controlFlows);
  initTestView(traceRecords);

  mermaid.initialize({
    // theme: 'forest',
    // themeCSS: '.label{color:#333}.node circle,.node ellipse,.node polygon,.node rect{fill:#a0dcff;stroke:#3666a0;stroke-width:1px}.node.clickable{cursor:pointer}.arrowheadPath{fill:#3666a0}.edgePath .path{stroke:#3666a0;stroke-width:1.5px}.edgeLabel{background-color:#8ab9b5}.cluster rect{fill:#c8c2ae!important;stroke:#c8c2ae!important;stroke-width:1px!important}.cluster text{fill:#0a1128}div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:trebuchet ms,verdana,arial;font-size:12px;background:#c8c2ae;border:1px solid #c8c2ae;border-radius:2px;pointer-events:none;z-index:100}.class{display:flex}.class__method{text-align:center}',
    logLevel: 3,
    flowchart: {
      curve: 'linear',
      useMaxWidth: false,
      htmlLabels: true
    },
    // sequenceDiagram: { actorMargin: 300 } // deprecated
  });

  // const insertSvg = (svg, bindfunction) => {
  //   console.log(svg);
  //   console.log(bindfunction);
  // };

  // var graphDefinition = 'graph TB\na-->b';
  // var graph = mermaidAPI.render('graphDiv', graphDefinition, insertSvg);

  document.addEventListener('click', (evt) => {
    const target = evt.target.className;
    switch (target) {
      case 'header__control-highlight':
        highlightTestPath(testRuns);
        break;
      case 'header__control-restore':
        removeAllHighlight();
    }
  });
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