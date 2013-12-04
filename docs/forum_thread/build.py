"""
"""
import os
import subprocess
import tempfile


def main():
    with open('mondocommand.html', 'rb') as fp:
        text = fp.read()

    text = text.replace('\r', '')
    for tag in ('<script>', '</script>', '[LIST]', '[/LIST]'):
        text = text.replace('\n' + tag, tag).replace(tag + '\n', tag)

    text = text.replace('<script>', '[syntax=java]').replace('</script>', '[/syntax]')
    text = text.replace('<h2>', '[size=5][b]').replace('</h2>', '[/b][/size]')
    outpath = os.path.join(tempfile.gettempdir(), 'mondocommand.txt')
    open(outpath, 'wb').write(text)
    subprocess.call(['subl', outpath])


if __name__ == '__main__':
    main()
