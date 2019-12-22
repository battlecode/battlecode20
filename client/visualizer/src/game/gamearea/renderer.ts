import * as config from '../../config';
import * as cst from '../../constants';
import NextStep from './../nextstep';

import {GameWorld, Metadata, schema, Game} from 'battlecode-playback';
import {AllImages} from '../../imageloader';
import Victor = require('victor');

/**
 * Renders the world.
 *
 * Note that all rendering functions draw in world-units,
 */
export default class Renderer {
  private conf: config.Config;

  readonly canvas: HTMLCanvasElement;
  readonly ctx: CanvasRenderingContext2D;
  readonly imgs: AllImages;
  readonly metadata: Metadata;

  // Callbacks
  readonly onRobotSelected: (id: number) => void;
  readonly onMouseover: (x: number, y: number, dirt: number, water: number, pollution: number, soup: number) => void;

  // For rendering robot information on click
  private lastSelectedID: number;

  // other cached useful values
  readonly bgPattern: CanvasPattern;

  constructor(canvas: HTMLCanvasElement, imgs: AllImages, conf: config.Config, metadata: Metadata,
    onRobotSelected: (id: number) => void,
    onMouseover: (x: number, y: number, dirt: number, water: number, pollution: number, soup: number) => void) {
    this.canvas = canvas;
    this.conf = conf;
    this.imgs = imgs;
    this.metadata = metadata;
    this.onRobotSelected = onRobotSelected;
    this.onMouseover = onMouseover;

    let ctx = canvas.getContext("2d");
    if (ctx === null) {
        throw new Error("Couldn't load canvas2d context");
    } else {
        this.ctx = ctx;
    }

    this.ctx['imageSmoothingEnabled'] = false;

    // TODO: can this be null???
    this.bgPattern = this.ctx.createPattern(imgs.background, 'repeat')!;
  }

  /**
   * world: world to render
   * time: time in turns
   * viewMin: min corner of view (in world units)
   * viewMax: max corner of view (in world units)
   */
  render(world: GameWorld, viewMin: Victor, viewMax: Victor, nextStep?: NextStep, lerpAmount?: number) {
    // setup correct rendering
    const viewWidth = viewMax.x - viewMin.x
    const viewHeight = viewMax.y - viewMin.y
    const scale = this.canvas.width / viewWidth;

    this.ctx.save();
    this.ctx.scale(scale, scale);
    this.ctx.translate(-viewMin.x, -viewMin.y);

    this.renderBackground(world);

    this.renderBodies(world, nextStep, lerpAmount);

    this.renderIndicatorDotsLines(world);
    this.setMouseoverEvent(world);

    // restore default rendering
    this.ctx.restore();
  }

  /**
   * Release resources.
   */
  release() {
    // nothing to do yet?
  }

