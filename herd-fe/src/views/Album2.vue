<template>
  <el-row>
    <el-col :span="4">
      <el-menu v-for="(e,i) in cates">
        <el-menu-item :index="''+i+1">{{e[0]}}
          ({{e[1]}})
        </el-menu-item>
      </el-menu>
    </el-col>
    <el-col :span="20">
      <div class="grid-content bg-purple-light"></div>
    </el-col>
  </el-row>

</template>
<script>
  import herdService from '../services/HerdService'

  export default {
    name: 'herd-album222',
    data () {
      return {cates: []}
    },
    mounted () {
      this.load()
    },

    methods: {
      getUrlByHash: herdService.getUrlByHash,
      load () {
        console.log('loading')
        herdService.countImageMediasByDate(null, res => {
          this.cates = res
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
