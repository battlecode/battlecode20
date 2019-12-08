import {Config, Mode} from '../config';
import * as imageloader from '../imageloader';
import * as cst from '../constants';

/**
 * Game controls: pause/unpause, fast forward, rewind
 */
export default class Controls {
  div: HTMLDivElement;
  wrapper: HTMLDivElement;

  readonly timeReadout: Text;
  readonly speedReadout: HTMLSpanElement;
  readonly tileInfo: Text;
  readonly infoString: HTMLTableDataCellElement;

  // Callbacks initialized from outside Controls
  // Yeah, it's pretty gross :/
  onGameLoaded: (data: ArrayBuffer) => void;
  onTogglePause: () => void;
  onToggleUPS: () => void;
  onToggleRewind: () => void;
  onStepForward: () => void;
  onStepBackward: () => void;
  onSeek: (frame: number) => void;

  // qualities of progress bar
  canvas: HTMLCanvasElement;
  maxFrame: number;
  ctx;
  curUPS: number;

  // buttons
  readonly conf: Config;
  readonly imgs: {
    playbackStart: HTMLImageElement,
    playbackPause: HTMLImageElement,
    playbackStop: HTMLImageElement,
    goNext: HTMLImageElement,
    goPrevious: HTMLImageElement,
    reverseUPS: HTMLImageElement,
    doubleUPS: HTMLImageElement,
    halveUPS: HTMLImageElement,
  };
  readonly buttonTexts: {
    playbackStart: string,
    playbackPause: string,
    playbackStop: string,
    goNext: string,
    goPrevious: string,
    reverseUPS: string,
    doubleUPS: string,
    halveUPS: string,
  };

  constructor(conf: Config, images: imageloader.AllImages) {
    this.div = this.baseDiv();
    this.timeReadout = document.createTextNode('No match loaded');
    this.tileInfo = document.createTextNode('X | Y | Dirt | Water | Pollution');
    this.speedReadout = document.createElement('span');
    this.speedReadout.style.cssFloat = 'right';
    this.speedReadout.textContent = 'UPS: 0 FPS: 0';

    // initialize the images
    this.conf = conf;
    this.imgs = {
      playbackStart: images.controls.playbackStart,
      playbackPause: images.controls.playbackPause,
      playbackStop: images.controls.playbackStop,
      goNext: images.controls.goNext,
      goPrevious: images.controls.goPrevious,
      reverseUPS: images.controls.reverseUPS,
      doubleUPS: images.controls.doubleUPS,
      halveUPS: images.controls.halveUPS,
    }
    this.buttonTexts = {
      playbackStart: 'Start',
      playbackPause: 'Pause',
      playbackStop: 'Stop',
      goNext: 'Next',
      goPrevious: 'Prev',
      reverseUPS: 'Reverse',
      doubleUPS: 'Faster',
      halveUPS: 'Slower',
    }

    let table = document.createElement("table");
    let tr = document.createElement("tr");

    // create the timeline
    let timeline = document.createElement("td");
    timeline.appendChild(this.timeline());
    timeline.appendChild(document.createElement("br"));
    timeline.appendChild(this.timeReadout);
    timeline.appendChild(this.speedReadout);

    this.curUPS = 16;

    // create the button controls
    let buttons = document.createElement("td");
    buttons.vAlign = "top";

    let reverseButton = this.createButton('reverseUPS', () => this.reverseUPS());

    let halveButton = this.createButton('halveUPS', () => this.halveUPS());
    let goPreviousButton = this.createButton('goPrevious', () => this.stepBackward());
    let pauseStartButton = this.createButton('playbackPause', () => this.pause(), 'playbackStart');
    let goNextButton = this.createButton('goNext', () => this.stepForward());
    let doubleButton = this.createButton('doubleUPS', () => this.doubleUPS());

    let stopButton = this.createButton('playbackStop', () => this.restart());

    buttons.appendChild(reverseButton);
    buttons.appendChild(halveButton);
    buttons.appendChild(goPreviousButton);
    buttons.appendChild(pauseStartButton);
    buttons.appendChild(goNextButton);
    buttons.appendChild(doubleButton);
    buttons.appendChild(stopButton);
    buttons.appendChild(document.createElement("br"));
    buttons.appendChild(this.tileInfo);

    pauseStartButton.title =  "Pause/resume";
    stopButton.title = "Stop";
    goPreviousButton.title = "Step back";
    goNextButton.title = "Step forward";
    doubleButton.title = "Double Speed";
    halveButton.title = "Halve Speed";
    reverseButton.title = "Play Reverse";

    // create the info string display
    let infoString = document.createElement("td");
    infoString.vAlign = "top";
    infoString.style.fontSize = "11px";
    this.infoString = infoString;

    table.appendChild(tr);
    tr.appendChild(timeline);
    tr.appendChild(document.createElement('div'));
    tr.appendChild(buttons);
    tr.appendChild(infoString);

    this.wrapper = document.createElement("div");
    this.wrapper.appendChild(table);
    this.div.appendChild(this.wrapper);
  }

  /**
   * @param content name of the image in this.imgs to display in the button
   * @param onclick function to call on click
   * @param hiddenContent name of the image in this.imgs to display as none
   * @return a button with the given attributes
   */
  private createButton(content, onclick, hiddenContent?) {
    let button = document.createElement("button");
    button.setAttribute("class", "custom-button");
    button.setAttribute("type", "button");
    button.id = content;

    if (content != null) button.appendChild(this.imgs[content]);
    if (content != null) button.innerText = this.buttonTexts[content];

    if (hiddenContent != null) {
      let hiddenImage = this.imgs[hiddenContent];
      hiddenImage.style.display = "none";
      button.appendChild(hiddenImage);
    }
    button.onclick = onclick;

    return button;
  }

