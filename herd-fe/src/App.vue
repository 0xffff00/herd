<template>
  <div id="layout">
    <Menu mode="horizontal" active-name="1">

      <div class="layout-nav">
        <router-link to="/hello">
          <MenuItem name="hello">
            <Icon type="home"></Icon>
            首页
          </MenuItem>
        </router-link>
        <router-link to="/album2">
          <MenuItem name="1">
            <Icon type="images"></Icon>
            相册
          </MenuItem>
        </router-link>
        <router-link to="/repo-man">
        <MenuItem name="2">
          <Icon type="ios-folder-outline"></Icon>
          仓库管理
        </MenuItem></router-link>
        <Submenu name="3">
          <template slot="title">
            <Icon type="stats-bars"></Icon>
            sub111
          </template>
          <MenuGroup title="使用">
            <MenuItem name="3-1">新增和启动</MenuItem>
            <MenuItem name="3-2">活跃分析</MenuItem>
            <MenuItem name="3-3">时段分析</MenuItem>
          </MenuGroup>
          <MenuGroup title="留存">
            <MenuItem name="3-4">用户留存</MenuItem>
            <MenuItem name="3-5">流失用户</MenuItem>
          </MenuGroup>
        </Submenu>s

      </div>
    </Menu>

    <div>

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
  import hello from './components/Hello'
  import a1 from './components/a1'
  import album from './views/Album2'

  export default {
    name: 'app',
    data () {
      return {
        'active-name': 'hello'
      }
    },
    components: {
      hello, a1, album
    }
  }
</script>

<style scoped>
  .layout {
    font-family: 'Roboto', 'RobotoDraft', 'Avenir', Helvetica, Arial, sans-serif;
    border: 1px solid #d7dde4;
    background: #f5f7f9;
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
    margin: 15px;
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
</style>
