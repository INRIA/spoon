import json as json_converter
import os
import re
import requests


def _file_value(settings_filename, expected):
    with open(settings_filename) as settings:
        for line in settings:
            key, value = line.split(':')[:2]
            if key == expected:
                return value
    raise EOFError


def _os_value(key, default_value=None):
    try:
        return os.environ[key]
    except KeyError:
        return default_value


class GitHubRelease(object):
    base_url = 'https://api.github.com/repos/INRIA/spoon'

    def __init__(self, settings_file):
        super(GitHubRelease, self).__init__()
        self.settings_file = settings_file

    def _headers(self):
        return {
            'Authorization': 'Token ' + _file_value(self.settings_file, 'TOKEN')
        }

    def get(self, url):
        response = requests.get(self.base_url + url, headers=self._headers())
        return json_converter.loads(response.text)

    def post(self, url, data=None, json=None, headers=None):
        if not json:
            json = {}
        if not headers:
            headers = self._headers()
        if not data:
            data = {}
        response = requests.post(self.base_url + url, data=data, json=json, headers=headers)
        return json_converter.loads(response.text)

    def delete(self, url):
        requests.delete(self.base_url + url, headers=self._headers())

    def _create(self, tag, name):
        return self.post('/releases', json={
            'tag_name': tag,
            'name': name,
            'body': 'Changelog here.'
        })

    def _upload_asset(self, upload_url, filename):
        self.base_url = upload_url.replace('{?name,label}', '?name={}'.format(os.path.basename(filename)))
        headers = self._headers()
        headers['Content-Type'] = 'application/zip'
        data = open(filename, 'r').read()
        return self.post('', data=data, headers=headers)

    def perform(self, tag, name, assets):
        release = self._create(tag, name)
        for asset in assets:
            self._upload_asset(release['upload_url'], asset[1])


class GforgeRelease(object):
    base_url = 'https://gforge.inria.fr'
    id_spoon = '86'
    group_spoon = '73'
    session = requests.Session()

    def __init__(self, settings_file):
        super(GforgeRelease, self).__init__()
        self.settings_file = settings_file

    def get(self, url, params=None):
        if not params:
            params = {}
        return self.session.get(self.base_url + url, params=params)

    def post(self, url, data=None, files=None):
        if not data:
            data = {}
        if not files:
            files = {}
        return self.session.post(self.base_url + url, data=data, files=files)

    def login(self, return_to='/'):
        self.session = requests.Session()
        url = '/plugins/authbuiltin/post-login.php'
        response = self.get(url)
        form_key = re.search('<input type="hidden" name="form_key" value="(.*)" />', response.text)
        payload = {
            'form_loginname': _file_value(self.settings_file, 'USERNAME'),
            'form_pw': _file_value(self.settings_file, 'PASSWORD_FORGE'),
            'return_to': return_to,
            'form_key': form_key.group(1),
            'login': 'Identification'
        }
        return self.get(url, payload)

    def _create(self, name, asset):
        self.login('/frs/?view=qrs&group_id={}'.format(self.group_spoon))
        url = '/frs/?group_id={}&action=addrelease'.format(self.group_spoon)
        payload = {
            'package_id': self.id_spoon,
            'release_name': re.search('Spoon ([0-9]+.[0-9]+.[0-9]+)', name).group(1),
            'type_id': asset[0],
            'processor_id': '100',
            'release_changes': 'Changelog here.',
            'release_notes': '',
            'preformatted': '1',
            'submit': 'Create release'
        }
        files = {
            'userfile': open(asset[1], 'r')
        }
        return self.post(url, data=payload, files=files)

    def _upload_asset(self, release_id, asset):
        self.login(
            '/frs/?view=editrelease&group_id={}&package_id={}&release_id={}'
                .format(self.group_spoon, self.id_spoon, release_id))
        url = '/frs/?group_id={}&package_id={}&release_id={}&action=addfile' \
            .format(self.group_spoon, self.id_spoon, release_id)
        payload = {
            'type_id': asset[0],
            'processor_id': '100',
            'submit': 'Add This File'
        }
        files = {
            'userfile': open(asset[1], 'r')
        }
        return self.post(url, data=payload, files=files)

    def perform(self, name, assets):
        response = self._create(name, assets[0])
        release_id = re.search('<tr id="releaseid([0-9]+)" class="bgcolor-white ff">', response.text).group(1)
        for asset in assets[1:]:
            self._upload_asset(release_id, asset)


type_ = _os_value('TYPE', 'minor')
settings_file = _os_value('SETTINGS_PATH', '/builds/resources/settings.txt')
release = _os_value('RELEASE')

tag_name = 'spoon-core-{}'.format(release)
name = 'Spoon {}'.format(release)
assets = [
    ('3000', 'target/{}.jar'.format(tag_name)),
    ('5000', 'target/{}-jar-with-dependencies.jar'.format(tag_name)),
    ('8200', 'target/{}-javadoc.jar'.format(tag_name)),
    ('5000', 'target/{}-sources.jar'.format(tag_name))
]
github = GitHubRelease(settings_file)
github.perform(tag_name, name, assets)

gforge = GforgeRelease(settings_file)
gforge.perform(name, assets)
