<template>
  <div class="time-grid">
    {{data.length}}
    <table class="time-grid-table">
      <tr v-for="y1 in involvedWeeksCount">
        <td v-for="x1 in 7" :style="getStyle(x1 - 1, y1 - 1)">{{getCnt(x1 - 1, y1 - 1)}}</td>
      </tr>
    </table>
  </div>
</template>
<script>
  import moment from 'moment'

  export default {
    name: 'TimeGrid-v1',
    data () {
      return {
        aaa: 1
      }
    },
    props: {
      data: {type: Array},
      groupBy: {type: String}
    },
    computed: {
      offsetInFirstWeek () {
        let a0 = this.data[0]
        return (moment(a0.date, 'YYYY-MM-DD').day() + 6) % 7
      },
      involvedWeeksCount () {
        return Math.ceil((this.offsetInFirstWeek + this.data.length) / 7)
      }
    },
    mounted () {

    },

    methods: {
      getCnt (x, y) {
        var idx = x + y * 7 - this.offsetInFirstWeek
        if (idx < 0 || idx >= this.data.length) {
          return null
        }
        return this.data[idx].cnt
      },
      getStyle (x, y) {
        let v = Math.min(10, this.getCnt(x, y)) // v varies from 0..10
        let color = `rgba(0,232,65,${v / 10})`
        return `background: ${color};`
      }
    }
  }

</script>
<style scoped>
  table.time-grid-table {
    border-spacing: 1px;
  }

  table.time-grid-table td {
    width: 17px;
    height: 17px;
    margin: 1px;
    color: red;
  }

</style>
