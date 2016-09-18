import Vec from '../src/vec';
import * as test from 'blue-tape';

test('trigonometry works', (t: test.Test) => {
  let v = new Vec(1, 1);
  t.equal(v.angle(), Math.PI / 4, 'angle');
  let v2 = new Vec(1, -1);
  t.equal(v.angleTo(v2), -Math.PI / 2, 'angleTo');
  t.equal(v.angleTo(v2), -v2.angleTo(v), 'opposite angleTo');
  t.end();
});
