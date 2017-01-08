"use strict";
// Map polyfill
var Map = require("core-js/library/es6/map");
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
 * type EntitySchema = {
 *   id: Uint16Array,
 *   x: Float64Array,
 *   y: Float64Array,
 *   size: Float64Array;
 * }
 *
 * let entities = new StructOfArrays<EntitySchema>({
 *   id: new Uint16Array([0, 1, ...]),
 *   x: new Float64Array([100, 300, ...]),
 *   y: new Float64Array([35, 24, ...]),
 *   size: new Float64Array([56, 73, ...]),
 * }, 'id');
 *
 * Note that one field is treated as the 'primary key' (although there aren't
 * actually secondary keys), and is used to uniquely identify objects.
 *
 * Invariants:
 * All data in the arrays is stored from index 0 to index soa.length - 1.
 * Primary keys may not be repeated.
 */
var StructOfArrays = (function () {
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
    function StructOfArrays(fields, primary) {
        if (!hasOwnProperty(fields, primary)) {
            // redundant unless somebody gets cocky
            throw new Error("Primary key must exist, '" + primary + "' not found");
        }
        this.arrays = Object.create(null);
        this._length = fields[primary].length;
        this._capacity = this._length;
        this._fieldNames = new Array();
        this._primLookup = new Map();
        this._primary = primary;
        this._indexBuffer = undefined;
        for (var field in fields) {
            if (hasOwnProperty(fields, field)) {
                var arr = fields[field];
                this.arrays[field] = new arr.constructor(this._capacity);
                this.arrays[field].set(arr.slice(0, this._length));
                this._fieldNames.push(field);
            }
        }
        this._refreshPrimariesLookup(this._length);
    }
    /**
     * Create a copy of this StructOfArrays.
     * Capacity of the copy will be shrunk to this.length.
     */
    StructOfArrays.prototype.copy = function () {
        return new StructOfArrays(this.arrays, this._primary);
    };
    /**
     * Copy source's buffers into ours, overwriting all values.
     * @throws Error if source is missing any of our arrays
     */
    StructOfArrays.prototype.copyFrom = function (source) {
        this._length = source.length;
        if (this._capacity < source.length) {
            this._capacity = source.length;
            for (var field in this.arrays) {
                var oldArray = this.arrays[field];
                var newArray = new oldArray.constructor(this._capacity);
                this.arrays[field] = newArray;
            }
        }
        for (var _i = 0, _a = this._fieldNames; _i < _a.length; _i++) {
            var field = _a[_i];
            if (!(field in source.arrays)) {
                throw new Error("Can't copyFrom, source missing field " + field);
            }
            this.arrays[field].set(source.arrays[field].slice(0, source.length));
            StructOfArrays.fill(this.arrays[field], 0, source.length, this._capacity);
        }
        this._refreshPrimariesLookup(this._length);
    };
    Object.defineProperty(StructOfArrays.prototype, "length", {
        /**
         * Get the length of the entries in the array.
         */
        get: function () {
            return this._length;
        },
        enumerable: true,
        configurable: true
    });
    /**
     * Delete everything.
     */
    StructOfArrays.prototype.clear = function () {
        // zero all arrays
        for (var _i = 0, _a = this._fieldNames; _i < _a.length; _i++) {
            var name_1 = _a[_i];
            var array = this.arrays[name_1];
            StructOfArrays.fill(array, 0, 0, this._capacity);
        }
        // clear key lookup
        this._primLookup.clear();
        // no elements
        this._length = 0;
    };
    /**
     * Insert a struct into the array.
     * Note: numbers with no corresponding entry will set their
     * corresponding fields to 0.
     *
     * @return index of inserted object
     */
    StructOfArrays.prototype.insert = function (numbers) {
        if (!(this._primary in numbers)) {
            throw new Error('Cannot insert without primary key');
        }
        var primary = numbers[this._primary];
        if (this._primLookup.has(primary)) {
            throw new Error('Primary key already exists');
        }
        this._resize(this._length + 1);
        var index = this._length - 1;
        this._primLookup.set(primary, index);
        this._alterAt(index, numbers);
        return index;
    };
    /**
     * Modify an existing struct in the array.
     *
     * @return index of altered object (NOT primary key)
     */
    StructOfArrays.prototype.alter = function (numbers) {
        if (!(this._primary in numbers)) {
            throw new Error("Cannot alter without primary key: '" + this._primary + "'");
        }
        var p = numbers[this._primary];
        if (!this._primLookup.has(p)) {
            throw new Error("Record with primary key does not exist: " + p);
        }
        var index = this._primLookup.get(p);
        this._alterAt(index, numbers);
        return index;
    };
    /**
     * Look up a primary key in the array.
     */
    StructOfArrays.prototype.lookup = function (primary, result) {
        if (result === void 0) { result = Object.create(null); }
        if (!this._primLookup.has(primary)) {
            throw new Error("Record with primary key does not exist: " + primary);
        }
        var i = this._primLookup.get(primary);
        for (var _i = 0, _a = this._fieldNames; _i < _a.length; _i++) {
            var field = _a[_i];
            result[field] = this.arrays[field][i];
        }
        return result;
    };
    /**
     * @return the index of the object with the given primary key,
     * or -1.
     */
    StructOfArrays.prototype.index = function (primary) {
        var index = this._primLookup[primary];
        return index === undefined ? -1 : index;
    };
    /**
     * Set at an array index.
     */
    StructOfArrays.prototype._alterAt = function (index, values) {
        for (var field in values) {
            if (hasOwnProperty(values, field) && field in this.arrays) {
                this.arrays[field][index] = values[field];
            }
        }
    };
    /**
     * Delete at an array index.
     * O(this.length); prefer deleting in bulk.
     */
    StructOfArrays.prototype.delete = function (key) {
        var arr = new this.arrays[this._primary].constructor(1);
        arr[0] = key;
        this.deleteBulk(arr);
    };
    /**
     * Insert values in bulk.
     * O(values[...].length).
     *
     * Values are guaranteed to be inserted in a single block.
     * You can perform extra initialization on this block after insertion.
     *
     * The block is in range [startI, this.length)
     *
     * @return startI
     */
    StructOfArrays.prototype.insertBulk = function (values) {
        if (!hasOwnProperty(values, this._primary)) {
            throw new Error("Cannot insert without primary key: '" + this._primary + "'");
        }
        var primaries = values[this._primary];
        var startInsert = this._length;
        this._resize(this._length + primaries.length);
        var err = false;
        for (var field in values) {
            if (hasOwnProperty(values, field) && field in this.arrays && values[field] != null) {
                this.arrays[field].set(values[field], startInsert);
            }
        }
        for (var i = 0; i < primaries.length; i++) {
            this._primLookup.set(primaries[i], startInsert + i);
        }
        return startInsert;
    };
    /**
     * Alter values in bulk.
     * O(values[...].length).
     * Rows with nonexistent primary keys will be silently ignored.
     */
    StructOfArrays.prototype.alterBulk = function (values) {
        if (!hasOwnProperty(values, this._primary)) {
            throw new Error("Cannot alter without primary key: '" + this._primary + "'");
        }
        var indices = this.lookupIndices(values[this._primary]);
        for (var field in values) {
            if (hasOwnProperty(values, field) && (field in this.arrays)
                && field != this._primary && values[field] != null) {
                this._alterBulkFieldImpl(this.arrays[field], indices, values[field]);
            }
        }
    };
    /**
     * Lookup the indices of a set of primary keys.
     * Returned array may not be the length of primaries; ignore extra entries.
     */
    StructOfArrays.prototype.lookupIndices = function (primaries) {
        if (this._indexBuffer == null || this._indexBuffer.length < primaries.length) {
            this._indexBuffer = new Uint32Array(StructOfArrays._capacityForLength(primaries.length));
        }
        var p = this._primLookup;
        var indexCount = 0;
        for (var i = 0; i < primaries.length; i++) {
            var key = p.get(primaries[i]);
            this._indexBuffer[i] = key === undefined ? -1 : key;
        }
        return this._indexBuffer;
    };
    /**
     * Let the JIT have a small, well-typed chunk of array code to work with.
     */
    StructOfArrays.prototype._alterBulkFieldImpl = function (target, indices, source) {
        for (var i = 0; i < source.length; i++) {
            target[indices[i]] = source[i];
        }
    };
    /**
     * Copy a value into a TypedArray (or normal array, I suppose).
     *
     * Just a polyfill.
     *
     * @param arr the array
     * @param value the value to fill with
     * @param start inclusive
     * @param end exclusive
     */
    StructOfArrays.fill = function (arr, value, start, end) {
        if (arr.fill) {
            arr.fill(value, start, end);
        }
        else {
            for (var i = start; i < end; i++) {
                arr[i] = value;
            }
        }
    };
    /**
     * Create a sorted array of keys to delete.
     * May allocate a new array, or reuse an old one.
     * Supplying nonexistent or repeated keys is not allowed.
     */
    StructOfArrays.prototype._makeToDelete = function (keys) {
        if (this._indexBuffer == undefined || this._indexBuffer.length < keys.length) {
            this._indexBuffer = new Uint32Array(StructOfArrays._capacityForLength(keys.length));
        }
        var indexCount = 0;
        for (var i = 0; i < keys.length; i++) {
            var key = this._primLookup.get(keys[i]);
            if (key === undefined)
                continue;
            this._indexBuffer[indexCount] = key;
            indexCount++;
        }
        var t = this._indexBuffer.subarray(0, indexCount);
        if (Uint32Array.prototype.sort) {
            // note: sort, by default, sorts lexicographically.
            // Javascript!
            t.sort(SENSIBLE_SORT);
        }
        else {
            Array.prototype.sort.call(t, SENSIBLE_SORT);
        }
        return t;
    };
    /**
     * Delete a set of primary keys.
     */
    StructOfArrays.prototype.deleteBulk = function (keys) {
        if (keys.length === 0)
            return;
        // map the keys to indices and sort them
        var toDelete = this._makeToDelete(keys);
        for (var _i = 0, _a = this._fieldNames; _i < _a.length; _i++) {
            var name_2 = _a[_i];
            var array = this.arrays[name_2];
            // copy the fields down in the array
            this._deleteBulkFieldImpl(toDelete, array);
            // zero the new space in the array
            StructOfArrays.fill(array, 0, this._length - toDelete.length, this._length);
        }
        this._length -= toDelete.length;
        this._refreshPrimariesLookup(this._length);
    };
    /**
     * @param toDelete at least one element; sorted ascending
     * @param array the array to modify
     */
    StructOfArrays.prototype._deleteBulkFieldImpl = function (toDelete, array) {
        var length = this._length;
        var off = 1;
        for (var i = toDelete[0] + 1; i < length; i++) {
            if (toDelete[off] === i) {
                off++;
            }
            else {
                array[i - off] = array[i];
            }
        }
    };
    /**
     * Update the indices in the lookup table
     */
    StructOfArrays.prototype._refreshPrimariesLookup = function (newLength) {
        var p = this.arrays[this._primary];
        this._primLookup.clear();
        for (var i = 0; i < newLength; i++) {
            this._primLookup.set(p[i], i);
        }
    };
    /**
     * Resize internal storage, if needed.
     */
    StructOfArrays.prototype._resize = function (newLength) {
        if (newLength > this._capacity) {
            this._capacity = StructOfArrays._capacityForLength(newLength);
            for (var field in this.arrays) {
                var oldArray = this.arrays[field];
                var newArray = new oldArray.constructor(this._capacity);
                newArray.set(oldArray);
                this.arrays[field] = newArray;
            }
        }
        this._length = newLength;
    };
    /**
     * Round up to the nearest power of two.
     */
    StructOfArrays._capacityForLength = function (size) {
        // see http://graphics.stanford.edu/~seander/bithacks.html
        if (size <= 0) {
            return 0;
        }
        // size is a power of two
        if ((size & (size - 1)) === 0) {
            return size;
        }
        // round up to the next power of two
        size--;
        size |= size >> 1;
        size |= size >> 2;
        size |= size >> 4;
        size |= size >> 8;
        size |= size >> 16;
        size++;
        return size;
    };
    /**
     * Check invariants.
     */
    StructOfArrays.prototype.assertValid = function () {
        // test primary key lookup / uniqueness
        var primary = this.arrays[this._primary];
        for (var i = 0; i < this._length; i++) {
            if (this._primLookup.get(primary[i]) !== i) {
                throw new Error("Incorrect: key '" + primary[i] + "', actual index " + i + ", cached index " + this._primLookup.get(primary[i]));
            }
        }
        for (var _i = 0, _a = this._fieldNames; _i < _a.length; _i++) {
            var field = _a[_i];
            if (this.arrays[field].length !== this._capacity) {
                throw new Error("Capacity mismatch: supposed to be " + this._capacity + ", actual " + this.arrays[field].length);
            }
            for (var i = this._length; i < this._capacity; i++) {
                if (this.arrays[field][i] !== 0) {
                    throw new Error("Array not zeroed after length: " + field);
                }
            }
        }
    };
    return StructOfArrays;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = StructOfArrays;
/**
 * The default capacity of our arrays.
 * TODO(jhgilles): tune.
 */
var DEFAULT_CAPACITY = 8;
/**
 * In case the object has deleted hasOwnProperty.
 */
function hasOwnProperty(obj, prop) {
    return Object.prototype.hasOwnProperty.call(obj, prop);
}
/**
 * Javascript, everybody!
 */
var SENSIBLE_SORT = function (a, b) { return a - b; };
