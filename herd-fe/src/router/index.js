import Vue from 'vue'
import Router from 'vue-router'
import Hello from '../components/Hello'
import Album2 from '../views/Album2'
import RepoMan from '../views/RepoMan'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/hello',
      name: 'Hello',
      component: Hello
    },
    {
      path: '/album2',
      name: 'Album2',
      component: Album2
    },
    {
      path: '/repo-man',
      name: 'RepoMan',
      component: RepoMan
    }
  ]
})
