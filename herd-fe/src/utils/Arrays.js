import _ from 'lodash'

/**
 *
 * @param arr must not null
 * @param itemToInsert
 * @param comparator (front,back) => always true,
 *  eg. a list inclined by id, comparator === (x,y)=> x.id<=y.id
 * @param identifier eg. (x,y)=> x.id===y.id
 * if not null, will check  existed by identifier
 * if found existed, refused to insert
 * @return item inserted or item existed
 */
function insertToOrderedArray (arr, itemToInsert, comparator, identifier = null) {
  if (arr.length === 0) {
    arr.push(itemToInsert)
    return itemToInsert
  }
  if (identifier) {
    let existed = arr.find(x => identifier(x, itemToInsert))
    if (existed) {
      return existed
    }
  }
  let pos = _.findIndex(arr, y => comparator(itemToInsert, y))
  if (pos === -1) pos = arr.length
  arr.splice(pos, 0, itemToInsert)
  return itemToInsert
}

export default {insertToOrderedArray}
