# HOW TO RELEASE

(This document assumes you are on a Mac. On a Windows machine, you need to figure out how to codesign the client.)

### Preliminaries
- Do what README.md says:
    - Set everything up
    - Install all dependencies
- Get the macOS signing certificate
    - Read https://help.apple.com/xcode/mac/current/#/dev154b28f09.
    - Create a `Developer ID Application` certificate by going to Xcode > Preferences > Accounts > Manage Certificates.
    - The signing procedure below will automatically find the certificate in your keychain.
- Install `pandoc` (e.g. using Homebrew)


### Release Procedure
- Make sure everything is up to date:
    - `git pull`
- Make sure everything is installed:
    - `./install_all.sh`
- Choose a version as $year.$release (e.g. 2020.1.32.2)
- Update `specs/specs.md` with the new version and a changelog.
- If new maps have been added, update `SERVER_MAPS` in `client/visualizer/constants.ts`.
  - (optional) If a new tournament has been released, also update `MapFilter.types` in `client/visualizer/game/sidebar/mapfilter.ts`.
- Review the changes, and commit and push (message e.g. "preparing for release 2020.1.32.2").
- `./gradlew clean`
- `./gradlew test`
- `python3 prepare_release.py $version` (but actually fill it in)
  - This will update `client/visualizer/config.ts` with the version number.
  - It will also generate the HTML version of the docs.
  - It will also build the client for the web.
- Review the changes.
- RELEASE: `./gradlew publish -Prelease_version=$version` (but actually fill it in)
- Deploy the frontend (as soon as possible after the previous step)
  - `cd frontend`
  - `./deploy.sh deploy`
  - `cd ..`
- Commit and push (message e.g. "release 2020.1.32.2").
- `git tag $version` (but actually fill it in)
- Update the scrim/compile servers
    - TODO
- `git push`
- Go on Discord and wait for things to catch on fire
