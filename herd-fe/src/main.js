import Vue from 'vue'
import App from './App.vue'
import 'iview/dist/styles/iview.css'
import 'font-awesome/css/font-awesome.min.css'
import router from './router'

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
