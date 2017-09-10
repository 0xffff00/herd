<template>
  <div id="app2">
    <!--<router-view></router-view>-->
    <a1></a1>
    <album></album>
  </div>
</template>

<script>
  import hello from './components/Hello'
  import a1 from './components/a1'
  import album from './views/Album'

  export default {
    name: 'app',
    components: {
      hello, a1, album
    }
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
  }
</style>
