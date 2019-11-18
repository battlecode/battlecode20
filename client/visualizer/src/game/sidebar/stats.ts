import {Config} from '../../config';
import * as cst from '../../constants';
import {AllImages} from '../../imageloader';
import Console from './console'

import {schema} from 'battlecode-playback';

const hex: Object = {
  1: "#db3627",
  2: "#4f7ee6"
};

export type StatBar = {
  bar: HTMLDivElement,
  label: HTMLSpanElement
};

/**
* Loads game stats: team name, victory points, bullets, robot count
* We make the distinction between:
*    1) Team names - a global string identifier i.e. "Teh Devs"
*    2) Team IDs - each Battlecode team has a unique numeric team ID i.e. 0
*    3) In-game ID - used to distinguish teams in the current match only;
*       team 1 is red, team 2 is blue
*/
export default class Stats {

  readonly div: HTMLDivElement;
  private readonly images: AllImages;

  // Key is the team ID
  private robotTds: Object = {}; // Secondary key is robot type
  // private statBars: Map<number, { bullets: StatBar, vps: StatBar }>;q
  private statsTableElement: HTMLTableElement;

  private robotConsole: HTMLDivElement;

  // Note: robot types and number of teams are currently fixed regardless of
  // match info. Keep in mind if we ever change these, or implement this less
  // statically.

  readonly robots: schema.BodyType[] = [
    cst.MINER, cst.LANDSCAPER, cst.DRONE, cst.NET_GUN, cst.REFINERY, cst.VAPORATOR, cst.HQ, cst.DESIGN_SCHOOL, cst.FULFILLMENT_CENTER
  ];

  constructor(conf: Config, images: AllImages, robotConsole: Console) {
    this.images = images;
    this.div = document.createElement("div");

    let teamNames: Array<string> = ["?????", "?????"];
    let teamIDs: Array<number> = [1, 2];
    this.statsTableElement = document.createElement("table");
    this.robotConsole = robotConsole.div;
    this.initializeGame(teamNames, teamIDs);
  }

  /**
   * Colored banner labeled with the given teamName
   */
  private teamHeaderNode(teamName: string, inGameID: number) {
    let teamHeader: HTMLDivElement = document.createElement("div");
    teamHeader.className += ' teamHeader';

    let teamNameNode = document.createTextNode(teamName);
    teamHeader.style.backgroundColor = hex[inGameID];
    teamHeader.appendChild(teamNameNode);
    return teamHeader;
  }

  /**
   * Create the table that displays the robot images along with their counts.
   * Uses the teamID to decide which color image to display.
   */
  private robotTable(teamID: number, inGameID: number): HTMLTableElement {
    let table: HTMLTableElement = document.createElement("table");
    table.setAttribute("align", "center");

    // Create the table row with the robot images
    let robotImages: HTMLTableRowElement = document.createElement("tr");
    
    // Create the table row with the robot counts
    let robotCounts: HTMLTableRowElement = document.createElement("tr");

    for (let robot of this.robots) {
      let robotName: string = cst.bodyTypeToString(robot);
      let tdRobot: HTMLTableCellElement = document.createElement("td");

      if(robotName === "drone"){
        tdRobot.appendChild(this.images.robot[robotName]['carry'][inGameID]);
        tdRobot.appendChild(this.images.robot[robotName]['empty'][inGameID]);
      }
      else{
        tdRobot.appendChild(this.images.robot[robotName][inGameID]);
      }

      if(robotName === 'vaporator'){
        // Wrap around
        table.appendChild(robotImages);
        robotImages = document.createElement("tr");
        table.appendChild(robotCounts);
        robotCounts = document.createElement("tr");
      }
      robotImages.appendChild(tdRobot);

      let tdCount: HTMLTableCellElement = this.robotTds[teamID][robot];
      robotCounts.appendChild(tdCount);
    }
    table.appendChild(robotImages);
    table.appendChild(robotCounts);

    return table;
  }

