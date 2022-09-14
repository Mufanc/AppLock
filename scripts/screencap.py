import glob
import os
import subprocess
from time import sleep

import cv2
import numpy as np

APPLICATION_ID = 'mufanc.tools.applock'
ADB_PATH = f'{os.environ.get("ANDROID_SDK_ROOT")}/platform-tools/adb'


def adb_shell(*args):
    print(' '.join(args))
    subprocess.call([ADB_PATH, 'shell', *args, '>/dev/null'])
    sleep(1)


def screenshot():
    proc = subprocess.Popen([ADB_PATH, 'shell', 'screencap', '-p'], stdout=subprocess.PIPE)
    return proc.stdout.read()


def command(action, **extras):
    args = ['am', 'broadcast', '-a', f'{APPLICATION_ID}.ACTION_CONTROL', '-e', 'command', action]

    for key in extras:
        args += ['-e', key, str(extras[key])]

    adb_shell(*args)


def main():
    adb_shell('am', 'broadcast', '-a', 'com.android.systemui.demo', '-e', 'command', 'enter')

    for i, color in enumerate(('PYRO', 'HYDRO', 'ANEMO', 'ELECTRO', 'DENDRO', 'CRYO', 'GEO')):
        command('theme', color=color)
        adb_shell('am', 'force-stop', APPLICATION_ID)
        adb_shell('am', 'start', '-n', f'{APPLICATION_ID}/.ui.MainActivity', '--activity-no-animation')
        images = []

        for page in range(3):
            command('navigate', page=page)
            sleep(5)
            images.append(cv2.imdecode(np.asarray(bytearray(screenshot()), dtype=np.uint8), cv2.IMREAD_COLOR))

        cv2.imwrite(f'AppLock-{i}.png', cv2.hconcat(images))

    adb_shell('am', 'broadcast', '-a', 'com.android.systemui.demo', '-e', 'command', 'exit')
    subprocess.call([
        'ffmpeg',
        '-r', '1', '-i', 'AppLock-%d.png',
        '-vf', 'split[A][B];[A]palettegen=max_colors=256[P];[B][P]paletteuse=dither=bayer:bayer_scale=3', '../images/screenshots.gif',
        '-y'
    ])
    [os.remove(im) for im in glob.glob('AppLock-*.png')]


if __name__ == '__main__':
    main()
