<template>
  <div class="herd-album">
    <h1>Album 2</h1>
    <div v-for="(m,i) in medias" class="imgbox">

      <img :src="getUrlByHash(m.hash,'1Kq5')">

    </div>
    <el-button @click="load()"></el-button>
  </div>
</template>
<script>
  import herdService from '../services/HerdService'

  export default {
    name: 'herd-album',
    data () {
      return {medias: []}
    },
    mounted () {
      this.load()
    },

    methods: {
      getUrlByHash: herdService.getUrlByHash,
      load () {
        console.log('loading')
        herdService.listMedias({l: 30}, res => {
          this.medias = res
        })
      }

    }
  }

</script>
<style>
  .herd-album {
    color: red;
    background: yellow;
  }

  .imgbox {
    width: 160px;
    height: 160px;
    margin: 0 3px;
    overflow: hidden;
    resize: both;
    display: inline-block;
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
</style>