  private statsTable(teamIDs: Array<number>): HTMLTableElement {
    const table = document.createElement("table");
    const bars = document.createElement("tr");
    const counts = document.createElement("tr");
    const labels = document.createElement("tr");
    table.id = "stats-table";
    bars.id = "stats-bars";
    table.setAttribute("align", "center");

    // Add the bullet bars and labels
    teamIDs.forEach((id: number) => {
      const bar = document.createElement("td");
      bar.height = "150";
      bar.vAlign = "bottom";
      // TODO: figure out if statbars.get(id) can actually be null??
      // bar.appendChild(this.statBars.get(id)!.bullets.bar);
      bars.appendChild(bar);

      const count = document.createElement("td");
      // TODO: figure out if statbars.get(id) can actually be null??
      // count.appendChild(this.statBars.get(id)!.bullets.label);
      counts.appendChild(count);
    });

    // Add the VP bars and labels
    teamIDs.forEach((id: number) => {
      const bar = document.createElement("td");
      bar.height = "150";
      bar.vAlign = "bottom";
      // TODO: figure out if statbars.get(id) can actually be null??
      // bar.appendChild(this.statBars.get(id)!.vps.bar);
      bars.appendChild(bar);

      const count = document.createElement("td");
      // TODO: figure out if statbars.get(id) can actually be null??
      // count.appendChild(this.statBars.get(id)!.vps.label);
      counts.appendChild(count);
    });

    // Labels - "Bullets" and "Victory Points"
    // const labelBullets = document.createElement("td");
    // labelBullets.colSpan = 2;
    // labelBullets.innerText = "Bullets";
    // const labelVPs = document.createElement("td");
    // labelVPs.colSpan = 2;
    // labelVPs.innerText = "Victory Points";

    table.appendChild(bars);
    table.appendChild(counts);
    table.appendChild(labels);
    // labels.appendChild(labelBullets);
    // labels.appendChild(labelVPs);
    return table;
  }

  /**
   * Clear the current stats bar and reinitialize it with the given teams.
   */
  initializeGame(teamNames: Array<string>, teamIDs: Array<number>){
    // Remove the previous match info
    while (this.div.firstChild) {
      this.div.removeChild(this.div.firstChild);
    }
    this.robotTds = {};
    // this.statBars = new Map<number, { bullets: StatBar, vps: StatBar }>();

    // Add view toggles
    this.div.append(this.addViewOptions());
    
    // Populate with new info
    // Add a section to the stats bar for each team in the match
    for (var index = 0; index < teamIDs.length; index++) {
      // Collect identifying information
      let teamID = teamIDs[index];
      let teamName = teamNames[index];
      let inGameID = index + 1; // teams start at index 1
      console.log("Team: " + inGameID);

      // A div element containing all stats information about this team
      let teamDiv = document.createElement("div");

      // Create td elements for the robot counts and store them in robotTds
      // so we can update these robot counts later; maps robot type to count
      let initialRobotCount: Object = {};
      for (let robot of this.robots) {
        let td: HTMLTableCellElement = document.createElement("td");
        td.innerHTML = "0";
        initialRobotCount[robot] = td;
      }
      this.robotTds[teamID] = initialRobotCount;

      // Create the stat bar for bullets
      // let bullets = document.createElement("div");
      // bullets.className = "stat-bar";
      // bullets.style.backgroundColor = hex[inGameID];
      // let bulletsSpan = document.createElement("span");
      // bulletsSpan.innerHTML = "0";

      // Create the stat bar for victory points
      let vps = document.createElement("div");
      vps.className = "stat-bar";
      vps.style.backgroundColor = hex[inGameID];
      let vpsSpan = document.createElement("span");
      vpsSpan.innerHTML = "0";

      // Store the stat bars
      // this.statBars.set(teamID, {
      //   bullets: {
      //     bar: bullets,
      //     label: bulletsSpan
      //   },
      //   vps: {
      //     bar: vps,
      //     label: vpsSpan
      //   }
      // });

      // Add the team name banner and the robot count table
      teamDiv.appendChild(this.teamHeaderNode(teamName, inGameID));
      teamDiv.appendChild(this.robotTable(teamID, inGameID));
      teamDiv.appendChild(document.createElement("br"));

      this.div.appendChild(teamDiv);
    }

    // Add stats table
    this.statsTableElement.remove();
    this.statsTableElement = this.statsTable(teamIDs);
    this.div.appendChild(this.statsTableElement);
    
    // Add log console
    let consoleDiv = document.createElement("div");
    consoleDiv.append(this.robotConsole);
    this.div.appendChild(consoleDiv);
  }

