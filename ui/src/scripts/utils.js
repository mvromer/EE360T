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

export const HtmlEncode = (s) => {
  var el = document.createElement("div");
  el.innerText = el.textContent = s;
  s = el.innerHTML;
  return s;
};