  /**
   * Make the controls look good
   */
  private baseDiv() {
    let div = document.createElement("div");
    div.id = "baseDiv";

    return div;
  }

  private timeline() {
    let canvas = document.createElement("canvas");
    canvas.id = "timelineCanvas";
    canvas.width = 400;
    canvas.height = 1;
    this.ctx = canvas.getContext("2d");
    this.ctx.fillStyle = "white";
    this.canvas = canvas;
    return canvas;
  }

  /**
   * Returns the UPS determined by the slider
   */
  getUPS(): number {
    return this.curUPS;
  }

  /**
   * Displays the correct controls depending on whether we are in game mode
   * or map editor mode
   */
  setControls = () => {
    const mode = this.conf.mode;

    // The controls can be anything in help mode
    if (mode === Mode.HELP) return;

    // Otherwise clear the controls...
    while (this.div.firstChild) {
      this.div.removeChild(this.div.firstChild);
    }

    // ...and add the correct thing
    if (mode !== Mode.MAPEDITOR) {
      this.div.appendChild(this.wrapper);
    }
  };

  /**
   * Upload a battlecode match file.
   */
  loadMatch(files: FileList) {
    console.log(files);
    const file = files[0];
    console.log(file);
    const reader = new FileReader();
    reader.onload = () => {
      this.onGameLoaded(<ArrayBuffer>reader.result);
    };
    reader.readAsArrayBuffer(file);
  }

  /**
   * Whether or not the simulation is paused.
   */
  isPaused() {
    return this.imgs.playbackPause.style.display === "none";
  }

  /**
   * Pause our simulation.
   */
  pause() {
    this.onTogglePause();

    // toggle the play/pause button
    if (this.isPaused()) {
      this.imgs["playbackStart"].style.display = "none";
      this.imgs["playbackPause"].style.display = "unset";
    } else {
      this.imgs["playbackStart"].style.display = "unset";
      this.imgs["playbackPause"].style.display = "none";
    }
  }

  /**
   * Restart simulation.
   */
  restart() {
    const pauseButton = document.getElementById("playbackPause");
    if (!this.isPaused() && pauseButton) {
      pauseButton.click();
    }
    this.onSeek(0);
  }

  /**
   * Steps forward one turn in the simulation
   */
  stepForward() {
    this.onStepForward();
  }

  /**
   * Steps backward one turn in the simulation
   */
  stepBackward() {
    this.onStepBackward();
  }

  /**
   * Doubles UPS (Max 128)
   */
  doubleUPS() {
    if (Math.abs(this.curUPS) < 128){
      this.curUPS = this.curUPS * 2;
      this.onToggleUPS();
    }
  }

  /**
   * Halves UPS (Min 1)
   */
  halveUPS() {
    if (Math.abs(this.curUPS) > 1){
      this.curUPS = this.curUPS / 2;
      this.onToggleUPS();
    }
  }

  /**
   * Changes the sign of UPS
   */
  reverseUPS() {
    this.curUPS = - this.curUPS;
    this.onToggleUPS();
  }

  /**
   * Redraws the timeline and sets the current round displayed in the controls.
   */
  setTime(time: number, loadedTime: number, ups: number, fps: number, lagging: Boolean) {
    // Redraw the timeline
    const scale = this.canvas.width / loadedTime;
    // const scale = this.canvas.width / cst.MAX_ROUND_NUM;
    this.ctx.fillStyle = "rgb(39, 39, 39)";
    this.ctx.fillRect(0, 0, time * scale, this.canvas.height)
    this.ctx.fillStyle = "#777";
    this.ctx.fillRect(time * scale, 0, (loadedTime - time) * scale, this.canvas.height)
    this.ctx.clearRect(loadedTime * scale, 0, this.canvas.width, this.canvas.height)

    // Edit the text
    this.timeReadout.textContent = `TIME: ${time+1}/${loadedTime+1}`;

    let speedText = (lagging ? '(Lagging) ' : '') + `UPS: ${ups | 0} FPS: ${fps | 0}`;
    speedText = speedText.padStart(32);
    this.speedReadout.textContent = speedText;
  }

  /**
   * Updates the location readout
   */
  setTileInfo(x: number, y: number, dirt: number, water: number, pollution: number): void {
    let content: string = "";
    content += 'X: ' + `${Math.floor(x)}`.padStart(3);
    content += ' | Y: ' + `${Math.floor(y)}`.padStart(3);
    if(dirt !== undefined) content += ' | D: ' + `${dirt}`.padStart(3);
    if(water !== undefined) content += ' | W: ' + `${water}`.padStart(3);
    if(pollution !== undefined) content += ' | P: ' + `${pollution}`.padStart(3);

    this.tileInfo.textContent = content;
  }

  /**
   * Display an info string in the controls bar
   * "Robot ID id
   * Location: (x, y)
   * Health: health/maxHealth
   * Bytecodes Used: bytecodes"
   */
  setInfoString(id, x, y, health, maxHealth, bytecodes?: number): void {
    if (bytecodes !== undefined) {
      // Not a neutral tree or bullet tree
      this.infoString.innerHTML = `Robot ID ${id}<br>
        Location: (${x.toFixed(3)}, ${y.toFixed(3)})<br>
        Health: ${health.toFixed(3)}/${maxHealth.toFixed(3)}<br>
        Bytecodes Used: ${bytecodes}`;
    } else {
      // Neutral tree or bullet tree, no bytecode information
      this.infoString.innerHTML = `Robot ID ${id}<br>
        Location: (${x.toFixed(3)}, ${y.toFixed(3)})<br>
        Health: ${health.toFixed(3)}/${maxHealth.toFixed(3)}`;
    }
  }
}
