<template>
  <div>
  <label>{{a1+1}}</label>
  <input :value="a1"/>
  </div>
</template>
<script>
  export default {
    name: 'a1',
    data () {
      return {
        a1: 2333
      }
    }
  }
</script>