  private renderBackground(world: GameWorld) {
    let dirtLayer = this.conf.viewDirt;
    let waterLayer = this.conf.viewWater;
    let pollutionLayer = this.conf.viewPoll;

    this.ctx.save();
    this.ctx.fillStyle = "white";
    this.ctx.globalAlpha = 1;

    const minX = world.minCorner.x;
    const minY = world.minCorner.y;
    const width = world.maxCorner.x - world.minCorner.x;
    const height = world.maxCorner.y - world.minCorner.y;

    const scale = 20;

    this.ctx.scale(1/scale, 1/scale);

    // scale the background pattern
    this.ctx.fillRect(minX*scale, minY*scale, width*scale, height*scale);

    const map = world.mapStats;

    // TODO use color pacakge for nicer manipulation?
    const getDirtColor = (x: number): string => {
      /*
      // -inf > 'rgba(89,156,28,1)'
      // inf -> 'rgba(156,28,28,1)'
      */
      // -inf-> 'rgba(0,255,0,0.7)'
      // inf -> 'rgba(255,0,0,0.7)'

      const lo = [0,255,0], hi = [255,0,0];

      // (-inf~inf) -> (0~1)
      // TODO getting inputs for color transition?
      const ex = Math.exp(x / 10);
      const t = ex / (5 + ex);

      let now = [0,0,0];
      for(let i=0; i<3; i++) now[i] = (hi[i]-lo[i]) * t + lo[i];

      return `rgba(${now[0]},${now[1]},${now[2]},0.7)`;
    }
    
    const getSoupColor = (s: number): string => {
      // TODO is this in right dimention?
      if (s <= 50)  return 'white';
      if (s <= 100) return 'yellow';
      return 'orange';
    }


    for (let i = 0; i < width; i++) for (let j = 0; j < height; j++){
      let idxVal = map.getIdx(i,j);
      let plotJ = height-j-1;

      const cx = (minX+i)*scale, cy = (minY+plotJ)*scale;

      this.ctx.fillStyle = 'white';
      this.ctx.globalAlpha = 1;


      if (dirtLayer) {// && (map.dirt[idxVal] > 0)) {
        // dirt should be a gradient from green to red depending on elevation
        let thisrgbcolor: string = getDirtColor(map.dirt[idxVal]);
        this.ctx.fillStyle = thisrgbcolor;
      }

      if (waterLayer && (map.flooded[idxVal] > 0)){
        // water should always be the same color
        this.ctx.fillStyle = 'rgba(0,0,255,1.0)';
      }

      // water covers dirt; we can fill only once
      this.ctx.fillRect(cx, cy, scale, scale);

      if (pollutionLayer) {
        // pollution should add a clouds that are black with some opacity
        this.ctx.fillStyle = 'black';
        this.ctx.globalAlpha = map.pollution[idxVal] / 100000.0;
        this.ctx.fillRect(cx, cy, scale, scale);
      }

      if (map.soup[idxVal] != 0){
        this.ctx.fillStyle = getSoupColor(map.soup[idxVal]);
        this.ctx.globalAlpha = 1;
        this.ctx.fillRect(cx+scale/3, cy+scale/3, scale/3, scale/3);
      }

      if (this.conf.showGrid) {
        this.ctx.strokeStyle = 'gray';
        this.ctx.globalAlpha = 1;
        this.ctx.strokeRect(cx, cy, scale, scale);
      }
    }

    this.ctx.restore();
  }

  private renderBodies(world: GameWorld, nextStep?: NextStep, lerpAmount?: number) {
    const bodies = world.bodies;
    const length = bodies.length;
    const types = bodies.arrays.type;
    const teams = bodies.arrays.team;
    const cargo = bodies.arrays.cargo;
    const ids = bodies.arrays.id;
    const xs = bodies.arrays.x;
    const ys = bodies.arrays.y;
    const minY = world.minCorner.y;
    const maxY = world.maxCorner.y -1;

    let nextXs: Int32Array, nextYs: Int32Array, realXs: Int32Array, realYs: Int32Array;
    if (nextStep && lerpAmount) {
      // Interpolated (not going to happen in 2019)
      nextXs = nextStep.bodies.arrays.x;
      nextYs = nextStep.bodies.arrays.y;
      lerpAmount = lerpAmount || 0;
    }
    else{
      // supposed to be error?
      // console.log("Error in renderer.ts");
      // return;
    }

    // Calculate the real xs and ys
    realXs = new Int32Array(length)
    realYs = new Int32Array(length)
    for (let i = 0; i < length; i++) {
      if (nextStep && lerpAmount && false) {
        // Interpolated
        console.log("This should not be executed");
        // realXs[i] = xs[i] + (nextXs[i] - xs[i]) * lerpAmount;
        // realYs[i] = this.flip(ys[i] + (nextYs[i] - ys[i]) * lerpAmount, minY, maxY);
      } else {
        // Not interpolated
        realXs[i] = xs[i];
        realYs[i] = this.flip(ys[i], minY, maxY);
      }
    }

    // Render the trees
    for (let i = 0; i < length; i++) {
      const team = teams[i];
      const type = types[i];
      const radius = 1;
      const x = realXs[i];
      const y = realYs[i];

      if (type === cst.COW) {
        const img = this.imgs.cow;
        // this.drawCircleBot(x, y, radius);
        // this.drawImage(img, x, y, radius);
        this.drawBot(img, x, y);
      }

    }

    // Render the robots
    for (let i = 0; i < length; i++) {
      const team = teams[i];
      const type = types[i];
      const x = realXs[i];
      const y = realYs[i];

      if (type !== cst.COW && type !== cst.NONE) {
        let tmp = this.imgs.robot[cst.bodyTypeToString(type)];
        // TODO how to change drone?
        if(type == cst.DRONE){
          tmp = (cargo[i]!=0 ? tmp.carry : tmp.empty);
        }

        const img = tmp[team];
        // this.drawCircleBot(x, y, radius);
        // this.drawImage(img, x, y, radius);
        this.drawBot(img, x, y);
        
        // Draw the sight radius if the robot is selected
        if (this.lastSelectedID === undefined || ids[i] === this.lastSelectedID) {
          this.drawSightRadii(x, y, type, ids[i] === this.lastSelectedID);
        }
      }
    }

    this.setInfoStringEvent(world, xs, ys);
  }

