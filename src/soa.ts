// Map polyfill
import * as Map from 'core-js/library/es6/map';

/**
 * A class that wraps a group of typed buffers.
 *
 * Say you want to store a bunch of game entities. You could store them like this:
 * let entities = [
 *   {id: 0, x: 100, y: 35, size: 56},
 *   {id: 1, x: 300, y: 24, size: 73},
 *   ...
 * ];
 * However, this creates memory overhead, gc pressure, and spreads your objects
 * all through memory.
 *
 * Instead, you can store them like this:
 * let entities = {
 *   id: new Uint16Array([0, 1, ...]),
 *   x: new Float64Array([100, 300, ...]),
 *   y: new Float64Array([35, 24, ...]),
 *   size: new Float64Array([56, 73, ...]),
 * };
 *
 * This is more space-efficient, iteration is fast, and if you're working with
 * an API that takes typed array views (say, webgl), you can pass your field
 * arrays in directly.
 *
 * It is more awkward to use, though. This class makes it easier.
 * let entities = new StructOfArrays({
 *   id: Uint16Array,
 *   x: Float64Array,
 *   y: Float64Array,
 *   size: Float64Array
 * }, 'id');
 * entities.insertBulk({
 *   id: new Uint16Array([0, 1, ...]),
 *   x: new Float64Array([100, 300, ...]),
 *   y: new Float64Array([35, 24, ...]),
 *   size: new Float64Array([56, 73, ...]),
 * });
 *
 * Note that one field is treated as the 'primary key' (although there aren't
 * actually secondary keys), and is used to uniquely identify objects.
 *
 * Invariants:
 * All data in the arrays is stored from index 0 to index soa.length - 1.
 * Primary keys may not be repeated.
 */
export default class StructOfArrays {
  /**
   * The actual storage.
   * You can access this, but you have to be careful not to break any
   * invariants.
   * In particular, you can't trust the length field of these TypedArrays;
   * you have to use soa.length.
   */
  arrays: {[id: string]: TypedArray};

  /**
   * The actual length of all arrays.
   * Arrays are resized asymptotically to power-of-two lengths
   * as we resize.
   */
  private _capacity: number;

  /**
   * The logical length of the container.
   */
  private _length: number;

  /**
   * The names of our fields.
   */
  private _fieldNames: string[];

  /**
   * The lookup table for our primary key. Maps keys to indices.
   * We attempt to use an ES6 Map for this, since it won't stringify
   * keys all the time.
   */
  private _primLookup: Map<number, number>;

  /**
   * The name of our primary key.
   */
  private _primary: string;

  // Cache fields
  // (Not needed, just to avoid allocating)

  /**
   * A sorted list of indices to delete.
   */
  private _toDelete: Uint32Array;

  /**
   * Cached views into our arrays.
  private _views:

  /**
   * Use like:
   * const db = new StructOfArrays({
   *   id: StructOfArrays.types.int32,
   *   x: StructOfArrays.types.float64,
   *   y: StructOfArrays.types.float64
   * }, 'id')
   *
   * @param fields the names and types of fields in the SOA
   * @param primary the primary key of the SOA
   * @param capacity the initial capacity of the SOA
   */
  constructor(fields: {[field: string]: TypeSelector},
              primary: string,
              capacity: number = 8) {
    if (!fields.hasOwnProperty(primary)) {
      throw new Error(`Primary key must exist, '${primary}' not found`);
    }

    this.arrays = Object.create(null);
    this._capacity = capacity? capacity : DEFAULT_CAPACITY;
    this._fieldNames = [];
    this._length = 0;
    this._primLookup = new Map<number, number>();
    this._primary = primary;
    this._toDelete = null;

    for (const field in fields) {
      if (fields.hasOwnProperty(field)) {
        this.arrays[field] = new fields[field](this._capacity);
        this._fieldNames.push(field);
      }
    }
  }

  /**
   * Create a copy of this StructOfArrays.
   * Capacity of the copy will be shrunk to this.length.
   */
  copy(): StructOfArrays {
    const types = Object.create(null);
    for (const field of this._fieldNames) {
      types[field] = this.arrays[field].constructor;
    }
    const result = new StructOfArrays(types, this._primary, this._length);
    const toInsert: {[field: string]: TypedArray} = Object.create(null);
    for (const field of this._fieldNames) {
      toInsert[field] = new (this.arrays[field].constructor as TypeSelector)(this.arrays[field].buffer, 0, this._length);
    }
    result.insertBulk(toInsert);
    return result;
  }

