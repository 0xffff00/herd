import Urls from '../utils/Urls'

console.log(Urls.appendQParams('/relations2/x1/', {rel: 'aaa', 'dst': 'addd'}))
console.log(Urls.appendQParams('/relations2/x1/?batch=1', {rel: 'aaa', 'dst': 'addd'}))
