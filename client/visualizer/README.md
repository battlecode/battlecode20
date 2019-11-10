# Battlecode Visualizer 2020 📺

This is the game client for Battlecode 2020, which can run in your web browser or as a standalone application.

If you're a competitor and not interested in *developing* the client, you shouldn't do any of the things this document tells you to do. Proceed at your own risk.

### Developing

To get started:
```sh
$ npm install
```

To watch in a browser:
```sh
$ npm run watch
```

To watch using a standalone app (Electron):
```sh
$ npm run electron
```

To run the tests:
```sh
$ npm test
```
Note: Tests currently *don't run in a browser*; they run in node. They also don't understand webpack. That should change quickly.

All code and assets go in `src`, which is written in Typescript. Note that we're using webpack to bundle everything up; if you want the url of, say, an image, put the image at `src/images/image_file.png`, and then do `require('./images/image_file.png')`, which will return the URL of the image. If you want to reference another typescript file do a [standard typescript ES6 import](https://www.typescriptlang.org/docs/handbook/modules.html).

If you want to add a dependency, run `npm install --save package-name` and then `npm install --save @types/package-name` (for the typescript declarations). If `@types/package-name` doesn't exist, sacrifice a goat, or possibly a grad student.

Also note that this repo doesn't contain all of the client code. See `../playback`; that's the library that actually reads and replays the `.bc20` match files. This repo has everything else; video, sound, controls, etc.

If you've made a change in `../playback` and want to integrate it here, you need to do `npm install` again. It's a bit of a pain; and should probably be integrated in this folder at some point.

### Deploying

To build a release bundle:
```sh
$ npm run prod
```
This will bundle up all of the assets you want in the `bc20` folder. You can then embed the client in any web page you want:

- Mount the assets in `bc20` at `/bc20/` on your webserver.
- `<script src="/bc20/bundle.js"></script>`
- `<script>window.battlecode.mount(document.getElementById('battlecode-div'))</script>`

You now have a copy of the battlecode client running on your web page. See `src/app.ts` and `src/config.ts` to learn about the client's API.