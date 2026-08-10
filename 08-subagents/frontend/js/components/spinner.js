export function createSpinner() {
  const wrapper = document.createElement('div');
  wrapper.className = 'spinner-wrapper';
  const spinner = document.createElement('div');
  spinner.className = 'spinner';
  wrapper.appendChild(spinner);
  return wrapper;
}
