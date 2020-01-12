import {Game} from "battlecode-playback";

enum Team {
  A, B
}

export default class Profiler {
  public readonly div: HTMLDivElement;
  public readonly iframe: HTMLIFrameElement;

  private teamSelector: HTMLSelectElement;
  private robotSelector: HTMLSelectElement;

  constructor() {
    this.div = this.createSidebarDiv();
    this.iframe = this.createIFrame();
  }

  public load(game: Game, match: number): void {
    // TODO(jmerle): Make sure to call this with all the profiling data when a match has ended

    this.clearSelect(this.teamSelector);
    this.clearSelect(this.robotSelector);

    this.addSelectOption(this.teamSelector, 'Team A (red)', 'A');
    this.addSelectOption(this.teamSelector, 'Team B (blue)', 'B');

    this.onTeamChange(Team.A);
  }

  private createSidebarDiv(): HTMLDivElement {
    const base = document.createElement('div');
    base.id = 'profiler';

    this.teamSelector = document.createElement('select');
    this.robotSelector = document.createElement('select');

    this.teamSelector.onchange = () => {
      const selectedIndex = this.teamSelector.selectedIndex;
      if (selectedIndex > -1) {
        this.onTeamChange(this.teamSelector.options[selectedIndex].value === 'A' ? Team.A : Team.B);
      }
    };

    this.robotSelector.onchange = () => {
      const selectedIndex = this.robotSelector.selectedIndex;
      if (selectedIndex > -1) {
        this.onRobotChange(parseInt(this.robotSelector.options[selectedIndex].value, 10));
      }
    };

    base.appendChild(document.createTextNode('If no teams are visible, make sure to run a game with profiling enabled by ticking the checkbox on the Runner tab or to load a replay of a game that had profiling enabled.'));
    base.appendChild(document.createElement('br'));
    base.appendChild(document.createElement('br'));

    base.appendChild(this.createSidebarFormItem('Team', this.teamSelector));
    base.appendChild(this.createSidebarFormItem('Robot', this.robotSelector));

    return base;
  }

  private createSidebarFormItem(name: string, select: HTMLSelectElement): HTMLElement {
    const item = document.createElement('div');

    const label = document.createElement('span');
    label.textContent = `${name}: `;

    item.appendChild(label);
    item.appendChild(select);
    item.appendChild(document.createElement('br'));

    return item;
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

    // Hide the Export and Import buttons in the top-right corner and certain elements on the homepage
    const css = `
      body > div > div:nth-child(2) > div:last-child > div:not(:last-child),
      body > div > div:nth-child(3) > div > div > p:nth-child(2),
      #file,
      label[for="file"] {
        display: none !important;
      }
      
    `;

    const style = doc.createElement('style');
    style.innerHTML = css;
    doc.head.appendChild(style);
  }

  private addSelectOption(select: HTMLSelectElement, label: string, value: string = label): void {
    const option = document.createElement('option');
    option.text = label;
    option.value = value;
    select.add(option);
  }

  private clearSelect(select: HTMLSelectElement): void {
    select.options.length = 0;
  }

  private onTeamChange(newTeam: Team): void {
    this.clearSelect(this.robotSelector);

    this.addSelectOption(this.robotSelector, '#0 (HQ)', '0');
    this.addSelectOption(this.robotSelector, '#1 (MINER)', '1');

    this.onRobotChange(0);

    // TODO(jmerle): Load robots from newTeam
  }

  private onRobotChange(newRobotId: number): void {
    console.log(`Changed robot to ${newRobotId}`);

    // TODO(jmerle): Load newRobotId's profiling results
  }
}
