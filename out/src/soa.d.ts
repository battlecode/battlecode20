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
    readonly arrays: {
        [id: string]: TypedArray;
    };
    /**
     * The actual length of all arrays.
     * Arrays are resized asymptotically to power-of-two lengths
     * as we resize.
     */
    private _capacity;
    /**
     * The logical length of the container.
     */
    private _length;
    /**
     * The names of our fields.
     */
    private readonly _fieldNames;
    /**
     * The lookup table for our primary key. Maps keys to indices.
     * We attempt to use an ES6 Map for this, since it won't stringify
     * keys all the time.
     */
    private readonly _primLookup;
    /**
     * The name of our primary key.
     */
    private readonly _primary;
    /**
     * An array we use to store intermediate indices generated while working.
     * May be null.
     */
    private _indexBuffer?;
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
    constructor(fields: {
        [field: string]: TypeSelector;
    }, primary: string, capacity?: number);
    /**
     * Create a copy of this StructOfArrays.
     * Capacity of the copy will be shrunk to this.length.
     */
    copy(): StructOfArrays;
    /**
     * Copy source's buffers into ours, overwriting all values.
     * @throws Error if source is missing any of our arrays
     */
    copyFrom(source: StructOfArrays): void;
    /**
     * Get the length of the entries in the array.
     */
    readonly length: number;
    /**
     * Insert a struct into the array.
     * Note: numbers with no corresponding entry will set their
     * corresponding fields to 0.
     *
     * @return index of inserted object
     */
    insert(numbers: {
        [field: string]: number;
    }): number;
    /**
     * Modify an existing struct in the array.
     *
     * @return index of altered object (NOT primary key)
     */
    alter(numbers: {
        [field: string]: number;
    }): number;
    /**
     * Look up a primary key in the array.
     */
    lookup(primary: number, result?: {
        [field: string]: number;
    }): {
        [field: string]: number;
    };
    /**
     * @return the index of the object with the given primary key,
     * or -1.
     */
    index(primary: number): number;
    /**
     * Set at an array index.
     */
    private _alterAt(index, values);
    /**
     * Delete at an array index.
     * O(this.length); prefer deleting in bulk.
     */
    delete(key: number): void;
    /**
     * Insert values in bulk.
     * O(values[...].length).
     *
     * Values will be inserted in a contiguous chunk.
     * @return index of first inserted object in chunk.
     */
    insertBulk(values: {
        [field: string]: TypedArray;
    }): number;
    /**
     * Alter values in bulk.
     * O(values[...].length).
     * Rows with nonexistent primary keys will be silently ignored.
     */
    alterBulk(values: {
        [field: string]: TypedArray;
    }): void;
    /**
     * Lookup the indices of a set of primary keys.
     * Returned array may not be the length of primaries; ignore extra entries.
     */
    private _lookupIndices(primaries);
    /**
     * Let the JIT have a small, well-typed chunk of array code to work with.
     */
    private _alterBulkFieldImpl(target, indices, source);
    /**
     * Zero a TypedArray (or normal array, I suppose)
     * @param start inclusive
     * @param end exclusive
     */
    private static _zero(arr, start, end);
    /**
     * Create a sorted array of keys to delete.
     * May allocate a new array, or reuse an old one.
     * Supplying nonexistent or repeated keys is not allowed.
     */
    private _makeToDelete(keys);
    /**
     * Delete a set of primary keys.
     */
    deleteBulk(keys: TypedArray): void;
    /**
     * @param toDelete at least one element; sorted ascending
     * @param array the array to modify
     */
    private _deleteBulkFieldImpl(toDelete, array);
    /**
     * Update the indices in the lookup table
     */
    private _refreshPrimariesLookup(newLength);
    /**
     * Resize internal storage, if needed.
     */
    private _resize(newLength);
    /**
     * Round up to the nearest power of two.
     */
    private static _capacityForLength(size);
    /**
     * Check invariants.
     */
    assertValid(): void;
}
/**
 * An array allocated as a contiguous block of memory.
 * Backed by an ArrayBuffer.
 */
export declare type TypedArray = Int8Array | Uint8Array | Int16Array | Uint16Array | Int32Array | Uint32Array | Float32Array | Float64Array;
/**
 * A constructor for a TypedArray.
 */
export declare type TypeSelector = new (...args: any[]) => TypedArray;