  /**
   * Get the length of the entries in the array.
   */
  get length(): number {
    return this._length;
  }

  /**
   * Insert a struct into the array.
   * Note: numbers with no corresponding entry will set their
   * corresponding fields to 0.
   *
   * @return index of inserted object
   */
  insert(numbers: {[field: string]: number}): number {
    if (!(this._primary in numbers)) {
      throw new Error('Cannot insert without primary key');
    }
    const primary = numbers[this._primary];
    if (primary in this._primLookup) {
      throw new Error('Primary key already exists');
    }

    this._length++;
    this._resize();

    const index = this._length - 1;
    this._primLookup.set(primary, index);
    this._alterAt(index, numbers);
    return index;
  }

  /**
   * Modify an existing struct in the array.
   *
   * @return index of altered object (NOT primary key)
   */
  alter(numbers: {[field: string]: number}): number {
    if (!(this._primary in numbers)) {
      throw new Error(`Cannot alter without primary key: '${this._primary}'`);
    }
    const p = numbers[this._primary];
    if (!this._primLookup.has(p)) {
      throw new Error(`Record with primary key does not exist: ${p}`);
    }
    const index = this._primLookup.get(numbers[this._primary]);
    this._alterAt(index, numbers);
    return index;
  }

  /**
   * Look up a primary key in the array.
   */
  lookup(primary: number, result: {[field: string]: number}=Object.create(null)):
      {[field: string]: number} {
    if (!this._primLookup.has(primary)) {
      throw new Error(`Record with primary key does not exist: ${primary}`);
    }
    const i = this._primLookup.get(primary);
    for (const field of this._fieldNames) {
      result[field] = this.arrays[field][i];
    }
    return result;
  }

  /**
   * @return the index of the object with the given primary key,
   * or -1.
   */
  index(primary: number): number {
    const index = this._primLookup[primary];

    return index === undefined? -1 : index;
  }

  /**
   * Set at an array index.
   */
  private _alterAt(index: number, values: {[field: string]: number}) {
    for (const field in values) {
      if (values.hasOwnProperty(field) && field in this.arrays) {
        this.arrays[field][index] = values[field];
      }
    }
  }

  /**
   * Delete at an array index.
   * O(this.length); prefer deleting in bulk.
   */
  delete(key: number) {
    const arr = new (this.arrays[this._primary].constructor as TypeSelector)(1);
    arr[0] = key;
    this.deleteBulk(arr);
  }

  /**
   * Insert values in bulk.
   * O(values[...].length).
   *
   * Values will be inserted in a contiguous chunk.
   * @return index of first inserted object in chunk.
   */
  insertBulk(values: {[field: string]: TypedArray}): number {
    if (!values.hasOwnProperty(this._primary)) {
      throw new Error(`Cannot insert without primary key: '${this._primary}'`);
    }
    const startInsert = this._length;
    this._length += values[this._primary].length;
    this._resize();
    for (const field in values) {
      if (values.hasOwnProperty(field) && field in this.arrays) {
        this.arrays[field].set(values[field], startInsert);
      }
    }
    const primaries = values[this._primary];
    for (let i = 0; i < primaries.length; i++) {
      this._primLookup.set(primaries[i], startInsert + i);
    }
    return startInsert;
  }

  /**
   * Alter values in bulk.
   * O(values[...].length).
   * Rows with nonexistent primary keys will be silently ignored.
   */
  alterBulk(values: {[field: string]: TypedArray}) {
    if (!values.hasOwnProperty(this._primary)) {
      throw new Error(`Cannot alter without primary key: '${this._primary}'`);
    }
    for (const field in values) {
      if (values.hasOwnProperty(field) && (field in this.arrays) && field != this._primary) {
        this._alterBulkFieldImpl(this.arrays[field], values[this._primary], values[field]);
      }
    }
  }

  /**
   * Let the JIT have a small, well-typed chunk of array code to work with.
   */
  private _alterBulkFieldImpl(target: TypedArray, primaries: TypedArray, source: TypedArray) {
    const lookup = this._primLookup;
    const length = primaries.length;
    for (let i = 0; i < length; i++) {
      target[lookup.get(primaries[i])] = source[i];
    }
  }

