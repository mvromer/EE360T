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

  mermaid.init({
    noteMargin: 100
  }, ".systemDiv");
  mermaid.init({
    noteMargin: 100
  }, ".classDiv");
});