export default class Profiler {
  public readonly div: HTMLDivElement;
  public readonly iframe: HTMLIFrameElement;

  constructor() {
    this.div = this.createSidebarDiv();
    this.iframe = this.createIFrame();
  }

  private createSidebarDiv(): HTMLDivElement {
    const base = document.createElement('div');

    // TODO(jmerle): Fill the sidebar

    return base;
  }

  private createIFrame(): HTMLIFrameElement {
    const frame = document.createElement('iframe');

    frame.src = 'bc20/speedscope/index.html';
    frame.onload = () => this.iframeOnLoad();

    return frame;
  }

  private iframeOnLoad(): void {
    const doc = this.iframe.contentDocument;

    if (doc == null) {
      return;
    }

    // Hide the Export and Import buttons in the top-right
    const css = `
        body > div > div:nth-child(2) > div:last-child > div:not(:last-child) {
          display: none !important;
        }
      `;

    const style = doc.createElement('style');
    style.innerHTML = css;
    doc.head.appendChild(style);
  }
}
