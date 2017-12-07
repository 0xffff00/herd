import Urls from '../utils/Urls'

console.log(Urls.buildQueryUrl('/relations2/x1/', {rel: 'aaa', 'dst': 'addd'}))
console.log(Urls.buildQueryUrl('/relations2/x1/?batch=1', {rel: 'aaa', 'dst': 'addd'}))
