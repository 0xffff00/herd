<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <h2>Essential Links</h2>
    <ul>
      <li><a href="https://vuejs.org" target="_blank">Core Docs</a></li>
      <li><a href="https://forum.vuejs.org" target="_blank">Forum</a></li>
      <li><a href="https://chat.vuejs.org" target="_blank">Community Chat</a></li>
      <li><a href="https://twitter.com/vuejs" target="_blank">Twitter</a></li>
      <li><a href="http://vuejs-templates.github.io/webpack/" target="_blank">Docs for This Template</a></li>
    </ul>
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
  </div>
</template>

<script>
  import { multiply } from '../learn/1'
  import appConf from '../../config/sk2/app-conf'
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
        processEnv: process.env
      }
    }
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
