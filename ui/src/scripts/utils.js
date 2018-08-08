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
