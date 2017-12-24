<template>
  <div id="layout">
    <Menu mode="horizontal" :active-name="activeName" @on-select="selectMenu">

      <div class="layout-nav">
        <router-link to="/hello">
          <MenuItem name="/hello">
            <Icon type="home"></Icon>
            首页
          </MenuItem>
        </router-link>
        <router-link to="/album">
          <MenuItem name="/album">
            <Icon type="images"></Icon>
            相册
          </MenuItem>
        </router-link>
        <router-link to="/repo-man">
          <MenuItem name="/repo-man">
            <Icon type="ios-folder-outline"></Icon>
            仓库管理
          </MenuItem>
        </router-link>

        <router-link to="/word-man">
          <MenuItem name="/word-man">
            <Icon type="ios-folder-outline"></Icon>
            词汇管理
          </MenuItem>
        </router-link>

        <Submenu name="3">
          <template slot="title">
            <Icon type="stats-bars"></Icon>
            DEV-ONLY
          </template>
          <MenuGroup title="words">
            <router-link to="/words/复旦大学/edit">
              <MenuItem name="/word-man22">/words/复旦大学/edit</MenuItem>
            </router-link>
            <router-link to="/words/复旦大学/edit">
              <MenuItem name="/word-man22">/words/复旦大学/edit</MenuItem>
            </router-link>
            <MenuItem name="3-3">时段分析</MenuItem>
          </MenuGroup>
          <MenuGroup title="留存">
            <MenuItem name="3-4">用户留存</MenuItem>
            <MenuItem name="3-5">流失用户</MenuItem>
          </MenuGroup>
        </Submenu>
      </div>
    </Menu>

    <div style="margin-top: 60px;">

    </div>
    <div class="layout-breadcrumb" style="display: none;">
      <Breadcrumb>
        <BreadcrumbItem href="#">首页</BreadcrumbItem>
        <BreadcrumbItem href="#">应用中心</BreadcrumbItem>
        <BreadcrumbItem>某应用</BreadcrumbItem>
      </Breadcrumb>
    </div>
    <div class="layout-content">
      <router-view></router-view>
    </div>
    <div class="layout-copy">
      2017-2018 &copy; threebody.party
    </div>
  </div>


</template>

<script>
  import hello from './views/Hello'
  import album from './views/Album'
  import Urls from './utils/Urls'

  export default {
    name: 'app',
    data () {
      return {
        activeName: 'home'
      }
    },
    methods: {
      selectMenu (name) {
        console.log(name)
      }
    },
    created () {
      let path = Urls.getCurrentUrlPathname()
      this.activeName = path
    },
    components: {hello, album}
  }

</script>

<style scoped>
  #layout {
    /*font-family: 'Roboto', 'RobotoDraft', 'Avenir', Helvetica, Arial, sans-serif;*/
    /*border: 1px solid #d7dde4;*/
    /*background: #f5f7f9;*/
    background: #f5f7f9;
    position: relative;
    overflow: hidden;
  }

  .layout-logo {
    width: 100px;
    height: 30px;
    background: #5b6270;
    border-radius: 3px;
    float: left;
    position: relative;
    top: 15px;
    left: 20px;
  }

  .layout-nav {
    margin: 0 auto;
  }

  .layout-nav a {
    color: inherit;
  }

  .layout-assistant {
    width: 300px;
    margin: 0 auto;
    height: inherit;
  }

  .layout-breadcrumb {
    padding: 10px 15px 0;
  }

  .layout-content {
    min-height: 200px;
    margin: 5px;
    overflow: hidden;
    background: #fff;
    border-radius: 4px;
  }

  .layout-content-main {
    padding: 10px;
  }

  .layout-copy {
    text-align: center;
    padding: 10px 0 20px;
    color: #9ea7b4;
  }

  .ivu-menu{
    position: fixed;
    width: 100%;
  }
</style>
