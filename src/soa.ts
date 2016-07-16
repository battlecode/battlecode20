import {flatbuffers} from 'battlecode-schema';

/**
 * A class that wraps a group of typed buffers.
 * Essentially a mini SQL table without the SQL.
 * Useful for storing, say, 100000 entities and updating them quickly.
 * Does not support strings.
 */
export default class StructOfArrays {
  static types = {
    int8: 'int8',
    uint8: 'uint8',
    uint8clamped: 'uint8clamped',
    int16: 'int16',
    uint16: 'uint16',
    int32: 'int32',
    uint32: 'uint32',
    float32: 'float32',
    float64: 'float64'
  }

  private _primary: string;
  private _primLookup: {[key: number]: number};
  private _types: {[field: string]: TypeName};
  private _arrays: {[field: string]: TypedArray};
  private _capacity: number;
  private _length: number;

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
  constructor(fields: {[field: string]: TypeName},
              primary: string,
              capacity?: number) {
    this._length = 0;
    this._capacity = capacity? capacity : DEFAULT_CAPACITY;
    this._arrays = {};
    this._types = {};
    this._primLookup = {};

    for (let field in fields) {
      if (fields.hasOwnProperty(field)) {
        this._arrays[field] = new TYPE_MAPPINGS[fields[field]](this._capacity);
        this._types[field] = fields[field];
      }
    }
  }

  /**
   * Get the length of the entries in the array.
   */
  length(): number {
    return this._length;
  }

  /**
   * Insert a struct into the array.
   * Note: numbers with no corresponding entry will set their
   * corresponding fields to 0.
   */
  insert(numbers: {[field: string]: number}) {
    if (!(this._primary in numbers)) {
      throw new Error('Cannot insert without primary key');
    }
    let primary = numbers[this._primary];
    if (primary in this._primLookup) {
      throw new Error('Primary key already exists');
    }

    this._length++;
    this._resize();

    let index = this._length - 1;
    this._primLookup[primary] = index;
    this._insertAt(index, numbers);
  }

  /**
   * Modify an existing struct in the array.
   */
  alter(numbers: {[field: string]: number}) {
    if (!(this._primary in numbers)) {
      throw new Error(`Cannot alter without primary key: '${this._primary}'`);
    }
    let p = numbers[this._primary];
    if (!(p in this._primLookup)) {
      throw new Error(`Record with primary key does not exist: ${p}`);
    }
    let index = this._primLookup[numbers[this._primary]];
    this._insertAt(index, numbers);
  }

  /**
   * Set at an array index.
   */
  private _insertAt(index: number, numbers: {[field: string]: number}) {
    for (const field in numbers) {
      if (numbers.hasOwnProperty(field) && this._arrays.hasOwnProperty(field)) {
        this._arrays[field][index] = numbers[field];
      }
    }
  }

  /**
   * Insert values in bulk.
   */
  insertBulk(values: {[field: string]: TypedArray}) {
    if (!(this._primary in values)) {
      throw new Error('Cannot insert without primary key');
    }
    const startInsert = this._length;
    this._length += values[this._primary].length;
    this._resize();
    for (let field in values) {
      if (values.hasOwnProperty(field) && this._arrays.hasOwnProperty(field)) {
        this._arrays[field].set(values[field], startInsert);
      }
    }
    const primaries = values[this._primary];
    for (let i = 0; i < primaries.length; i++) {
      this._primLookup[primaries[i]] = startInsert + i;
    }
  }

  /**
   * Alter values in bulk.
   * Rows with nonexistent primary keys will be silently ignored.
   */
  alterBulk(values: {[field: string]: TypedArray}) {
    if (!(this._primary in values)) {
      throw new Error('Cannot insert without primary key');
    }
    for (let field in values) {
      if (values.hasOwnProperty(field) && this._arrays.hasOwnProperty(field) && field != this._primary) {
        this._alterBulkFieldImpl(this._arrays[field], values[this._primary], values[field]);
      }
    }
  }

  /**
   * Let the JIT have a small, well-typed chunk of array code to work with.
   */
  _alterBulkFieldImpl(target: TypedArray, primaries: TypedArray, source: TypedArray) {
    const lookup = this._primLookup;
    const length = primaries.length;
    for (let i = 0; i < length; i++) {
      target[lookup[primaries[i]]] = source[i];
    }
  }

