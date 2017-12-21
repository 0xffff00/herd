<template>
  <div class="time-grid">
    <table class="month-bar">
      <tr v-for="month in involvedMonths">
        <td :style="cssOfMonthBlock(month)">
          <span v-if="month.endsWith('01')" class="year">{{month.slice(0, 4)}}</span>
          <span v-else class="month">{{month.slice(5, 7)}}</span>
        </td>
      </tr>
    </table>
    <table class="week-bar">
      <tr v-for="y1 in involvedWeeksCount">
        <td v-for="x1 in 7" :style="cssOfDateBlock(x1 - 1, y1 - 1)" :class="[cssOfBorder(x1-1,y1-1)]">
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

  /**
   * code conventions:
   * x: which day of week , 0 - Monday, 7 - Sunday
   * y: which week of TimeGrid
   * idx: the index of vue.data
   *
   */
  export default {
    name: 'TimeGrid',
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
      countMapByMonth () {
        return _.mapValues(_.groupBy(this.data, x => x.date.slice(0, 7)), x => x.length)
      },
      involvedMonths () {
        return _.keys(this.countMapByMonth)
      },
      involvedMonthsCount () {
        return this.countMapByMonth.length
      },
      xOf1stDateInEachMonth () { // y -> x of 1st date in month
        let res = new Array(this.involvedWeeksCount).fill(-1)
        for (let k = 0; k < this.data.length; k++) {
          let d = this.data[k]
          if (d.date.endsWith('01')) {
            let co = this.coord(k)
            res[co.y] = co.x
          }
        }
        return res
      }
    },
    mounted () {

    },

    methods: {
      /**
       * 76 -> {x：6,y:10}
       */
      coord (idx) {
        let k = this.offsetInFirstWeek + idx
        return {x: k % 7, y: parseInt(k / 7)}
      },
      /**
       * （6，10) -> 76
       */
      idx (x, y) {
        let idx = x + y * 7 - this.offsetInFirstWeek
        if (idx < 0 || idx >= this.data.length) {
          return -1
        }
        return idx
      },
      getItem (x, y) {
        let idx = this.idx(x, y)
        return idx < 0 ? {} : this.data[idx]
      },

      cssOfBorder (x, y) {
        let e = this.xOf1stDateInEachMonth[y]
        let res = ''
        if (e !== -1) {
          res = (x >= e) ? 'mon-up' : 'mon-dn'
          if (x === e && e !== 0) res += ' mon-lt'
        }
        return res
      },
      cssOfDateBlock (x, y) {
        let v = this.getItem(x, y).cnt
        let c = J_1_9_GREEN(v)
        return `background: rgb(${c.r},${c.g},${c.b});`
      },
      cssOfMonthBlock (month) {
        let cnt = this.countMapByMonth[month]
        let h = cnt / 7 * 18
        let monNo = parseInt(month.slice(5, 7))
        let bgc = monNo % 2 ? '#efe' : '#dfd'
        return `height:${h}px;background:${bgc}`
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
      k = (Math.log2(x - 1) - Math.log2(p - 1)) / (Math.log2(q - 1) - Math.log2(p - 1))
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
    border-spacing: 0;
  }

  table.month-bar td {
    padding-bottom: 1px;
    background: blanchedalmond;
  }

  table.month-bar td .year {
    color: dimgray;
    font-size: 18px;
  }

  table.month-bar td .month {
    color: gray;
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

  .mon-dn {
    border-bottom: dotted 1px red;
  }

  .mon-up {
    border-top: dotted 1px red;
  }

  .mon-lt {
    border-left: dotted 1px red;
  }

</style>