  /**
   * Returns the mirrored y coordinate to be consistent with (0, 0) in the
   * bottom-left corner (top-left corner is canvas default).
   * params: y coordinate to flip
   *         yMin coordinate of the minimum edge
   *         yMax coordinate of the maximum edge
   */
  private flip(y: number, yMin: number, yMax: number) {
    return yMin + yMax - y;
  }

  /**
   * Draws a circle centered at (x, y) with the given radius
   */
  private drawCircleBot(x: number, y: number, radius: number) {
    if (!this.conf.circleBots) return; // skip if the option is turned off

    this.ctx.beginPath();
    this.ctx.fillStyle = "#ddd";
    this.ctx.arc(x, y, radius, 0, 2 * Math.PI, false);
    this.ctx.fill();
  }

  /**
   * Draws a circular outline representing the sight radius or bullet sight
   * radius of the given robot type, centered at (x, y)
   */
  private drawSightRadii(x: number, y: number, type: schema.BodyType, single?: Boolean) {
    if (type === cst.COW) {
      return; // cows can't see...
    }

    if (this.conf.sightRadius || single) {
      const sightRadius = this.metadata.types[type].sensorRadius;
      this.ctx.beginPath();
      this.ctx.arc(x+0.5, y+0.5, sightRadius, 0, 2 * Math.PI);
      this.ctx.strokeStyle = "#46ff00";
      this.ctx.lineWidth = cst.SIGHT_RADIUS_LINE_WIDTH;
      this.ctx.stroke();
    } else {
      // console.log("drawSightRadii called, but should not draw it");
    }

  }

  /**
   * Draws an image centered at (x, y) with the given radius
   */
  private drawImage(img: HTMLImageElement, x: number, y: number, radius: number) {
    this.ctx.drawImage(img, x-radius, y-radius, radius*2, radius*2);
  }

  /**
   * Draws a bot at (x, y)
   */
  private drawBot(img: HTMLImageElement, x: number, y: number) {
    this.ctx.drawImage(img, x, y, 1, 1);
  }

  private setInfoStringEvent(world: GameWorld,
    xs: Int32Array, ys: Int32Array) {
    // world information
    const width = world.maxCorner.x - world.minCorner.x;
    const height = world.maxCorner.y - world.minCorner.y;
    const ids: Int32Array = world.bodies.arrays.id;
    // TODO: why is this Int8Array and not Int32Array?
    const types: Int8Array = world.bodies.arrays.type;
    // const radii: Float32Array = world.bodies.arrays.radius;
    const onRobotSelected = this.onRobotSelected;

    this.canvas.onmousedown = (event: MouseEvent) => {
      const {x, y} = this.getIntegerLocation(event, world);

      // Get the ID of the selected robot
      let selectedRobotID;
      for (let i in ids) {
        if (xs[i] == x && ys[i] == y) {
          selectedRobotID = ids[i];
          break;
        }
      }

      // Set the info string even if the robot is undefined
      this.lastSelectedID = selectedRobotID;
      onRobotSelected(selectedRobotID);
    };
  }