  /**
   * Insert from a flatbuffer.
   * This is a dirty hack, avoid unless you understand what you're doing.
   *
   * Given a flatbuffers.Table, you can look into the _generated.js code to find
   * the offsets of the fields they contain.
   * This method only handles vector table members.
   * From flatbuffer code that looks like:
   *
   *   namespace myns {
   *     table MyTable {
   *       myInts: [int32]
   *     }
   *   }
   * The generated code will look like:
   *
   *   myns.myTable.prototype.myInts = function(index) {
   *     var offset = this.bb.__offset(this.bb_pos, 20);
   *     return offset ? (this.bb.readInt32(this.bb.__vector(this.bb_pos
   *            + offset) + index)) : 0;
   *   };
   *   myns.myTable.prototype.myIntsLength = function() {
   *     var offset = this.bb.__offset(this.bb_pos, 20);
   *     return offset ? this.bb.__vector_len(this.bb_pos + offset) : 0;
   *   };
   *
   * The offset for field 'vectorField' is 20.
   * If you want to store 'vectorField' in a StructOfArrays field called
   * 'soaField', call like so:
   *
   * var soa = new StructOfArrays({ soaField: 'int32' }, 'soaField');
   * soa.insertBulkFlat(table, { soaField: 20 });
   *
   * If you change your flatbuffer in a backwards-incompatible way, YOU HAVE TO
   * UPDATE THE OFFSETS.
   * Otherwise you'll get garbage.
   *
   * If the flatbuffers type does not match the type in the StructOfArrays,
   * you need to supply it as well.
   *
   * var soa = new StructOfArrays({ soaField: 'float64' }, 'soaField');
   * soa.insertBulkFlat(table, { soaField: 20 }, 'int32');
   *
   * Does not support int64, uint64, or struct vectors.
   */
  insertBulkFlat(table: flatbuffers.Table,
                 offsets: {[field: string]: flatbuffers.Offset},
                 flatType?: TypeName) {
    this.insertBulk(this._lookupOffsets(table, offsets, flatType));
  }

  /**
   * Alter from a flatbuffer.
   * @see StructOfArrays.insertBulkFlat
   */
  alterBulkFlat(table: flatbuffers.Table,
                offsets: {[field: string]: flatbuffers.Offset},
                flatType?: TypeName) {
    this.alterBulk(this._lookupOffsets(table, offsets, flatType));
  }

  /**
   * Implementation that turns offsets into TypedArrays.
   */
  private _lookupOffsets(table: flatbuffers.Table,
                         offsets: {[field: string]: flatbuffers.Offset},
                         flatType?: TypeName):
                           {[field: string]: TypedArray} {

    const bb = table.bb;
    const bb_pos = table.bb_pos;

    // Flatbuffers stores things as little endian.
    // If we're not little endian, this won't work.
    // The vast majority of modern systems are little endian, though.
    // TODO(jhgilles): add a fallback
    if (!flatbuffers.isLittleEndian) {
      throw new Error('Wrong endianness, bucko.');
    }
    let results: {[field: string]: TypedArray} = {};
    for (let field in offsets) {
      if (offsets.hasOwnProperty(field) && this._arrays.hasOwnProperty(field)) {
        // Go through the tables's vtable to find the vector object's
        // offset in the table
        let offset = bb.__offset(bb_pos, offsets[field]);
        if (offset) { // not null
          // The length of the vector, in terms of the type contained, NOT bytes
          let length = bb.__vector_len(bb_pos + offset);
          // The start of the vector
          let start = bb.__vector(bb_pos + offset);

          let arrayType = TYPE_MAPPINGS[this._types[field]];
          results[field] = new arrayType(
            // sneak around privacy limitations
            bb['bytes_'].buffer,
            start,
            length
          );
        }
      }
    }
    return results;
  }

  /**
   * Resize internal storage, if needed.
   */
  private _resize() {
    if (this._length > this._capacity) {
      this._capacity = StructOfArrays._capacityForLength(this._length);
      for (const field in this._arrays) {
        if (!this._arrays.hasOwnProperty(field)) { continue; }
        if (this._types[field] === 'string') {
          this._arrays[field].length = this._capacity;
        } else {
          let oldArray = this._arrays[field] as TypedArray;
          let newArray = new TYPE_MAPPINGS[this._types[field]](this._capacity) as TypedArray;
          newArray.set(oldArray);
        }
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
    // test primary key lookup
  }
}

export namespace StructOfArrays {
  /**
   * A callback for when an something happens.
   * Use like so:
   *
   *   let cb = (id, x, y) => console.log('Id '+id+' moved to '+x+','+y);
   *   cb.argumentFields = ['id', 'x', 'y'];
   *
   *   soa.alterBulk({id: ids, x: newXs, y: newYs}, cb);
   *
   * > Id 1 moved to 3,4
   * > Id 2 moved to 1,-21
   * > ...
   */
  interface TypedCallback {
    (...args: number[]);
    argumentFields: string[];
  }

  interface ObjectCallback {
    (arg: {[field: string]: number});
  }

  type Callback = ObjectCallback | TypedCallback;
}

type TypedArray = Int8Array | Uint8Array | Uint8ClampedArray | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array;
type TypeName = 'int8' | 'uint8' | 'uint8clamped' | 'int16' | 'uint16' | 'int32' | 'uint32' | 'float32' | 'float64';

const DEFAULT_CAPACITY = 16;

const TYPE_MAPPINGS = {
  int8: Int8Array,
  uint8: Uint8Array,
  uint8clamped: Uint8ClampedArray,
  int16: Int16Array,
  uint16: Uint16Array,
  int32: Int32Array,
  uint32: Uint32Array,
  float32: Float32Array,
  float64: Float64Array
}

Object.freeze(StructOfArrays.types);
Object.freeze(TYPE_MAPPINGS);
