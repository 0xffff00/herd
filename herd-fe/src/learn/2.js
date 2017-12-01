import { add, add1, multiply } from './1'
import _ from 'lodash'

multiply(3, 4)


function foo (a, b, ...rest) {
  console.log('a = ' + a)
  console.log('b = ' + b)
  console.log(rest)
}

foo(1, 2, 3, 4, 5)
console.log(add1(4, 6))
console.log(add(4, 7))

console.log(_.difference([1, 2, 3], [4, 2]))
