# battlecode-playback 📼

## Overview
  Analyze battlecode match files programmatically.
  This is written in Typescript and imports `battlecode-schema` in `/schema`. Visualizer in `../visualizer` will import this `battlecode-playback` package.


## Contributing
Before you commit, **please run** `npm run build` and commit the changes in the out/ directory. 
This is a slightly painful fact due to typescript, sorry.

### Structure
  * `src/*.ts` : main source files. `index.ts` is the starting point
  * `src/tsconfig.json` : TypeScript compile option configuration file
  * `src/bench/*.ts` : code for making a dummy playback file.
  * `src/test/*.ts` : code for testing `src/bench/*.ts` and `src/soa.ts`.
  * `out/**` : output directory for compiled TypeScript files
  

### Note
  * There is `--declaration` option in `tsconfig.json`, and therefore declaration files (`*.d.ts` files) are also generated.
  * You can try to lint this code `npm run lint`. However, I never tried it, so I don't recommend it yet.
  * The `analyze.js` below will be in source soon.
  * Commented lines are mostly explanations and legacies. Try to leave legacies somehow, so that it can be used later. (Searching commit list might be a bad idea)

---

### Quick sample (legacy):
Install [node](nodejs.org).

```sh
$ npm init
$ npm i --save battlecode-playback
$ $EDITOR analyze.js
```

`analyze.js`:

```js
const bc = require('battlecode-playback');

let aliveRobots = {};
let lifetimes = [];

bc.stream('match.bc20').on('spawn', spawnEvent => {
  aliveRobots[spawnEvent.bodyId] = {
    born: spawnEvent.round
  };
}).on('death', deathEvent => {
  let lifetime = death.round - aliveRobots[deathEvent.bodyId].born;
  delete aliveRobots[deathEvent.bodyId];
}).on('close', () => {
  console.log('Average robot life length: ' +
    (lifetimes.reduce((a,b) => a+b, 0) / lifetimes.length));
});
```
