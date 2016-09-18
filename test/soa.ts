import StructOfArrays from '../src/soa.ts';
import * as test from 'blue-tape';

test('insert and query', (t: test.Test) => {
  const db = new StructOfArrays({
    id: StructOfArrays.types.int32,
    x: StructOfArrays.types.float64,
    y: StructOfArrays.types.float64
  }, 'id');
  db.insert({id: 1, x: 1, y: 1});
  db.insert({id: 2, x: -5565.332, y: 2});
  t.deepEqual(db.lookup(1), {id: 1, x: 1, y: 1});
  t.deepEqual(db.lookup(2), {id: 2, x: -5565.332, y: 2});
  db.assertValid();
  t.end();
});