  addViewOptions(){
    let viewOptionForm = document.createElement("form");
    viewOptionForm.setAttribute("id", "viewoptionformid");

    let waterInp = document.createElement("input");
    let waterLabel = document.createElement("label");
    waterInp.checked = true;
    waterInp.setAttribute("type", "checkbox");
    waterInp.setAttribute("name", "view");
    waterInp.setAttribute("value", "water");
    waterInp.setAttribute("id", "waterid");
    waterLabel.setAttribute("for", "waterid");
    waterLabel.innerHTML = "water";
    viewOptionForm.appendChild(waterInp);
    viewOptionForm.appendChild(waterLabel);

    let dirtInp = document.createElement("input");
    let dirtLabel = document.createElement("label");
    dirtInp.checked = true;
    dirtInp.setAttribute("type", "checkbox");
    dirtInp.setAttribute("name", "view");
    dirtInp.setAttribute("value", "dirt");
    dirtInp.setAttribute("id", "dirtid");
    dirtLabel.setAttribute("for", "dirtid");
    dirtLabel.innerHTML = "dirt";
    viewOptionForm.appendChild(dirtInp);
    viewOptionForm.appendChild(dirtLabel);

    let pollutionInp = document.createElement("input");
    let pollutionLabel = document.createElement("label");
    pollutionInp.checked = true;
    pollutionInp.setAttribute("type", "checkbox");
    pollutionInp.setAttribute("name", "view");
    pollutionInp.setAttribute("value", "pollution");
    pollutionInp.setAttribute("id", "pollutionid");
    pollutionLabel.setAttribute("for", "pollutionid");
    pollutionLabel.innerHTML = "pollution";
    viewOptionForm.appendChild(pollutionInp);
    viewOptionForm.appendChild(pollutionLabel);

    return viewOptionForm;
  }

  /**
   * Change the robot count on the stats bar
   */
  setRobotCount(teamID: number, robotType: schema.BodyType, count: number) {
    let td: HTMLTableCellElement = this.robotTds[teamID][robotType];
    td.innerHTML = String(count);
  }

  /**
   * Change the victory points of the given team
   */
  // setVPs(teamID: number, count: number) {
  //   // TODO: figure out if statbars.get(id) can actually be null??
  //   const statBar: StatBar = this.statBars.get(teamID)!.vps
  //   statBar.label.innerText = String(count);
  //   statBar.bar.style.height = `${100 * count / cst.VICTORY_POINT_THRESH}%`;

  //   if (this.images.star.parentNode === statBar.bar) {
  //     this.images.star.remove();
  //   }

  //   if (count >= cst.VICTORY_POINT_THRESH) {
  //     this.images.star.id = "star";
  //     statBar.bar.appendChild(this.images.star);
  //   }
  // }

  /**
   * Change the bullets of the given team
   */
  // setBullets(teamID: number, count: number) {
  //   // TODO: figure out if statbars.get(id) can actually be null??
  //   const statBar: StatBar = this.statBars.get(teamID)!.bullets;
  //   statBar.label.innerText = String(count.toPrecision(5));
  //   statBar.bar.style.height = `${100 * count / cst.BULLET_THRESH}%`;
  // }
}