  /**
   * Zero a TypedArray (or normal array, I suppose)
   * @param start inclusive
   * @param end exclusive
   */
  private static _zero(arr: TypedArray, start: number, end: number) {
    if (arr.fill) {
      arr.fill(0, start, end);
    } else {
      for (let i = start; i < end; i++) {
        arr[i] = 0;
      }
    }
  }

  /**
   * Create a sorted array of keys to delete.
   * May allocate a new array, or reuse an old one.
   * Supplying nonexistent or repeated keys is not allowed.
   */
  private _makeToDelete(keys: TypedArray): TypedArray {
    if (this._toDelete === null || this._toDelete.length < keys.length) {
      this._toDelete = new Uint32Array(StructOfArrays._capacityForLength(keys.length));
    }
    let t = this._toDelete;
    for (let i = 0; i < keys.length; i++) {
      t[i] = this._primLookup.get(keys[i]);
    }

    if (Uint32Array.prototype.sort) {
      // note: sort, by default, sorts lexicographically.
      // Javascript!
      t.sort((a, b) => a - b);
    } else {
      Array.prototype.sort.call(t, (a, b) => a - b);
    }
    return t;
  }

  /**
   * Delete a set of primary keys.
   *
   * Note: this is the only thing that might be slower than just using objects.
   * TODO benchmark.
   */
  deleteBulk(keys: TypedArray) {
    if (keys.length === 0) return;

    // map the keys to indices and sort them
    const deleteIndices = this._makeToDelete(keys);
    for (const name of this._fieldNames) {
      const array = this.arrays[name];
      // copy the fields down in the array
      this._deleteBulkFieldImpl(deleteIndices, array);
      // zero the new space in the array
      StructOfArrays._zero(array, this._length - keys.length, this._length);
    }
    // update _primLookup
    this._removePrimariesLookup(keys)
    this._refreshPrimariesLookup();
    this._length -= deleteIndices.length;
  }

  /**
   * @param toDelete at least one element; sorted ascending
   * @param array the array to modify
   */
  private _deleteBulkFieldImpl(toDelete: Uint32Array, array: TypedArray) {
    const n = this._length;
    for (let i = toDelete[0], offset = 0; i < n; i++) {
      if (toDelete[offset] === i) {
        offset++;
      }
      array[i] = array[i + offset];
    }
  }

  /**
   * Remove primary keys from lookup table
   */
  private _removePrimariesLookup(keys: TypedArray) {
    const p = this._primLookup;
    for (let i = 0; i < keys.length; i++) {
      p.delete(keys[i]);
    }
  }

  /**
   * Update the indices in the lookup table
   */
  private _refreshPrimariesLookup() {
    const l = this._primLookup, p = this.arrays[this._primary];
    const length = this._length;
    for (let i = 0; i < length; i++) {
      l.set(p[i], i);
    }
  }

  /**
   * Resize internal storage, if needed.
   */
  private _resize() {
    if (this._length > this._capacity) {
      this._capacity = StructOfArrays._capacityForLength(this._length);
      for (const field in this.arrays) {
        if (!(field in this.arrays)) { continue; }
        const oldArray = this.arrays[field];
        const newArray = new (oldArray.constructor as TypeSelector)(this._capacity);
        newArray.set(oldArray);
      }
    }
  }

  /**
   * Round up to the nearest power of two.
   */
  private static _capacityForLength(size: number): number {
    if ((Math.log(size) / Math.LN2) % 1 === 0) {
      return size;
    }
    return Math.pow(2, Math.floor(Math.log(size) / Math.LN2) + 1);
  }

  /**
   * Check invariants.
   */
  assertValid() {
    // test primary key lookup / uniqueness
    const primary = this.arrays[this._primary];
    for (let i = 0; i < this._length; i++) {
      if (this._primLookup.get(primary[i]) !== i) {
        throw new Error(`Incorrect: key '${primary[i]}', actual index ${i}, cached index ${this._primLookup.get(primary[i])}`);
      }
    }
    for (const field of this._fieldNames) {
      if (this.arrays[field].length !== this._capacity) {
        throw new Error(`Capacity mismatch: supposed to be ${this._capacity}, actual ${this.arrays[field].length}`);
      }
      for (let i = this._length; i < this._capacity; i++) {
        if (this.arrays[field][i] !== 0) {
          throw new Error(`Array not zeroed after length: ${field}`);
        }
      }
    }
  }
}

export type TypedArray = Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array;

/**
 * A constructor for a TypedArray.
 */
export type TypeSelector = new (...args: any[]) => TypedArray;

const DEFAULT_CAPACITY = 16;