  private setMouseoverEvent(world: GameWorld) {
    // world information
    // const width = world.maxCorner.x - world.minCorner.x;
    // const height = world.maxCorner.y - world.minCorner.y;
    const onMouseover = this.onMouseover;
    // const minY = world.minCorner.y;
    // const maxY = world.maxCorner.y - 1;

    this.canvas.onmousemove = (event) => {
      // const x = width * event.offsetX / this.canvas.offsetWidth + world.minCorner.x;
      // const _y = height * event.offsetY / this.canvas.offsetHeight + world.minCorner.y;
      // const y = this.flip(_y, minY, maxY)

      // Set the location of the mouseover
      const {x, y} = this.getIntegerLocation(event, world);
      const idx = world.mapStats.getIdx(x, y);
      onMouseover(x, y, world.mapStats.dirt[idx], world.mapStats.flooded[idx], world.mapStats.pollution[idx], world.mapStats.soup[idx]);
    };
  }

  private getIntegerLocation(event: MouseEvent, world: GameWorld) {
    const width = world.maxCorner.x - world.minCorner.x;
    const height = world.maxCorner.y - world.minCorner.y;
    const minY = world.minCorner.y;
    const maxY = world.maxCorner.y - 1;
    const x = width * event.offsetX / this.canvas.offsetWidth + world.minCorner.x;
    const _y = height * event.offsetY / this.canvas.offsetHeight + world.minCorner.y;
    const y = this.flip(_y, minY, maxY)
    return {x: Math.floor(x), y: Math.floor(y+1)};
  }

  private renderIndicatorDotsLines(world: GameWorld) {
    if (!this.conf.indicators) {
      return;
    }

    const dots = world.indicatorDots;
    const lines = world.indicatorLines;

    // Render the indicator dots
    const dotsID = dots.arrays.id;
    const dotsX = dots.arrays.x;
    const dotsY = dots.arrays.y;
    const dotsRed = dots.arrays.red;
    const dotsGreen = dots.arrays.green;
    const dotsBlue = dots.arrays.blue;
    const minY = world.minCorner.y;
    const maxY = world.maxCorner.y - 1;

    for (let i = 0; i < dots.length; i++) {
      if (this.lastSelectedID === undefined || dotsID[i] === this.lastSelectedID) {
        const red = dotsRed[i];
        const green = dotsGreen[i];
        const blue = dotsBlue[i];
        const x = dotsX[i];
        const y = this.flip(dotsY[i], minY, maxY);

        this.ctx.beginPath();
        this.ctx.arc(x, y, cst.INDICATOR_DOT_SIZE, 0, 2 * Math.PI, false);
        this.ctx.fillStyle = `rgb(${red}, ${green}, ${blue})`;
        this.ctx.fill();
      }
    }

    // Render the indicator lines
    const linesID = lines.arrays.id;
    const linesStartX = lines.arrays.startX;
    const linesStartY = lines.arrays.startY;
    const linesEndX = lines.arrays.endX;
    const linesEndY = lines.arrays.endY;
    const linesRed = lines.arrays.red;
    const linesGreen = lines.arrays.green;
    const linesBlue = lines.arrays.blue;
    this.ctx.lineWidth = cst.INDICATOR_LINE_WIDTH;

    for (let i = 0; i < lines.length; i++) {
      if (this.lastSelectedID === undefined || linesID[i] === this.lastSelectedID) {
        const red = linesRed[i];
        const green = linesGreen[i];
        const blue = linesBlue[i];
        const startX = linesStartX[i];
        const startY = this.flip(linesStartY[i], minY, maxY);
        const endX = linesEndX[i];
        const endY = this.flip(linesEndY[i], minY, maxY);

        this.ctx.beginPath();
        this.ctx.moveTo(startX, startY);
        this.ctx.lineTo(endX, endY);
        this.ctx.strokeStyle = `rgb(${red}, ${green}, ${blue})`;
        this.ctx.stroke();
      }
    }
  }
}
