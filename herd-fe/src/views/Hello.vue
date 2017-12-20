<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <i-button>aaaa</i-button>
    <h2>CONFIG</h2>
    <table>
      <tr>
        <td>appConf</td>
        <td>{{appConf}}</td>
      </tr>

      <tr>
        <td>processEnv</td>
        <td>{{processEnv}}</td>
      </tr>
    </table>
    <TimeGrid groupBy="aa21" :data="demo.countsByDate">
    </TimeGrid>
  </div>
</template>

<script>
  import TimeGrid from '../components/TimeGrid.vue'
  import { multiply } from '../learn/1'
  import appConf from '../../config/sk2/app-conf'
  import moment from 'moment'

  function m1 (x, y) {
    return x * y
  }

  console.log(JSON.stringify(process.env))
  console.log(JSON.stringify(appConf))
  export default {
    name: 'hello',
    data () {
      return {
        msg: 'Welcome to ' + m1(2, 4) + multiply(6, 4),
        appConf: appConf,
        processEnv: process.env,
        demo: {
          countsByDate: []
        }
      }
    },
    created () {
      for (let i = 0; i < 100; i++) {
        let d = moment().add(i - 100, 'd').format('YYYY-MM-DD')
        this.demo.countsByDate.push({date: d, cnt: i - 5})
      }
      for (let i = 0; i < 800; i++) {
        let d = moment().add(i, 'd').format('YYYY-MM-DD')
        let p = Math.random()
        let v = 0
        if (p > 0.9) {
          v = (Math.random() * 70).toFixed() + 20
        } else if (p > 0.3) {
          v = (Math.random() * 16).toFixed()
        }
        this.demo.countsByDate.push({date: d, cnt: v})
      }
    },
    components: {TimeGrid}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  h1, h2 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }

  table {

  }
</style>
