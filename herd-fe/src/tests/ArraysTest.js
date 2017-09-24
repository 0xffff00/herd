import Arrays from '../utils/Arrays'

function test1 () {
  let arr = []
  let comp = (x, y) => x <= y
  let eq = (x, y) => x === y
  console.info(arr)
  Arrays.insertToOrderedArray(arr, 6, comp)
  Arrays.insertToOrderedArray(arr, 2, comp)

  Arrays.insertToOrderedArray(arr, 3, comp)
  Arrays.insertToOrderedArray(arr, 8, comp)
  Arrays.insertToOrderedArray(arr, 1, comp)
  console.info(arr)
  Arrays.insertToOrderedArray(arr, 1, comp, eq)
  Arrays.insertToOrderedArray(arr, 8, comp, eq)
  console.info(arr)
}

test1()
