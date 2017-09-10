let a = 3
var b = 4

export { a, b }

export function multiply (x, y) {
  const r = x * y
  console.log(r)
  return r
}

function add (x, y) { return x + y }

export { add, add as add1 }
