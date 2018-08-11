export const removeAllHighlight = () => {
  const highLightElems = document.querySelectorAll('.highlighted');
  [...highLightElems].forEach(elem => {
    elem.classList.remove('highlighted');
  });
};

export const hideAllGraph = () => {
  const shownElems = document.querySelectorAll('.show');
  [...shownElems].forEach(elem => {
    elem.classList.remove('show');
  });
};

export const disselectALL = () => {
  const selectedElem = document.querySelector('.selected');
  selectedElem && selectedElem.classList.remove('selected');
};

export const loadJSON = (file, callback) => {
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
