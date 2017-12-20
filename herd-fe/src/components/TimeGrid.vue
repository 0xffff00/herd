<template>
  <div class="time-grid">
    {{s3}}
    <table class="month-bar" :style="saa">
      <tr>
        <td></td>
      </tr>
    </table>
    <table class="week-bar">
      <tr v-for="y1 in involvedWeeksCount">
        <td v-for="x1 in 7" :style="getStyle(x1 - 1, y1 - 1)">
          <a :title="getItem(x1 - 1, y1 - 1).date+'('+getItem(x1 - 1, y1 - 1).cnt+')'">
          </a>
        </td>
      </tr>
    </table>
  </div>
</template>
<script>
  import _ from 'lodash'
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
        return (moment(a0.date).day() + 6) % 7  // default format: 'YYYY-MM-DD'
      },
      involvedWeeksCount () {
        return Math.ceil((this.offsetInFirstWeek + this.data.length) / 7)
      },
      saa () {
        return `height:${this.involvedWeeksCount * 18}px;`
      },
      s3 () {
        let m1 = _.mapValues(_.groupBy(this.data, x => x.date.slice(0, 7)), x => x.length)
        console.log(m1)
      }
    },
    mounted () {

    },

    methods: {
      getIdx (x, y) {
        let idx = x + y * 7 - this.offsetInFirstWeek
        if (idx < 0 || idx >= this.data.length) {
          return -1
        }
        return idx
      },
      getItem (x, y) {
        let idx = this.getIdx(x, y)
        return idx < 0 ? {} : this.data[idx]
      },
      getStyle (x, y) {
        let v = this.getItem(x, y).cnt
        let c = J_1_9_GREEN(v)
        return `background: rgb(${c.r},${c.g},${c.b});`
      }
    }
  }

  /**
   * mark C=J(x), so J is like a 'J' curve
   * x is number
   * C is color object, like {r: 12, g: 233, b: 48}
   * [1,p] is linear
   * [p,q] is exponential, log2 curve
   * @param C0 color when x=0
   * @param C1 color when x=1
   * @param Cp color when x=p
   * @param Cq color when x=q
   * @param p {number} [1,p] is linear
   * @param q {number} [p,q] is exponential
   */
  const J = (C0, C1, Cp, Cq, p, q) => x => {
    let k = 0
    // bound guarantee
    if (x < 0) x = 0
    if (x > q) x = q
    if (x === 0) return C0
    if (x <= p) {
      k = (x - 1) / (p - 1)
      return midRgb(C1, Cp, k)
    } else {
      k = (Math.log2(x - 1) - Math.log2(p - 1)) / (Math.log2(q - 1) - Math.log2(x - 1))
      console.log(x, k)
      return midRgb(Cp, Cq, k)
    }
  }
  const J_1_9_GREEN = J(
    {r: 233, g: 233, b: 233},
    {r: 32, g: 233, b: 82},
    {r: 45, g: 128, b: 45},
    {r: 48, g: 64, b: 0},
    9, 65
  )
  const midNum = (x, y, k) => x + (y - x) * k
  const midInt = (x, y, k) => Math.floor(x + (y - x) * k)
  const midRgb = (x, y, k) => ({r: midInt(x.r, y.r, k), g: midInt(x.g, y.g, k), b: midInt(x.b, y.b, k)})

</script>
<style scoped>

  table.month-bar {
    float: left;
    width: 30px;
    height: 100%;
    border-spacing: 0px;
    background: blanchedalmond;
  }

  table.month-bar td {

  }

  table.week-bar {
    float: left;
    border-spacing: 1px;
  }

  table.week-bar td {
    width: 17px;
    height: 17px;
    margin: 1px;
    color: red;
  }

  table.week-bar td a {
    display: block;
    width: 100%;
    height: 100%;
  }

</style>
