import Vue from 'vue'
import VueRouter from 'vue-router'
import iView from 'iview'
import Hello from '@/components/Hello'
import Album2 from '@/views/Album2'
import RepoMan from '@/views/RepoMan'
import WordMan from '@/views/WordMan'
import WordEdit from '@/views/WordEdit'
import WordView from '@/views/WordView'
Vue.use(VueRouter)
Vue.use(iView)
const router = new VueRouter({
  mode: 'history',
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
    },
    {
      path: '/word-man',
      name: 'WordMan',
      component: WordMan
    },
    {
      path: '/words/:text/edit',
      name: 'WordEdit',
      component: WordEdit
    },
    {
      path: '/words/:text/view',
      name: 'WordView',
      component: WordView
    }
  ]
})
router.beforeEach((to, from, next) => {
  iView.LoadingBar.start()
  next()
})

router.afterEach(route => {
  iView.LoadingBar.finish()
})

export default router
