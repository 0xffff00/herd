import Vue from 'vue'
import iView from 'iview'
import App from './App'
import 'iview/dist/styles/iview.css'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
import 'font-awesome/css/font-awesome.min.css'
import router from './router'

// Vue.use(VueResource)
Vue.use(ElementUI)
Vue.config.productionTip = false

/* eslint-disable no-new */
// new Vue({
//   el: '#app',
//   router,
//   template: '<App/>',
//   components: {App},
//   render: h => h(App)
// })
//   .$mount('#app')
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: {App}
})
