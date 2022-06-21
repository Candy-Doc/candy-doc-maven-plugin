const {promisify} = require('util')
const path = require("path");
const readFileAsync = promisify(require('fs').readFile)
const TEMPLATE_DIR = path.join('.github/', 'templates/')
// Given a `const` variable `TEMPLATE_DIR` which points to "<semantic-release-gitmoji>/lib/assets/templates"

// the *.hbs template and partials should be passed as strings of contents
const template = readFileAsync(path.join(TEMPLATE_DIR, 'default-template.hbs'))
const commitTemplate = readFileAsync(
    path.join(TEMPLATE_DIR, 'commit-template.hbs'))

module.exports = {
  branches: [
    'main'
  ],
  plugins: [
    [
      'semantic-release-gitmoji', {
      releaseRules: {
        major: [
          ':boom:',
          ':candy:'
        ],
        minor: [
          ':sparkles:',
          ':zap:',
          ':lipstick:',
          ':recycle:'
        ],
        patch: [
          ':bug:',
          ':arrow_down:',
          ':arrow_up:',
          ':rotating_light:'
        ]
      },
      releaseNotes: {
        template,
        partials: {commitTemplate},
        issueResolution: {
          template: '{baseUrl}/{owner}/{repo}/issues/{ref}',
          baseUrl: 'https://github.com',
          source: 'github.com'
        }
      }
    }
    ],
    '@semantic-release/github',
    '@semantic-release/changelog',
    {
      changelogFile: 'CHANGELOG.md',
      changelogTitle: '# Changelog'
    },
    '@conveyal/maven-semantic-release',
    '@semantic-release/git',
    {
      assets: [
        'CHANGELOG.md',
        'pom.xml'
      ],
      message: ':bookmark: chore(release): ${nextRelease.gitTag} \n\n${nextRelease.notes}'
    }
  ]
}