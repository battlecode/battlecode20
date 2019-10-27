import {Config} from './config';

type Image = HTMLImageElement;

export type AllImages = {
  background: Image, 
  star: Image,
  robot: {
    netGun: Image,
    landscaper: Image,
    miner: Image,
    fulfillmentCenter: Image,
    drone: {
      empty: Image,
      full: Image
    }, 
    souperCenter: Image,
    refinery: Image,
    vaporator: Image,
    hq: Image
  },
  cow: Image,
  controls: {
    goNext: Image,
    goPrevious: Image,
    playbackPause: Image,
    playbackStart: Image,
    playbackStop: Image,
    matchForward: Image,
    matchBackward: Image,
    upload: Image
  }
};

export function loadAll(config: Config, finished: (AllImages) => void) {
  let expected = 0, loaded = 0;
  let result: any = {robot: {netGun: [], landscaper: [], miner: [], fulfillmentCenter: [], drone: {}, souperCenter: [], refinery: [], vaporator: [], hq: []}, controls: {}};

  // write loaded image to obj[slot]
  function img(obj, slot, url: string) {
    // we expect another one
    expected++;
    let image = new Image();
    image.onload = () => {
      obj[slot] = image;
      // hey, we found it
      loaded++;
      if (loaded === expected) {
        console.log('All images loaded.');
        finished(Object.freeze(result) as AllImages);
      }
    };
    image.onerror = () => {
      loaded++;
      console.log(`CANNOT LOAD IMAGE: ${url}`);
      if (loaded === expected) {
        console.log('All images loaded.');
        finished(Object.freeze(result) as AllImages);
      }
    }
    image.src = url;
  }

  const dirname = "./static/img/";

  img(result, 'background', require(dirname + 'map/tiled_1.jpg'));
  img(result, 'unknown', require(dirname + 'sprites/unknown.png'));
  img(result, 'star', require(dirname + 'yellow_star.png'));
  // TODO make cow
  img(result, 'cow', require(dirname + 'sprites/unknown.png'));


  // these are the teams we expect robots to be in according to current
  // battlecode-server
  // TODO(jhgilles):
  // we'll need to update them if team configuration becomes more dynamic
  img(result.drone, 'full_0', require(dirname + 'sprites/Drone_red_carry.png'));
  img(result.drone, 'empty_0', require(dirname + 'sprites/Drone_red.png'));
  img(result.drone, 'full_1', require(dirname + 'sprites/Drone_blue_carry.png'));
  img(result.drone, 'empty_1', require(dirname + 'sprites/Drone_blue.png'));

  img(result.robot.netGun, 0, require(dirname + 'sprites/Net_gun_red.png'));
  img(result.robot.netGun, 1, require(dirname + 'sprites/Net_gun_blue.png'));
  
  img(result.robot.landscaper, 0, require(dirname + 'sprites/Landscaper_red.png'));
  img(result.robot.landscaper, 1, require(dirname + 'sprites/Landscaper_blue.png'));
  
  img(result.robot.miner, 0, require(dirname + 'sprites/Miner_red.png'));
  img(result.robot.miner, 1, require(dirname + 'sprites/Miner_blue.png'));
  
  img(result.robot.fulfillmentCenter, 0, require(dirname + 'sprites/Fulfillment_red.png'));
  img(result.robot.fulfillmentCenter, 1, require(dirname + 'sprites/Fulfillment_blue.png'));
  
  img(result.robot.souperCenter, 0, require(dirname + 'sprites/SOUPER_red.png'));
  img(result.robot.souperCenter, 1, require(dirname + 'sprites/SOUPER_blue.png'));
  
  img(result.robot.refinery, 0, require(dirname + 'sprites/Refinery_red.png'));
  img(result.robot.refinery, 1, require(dirname + 'sprites/Refinery_blue.png'));
  
  img(result.robot.vaporator, 0, require(dirname + 'sprites/Vaporator_red.png'));
  img(result.robot.vaporator, 1, require(dirname + 'sprites/Vaporator_blue.png'));
  
  img(result.robot.hq, 0, require(dirname + 'sprites/HQ_red.png'));
  img(result.robot.hq, 1, require(dirname + 'sprites/HQ_blue.png'));
  

  img(result.controls, 'goNext', require(dirname + 'controls/go-next.png'));
  img(result.controls, 'goPrevious', require(dirname + 'controls/go-previous.png'));
  img(result.controls, 'playbackPause', require(dirname + 'controls/playback-pause.png'));
  img(result.controls, 'playbackStart', require(dirname + 'controls/playback-start.png'));
  img(result.controls, 'playbackStop', require(dirname + 'controls/playback-stop.png'));
  img(result.controls, 'matchBackward', require(dirname + 'controls/skip-backward.png'));
  img(result.controls, 'matchForward', require(dirname + 'controls/skip-forward.png'));
  img(result.controls, 'upload', require(dirname + 'controls/upload.png'));
}


