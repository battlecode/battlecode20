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
- Make sure you have the correct `frontend/public/access.txt`. Ask someone.
- Add `BC20_GITUSERNAME` and `BC20_GITKEY` as environment variables.

TODO: deploy BACKEND??????
### Release Procedure
- Make sure everything is up to date:
    - `git pull`
- Make sure everything is installed:
    - `./install_all.sh`
- Choose a version as $year.$release (e.g. 2020.1.32.2)
- Run `./pre_release.py $version`.
    - It will generate a comparison link where you can view the changes since the last version.
    - It will update `specs/specs.md` with version and changelog
    - It will update `gradle.properties` with version
    - It will update `client/visualizer/src/config.ts` with version
    - If new maps have been added, run `./pre_release.py $version --maps True`, and make sure to have the names of the maps copied to your clipboard.
        - This will update `SERVER_MAPS` in `client/visualizer/src/constants.ts` and in `backend/settings.py` and copy the maps over from the internal scaffold.
    - If a new tournament has been released, add the flag `--tournament SPRINT`. (Options are `INTL_QUALIFYING`, `US_QUALIFYING`, `HS`, `NEWBIE`, `FINAL`.)
        - This will update `MapFilter.types` in `client/visualizer/src/game/sidebar/mapfilter.ts`.
- Review the changes, and commit and push (message e.g. "preparing for release 2020.1.32.2").
- `./gradlew clean`
- `./gradlew test`
- `./gradlew headless` some good bots
- Review the changes.
- RELEASE: `./gradlew publish`
- `./post_release.py`
  - It will also generate the HTML version of the docs.
  - It will also build the client for the web.
- Review the changes.
- Deploy the frontend (as soon as possible after the previous step)
  - `cd frontend`
  - `./deploy.sh deploy`
  - `cd ..`
- Commit and push (message e.g. "release 2020.1.32.2").
- `git tag $version` (but actually fill it in)
- `git push --tags`
- Update `battlecode20-scaffold/version.txt` WITHOUT A NEWLINE!!! (don't update on github, don't use normal text editor, be super careful)
- Go on Discord and wait for things to catch on fire
