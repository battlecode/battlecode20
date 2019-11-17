#!/usr/bin/env python3
"""
This script releases a new version of the game.

It should probably test things at some point.

This script only works on a Mac, since it will need to codesign the client.
If the codesign flag is set to False, this script should work on other
OSes, although I'm not sure.

The script assumes that you have gradle set up and everything.

Before running this script, you NEED to modify the releases.json file,
by adding a new version number with a changelog description.

Requires click: `pip3 install click`

1. Build the engine jar.
2. Build the client.
3. Upload both to battlecode-dist.
4. Build website with new client and docs.
5. Deploy new website.
6. Deploy new game runner servers.
7. profit
"""

import os
import click




@click.group()
def cli():
    pass

@cli.command()
def install():
    """
    install everything.
    """
    os.chdir('schema')
    os.system('npm install')
    os.chdir('../client/playback')
    os.system('npm install')
    os.chdir('../visualizer')
    os.system('npm install')
    os.chdir('../../')


@cli.command()
def clean():
    os.system('rm -rf temp-dist')


@cli.command()
@click.option('--codesign',default=True,help='whether to codesign.')
@click.option('-v','--release-version',required=True,help='needs to start with 2020.')
def release(release_version, codesign):

    if os.path.isdir('temp-dist'):
        raise click.ClickException('Need to clean first.')

    os.system('mkdir temp-dist')

    build_engine(release_version=release_version)

    build_client(codesign=codesign)

    # push_to_dist()

    # build_website()

    # deploy_website()

    # deploy_servers()

    # clean()

    # profit()

def push_to_dist():
    os.system('cd temp-dist')


def build_engine(release_version):
    """
    Builds engine.
    """
    os.system("./gradlew release -Prelease_version=" + release_version)

    os.system("mv battlecode-" + release_version + '.jar temp-dist/engine.jar')
    os.system("mv battlecode-javadoc-" + release_version + '-javadoc.jar temp-dist/javadoc.jar')

def build_client(codesign=True):
    """
    builds client
    """
    os.chdir('client/visualizer')
    if not codesign:
        os.system('npm run prod-electron-no-sign')
    os.system('npm run prod-electron')
    os.chdir('../../')
    os.system('mv client/visualizer/dist temp-dist/client')


if __name__ == '__main__':

    cli()
