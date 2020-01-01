# HOW TO RELEASE

(This document assumes you are on a Mac. On a Windows machine, you need to figure out how to codesign the client.)

### Preliminaries
- Do what README.md says:
    - Set everything up
    - Install all dependencies
- Get the macOS signing certificate
    - Read https://help.apple.com/xcode/mac/current/#/dev154b28f09.
    - Create a `Developer ID Application` certificate by going to Xcode > Preferences > Accounts > Manage Certificates.


### Release Procedure
- Make sure everything is up to date:
    - `git pull`
- Choose a version as $year.$release (e.g. 2020.1.32.2)
- `python3 prepare_release.py $year $release` (e.g. `python3 prepare_release.py 2020 1.3.0`;
  note that $year and $release are separated by a space, for reasons.
  - Then do the thing it tells you to do.
  - This will update `specs.md` with version and changelog, and `frontend/public/version.txt` and `client/visualizer/config.ts` with version numbers.
  - It will also generate the HTML version of the docs.
- `./gradlew clean`
- `./gradlew test`
- Review the changes, and commit and push.
- RELEASE: `./gradlew publish -Prelease_version=$version` (but actually fill it in)
- Deploy the frontend (as soon as possible after the previous step)
  - `cd frontend`
  - `./deploy.sh build`
  - `cd ..`
- `git tag $version` (but actually fill it in)
- Update the scrim/compile servers
    - TODO
- `git push`
- Go on Discord and wait for things to catch on fire