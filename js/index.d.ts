declare module index {
    /**
     * @namespace
     */
    export namespace flatbuffers {
        /**
         * @typedef {number}
         */
        export type Offset = number;

        /**
         * @typedef {{
         *   bb: flatbuffers.ByteBuffer,
         *   bb_pos: number
         * }}
         */
        export type Table = {
           bb: flatbuffers.ByteBuffer,
           bb_pos: number
        };

        /**
         * @type {number}
         * @const
         */
        export const SIZEOF_SHORT: number;

        /**
         * @type {number}
         * @const
         */
        export const SIZEOF_INT: number;

        /**
         * @type {number}
         * @const
         */
        export const FILE_IDENTIFIER_LENGTH: number;

        /**
         * @enum {number}
         */
        export enum Encoding {
            UTF8_BYTES,
            UTF16_STRING
        }

        /**
         * @type {Int32Array}
         * @const
         */
        export const int32: Int32Array;

        /**
         * @type {Float32Array}
         * @const
         */
        export const float32: Float32Array;

        /**
         * @type {Float64Array}
         * @const
         */
        export const float64: Float64Array;

        /**
         * @type {boolean}
         * @const
         */
        export const isLittleEndian: boolean;

        /**
         * @constructor
         * @param {number} high
         * @param {number} low
         */
        export class Long {
            /**
             * @constructor
             * @param {number} high
             * @param {number} low
             */
            constructor(high: number, low: number);

            /**
             * @type {number}
             * @const
             */
            low: number;

            /**
             * @type {number}
             * @const
             */
            high: number;

            /**
             * @param {number} high
             * @param {number} low
             * @returns {flatbuffers.Long}
             */
            static create(high: number, low: number): flatbuffers.Long;

            /**
             * @returns {number}
             */
            toFloat64(): number;

            /**
             * @param {flatbuffers.Long} other
             * @returns {boolean}
             */
            equals(other: flatbuffers.Long): boolean;

            /**
             * @type {flatbuffers.Long}
             * @const
             */
            static ZERO: flatbuffers.Long;

        }

        /**
         * Create a FlatBufferBuilder.
         *
         * @constructor
         * @param {number=} initial_size
         */
        export class Builder {
            /**
             * Create a FlatBufferBuilder.
             *
             * @constructor
             * @param {number=} initial_size
             */
            constructor(initial_size?: number);

            /**
             * @type {flatbuffers.ByteBuffer}
             * @private
             */
            private bb: flatbuffers.ByteBuffer;

            /**
             * Remaining space in the ByteBuffer.
             *
             * @type {number}
             * @private
             */
            private space: number;

            /**
             * Minimum alignment encountered so far.
             *
             * @type {number}
             * @private
             */
            private minalign: number;

            /**
             * The vtable for the current table.
             *
             * @type {Array.<number>}
             * @private
             */
            private vtable: number[];

            /**
             * The amount of fields we're actually using.
             *
             * @type {number}
             * @private
             */
            private vtable_in_use: number;

            /**
             * Whether we are currently serializing a table.
             *
             * @type {boolean}
             * @private
             */
            private isNested: boolean;

            /**
             * Starting offset of the current struct/table.
             *
             * @type {number}
             * @private
             */
            private object_start: number;

            /**
             * List of offsets of all vtables.
             *
             * @type {Array.<number>}
             * @private
             */
            private vtables: number[];

            /**
             * For the current vector being built.
             *
             * @type {number}
             * @private
             */
            private vector_num_elems: number;

            /**
             * False omits default values from the serialized data
             *
             * @type {boolean}
             * @private
             */
            private force_defaults: boolean;

            /**
             * In order to save space, fields that are set to their default value
             * don't get serialized into the buffer. Forcing defaults provides a
             * way to manually disable this optimization.
             *
             * @param {boolean} forceDefaults true always serializes default values
             */
            forceDefaults(forceDefaults: boolean): void;

            /**
             * Get the ByteBuffer representing the FlatBuffer. Only call this after you've
             * called finish(). The actual data starts at the ByteBuffer's current position,
             * not necessarily at 0.
             *
             * @returns {flatbuffers.ByteBuffer}
             */
            dataBuffer(): flatbuffers.ByteBuffer;

            /**
             * Get the ByteBuffer representing the FlatBuffer. Only call this after you've
             * called finish(). The actual data starts at the ByteBuffer's current position,
             * not necessarily at 0.
             *
             * @returns {Uint8Array}
             */
            asUint8Array(): Uint8Array;

            /**
             * Prepare to write an element of `size` after `additional_bytes` have been
             * written, e.g. if you write a string, you need to align such the int length
             * field is aligned to 4 bytes, and the string data follows it directly. If all
             * you need to do is alignment, `additional_bytes` will be 0.
             *
             * @param {number} size This is the of the new element to write
             * @param {number} additional_bytes The padding size
             */
            prep(size: number, additional_bytes: number): void;

            /**
             * @param {number} byte_size
             */
            pad(byte_size: number): void;

            /**
             * @param {number} value
             */
            writeInt8(value: number): void;

            /**
             * @param {number} value
             */
            writeInt16(value: number): void;

            /**
             * @param {number} value
             */
            writeInt32(value: number): void;

            /**
             * @param {flatbuffers.Long} value
             */
            writeInt64(value: flatbuffers.Long): void;

            /**
             * @param {number} value
             */
            writeFloat32(value: number): void;

            /**
             * @param {number} value
             */
            writeFloat64(value: number): void;

            /**
             * Add an `int8` to the buffer, properly aligned, and grows the buffer (if necessary).
             * @param {number} value The `int8` to add the the buffer.
             */
            addInt8(value: number): void;

            /**
             * Add an `int16` to the buffer, properly aligned, and grows the buffer (if necessary).
             * @param {number} value The `int16` to add the the buffer.
             */
            addInt16(value: number): void;

            /**
             * Add an `int32` to the buffer, properly aligned, and grows the buffer (if necessary).
             * @param {number} value The `int32` to add the the buffer.
             */
            addInt32(value: number): void;

            /**
             * Add an `int64` to the buffer, properly aligned, and grows the buffer (if necessary).
             * @param {flatbuffers.Long} value The `int64` to add the the buffer.
             */
            addInt64(value: flatbuffers.Long): void;

            /**
             * Add a `float32` to the buffer, properly aligned, and grows the buffer (if necessary).
             * @param {number} value The `float32` to add the the buffer.
             */
            addFloat32(value: number): void;

            /**
             * Add a `float64` to the buffer, properly aligned, and grows the buffer (if necessary).
             * @param {number} value The `float64` to add the the buffer.
             */
            addFloat64(value: number): void;

            /**
             * @param {number} voffset
             * @param {number} value
             * @param {number} defaultValue
             */
            addFieldInt8(voffset: number, value: number, defaultValue: number): void;

            /**
             * @param {number} voffset
             * @param {number} value
             * @param {number} defaultValue
             */
            addFieldInt16(voffset: number, value: number, defaultValue: number): void;

            /**
             * @param {number} voffset
             * @param {number} value
             * @param {number} defaultValue
             */
            addFieldInt32(voffset: number, value: number, defaultValue: number): void;

            /**
             * @param {number} voffset
             * @param {flatbuffers.Long} value
             * @param {flatbuffers.Long} defaultValue
             */
            addFieldInt64(voffset: number, value: flatbuffers.Long, defaultValue: flatbuffers.Long): void;

            /**
             * @param {number} voffset
             * @param {number} value
             * @param {number} defaultValue
             */
            addFieldFloat32(voffset: number, value: number, defaultValue: number): void;

            /**
             * @param {number} voffset
             * @param {number} value
             * @param {number} defaultValue
             */
            addFieldFloat64(voffset: number, value: number, defaultValue: number): void;

            /**
             * @param {number} voffset
             * @param {flatbuffers.Offset} value
             * @param {flatbuffers.Offset} defaultValue
             */
            addFieldOffset(voffset: number, value: flatbuffers.Offset, defaultValue: flatbuffers.Offset): void;

            /**
             * Structs are stored inline, so nothing additional is being added. `d` is always 0.
             *
             * @param {number} voffset
             * @param {flatbuffers.Offset} value
             * @param {flatbuffers.Offset} defaultValue
             */
            addFieldStruct(voffset: number, value: flatbuffers.Offset, defaultValue: flatbuffers.Offset): void;

            /**
             * Structures are always stored inline, they need to be created right
             * where they're used.  You'll get this assertion failure if you
             * created it elsewhere.
             *
             * @param {flatbuffers.Offset} obj The offset of the created object
             */
            nested(obj: flatbuffers.Offset): void;

            /**
             * Should not be creating any other object, string or vector
             * while an object is being constructed
             */
            notNested(): void;

            /**
             * Set the current vtable at `voffset` to the current location in the buffer.
             *
             * @param {number} voffset
             */
            slot(voffset: number): void;

            /**
             * @returns {flatbuffers.Offset} Offset relative to the end of the buffer.
             */
            offset(): flatbuffers.Offset;

            /**
             * Doubles the size of the backing ByteBuffer and copies the old data towards
             * the end of the new buffer (since we build the buffer backwards).
             *
             * @param {flatbuffers.ByteBuffer} bb The current buffer with the existing data
             * @returns {flatbuffers.ByteBuffer} A new byte buffer with the old data copied
             * to it. The data is located at the end of the buffer.
             */
            static growByteBuffer(bb: flatbuffers.ByteBuffer): flatbuffers.ByteBuffer;

            /**
             * Adds on offset, relative to where it will be written.
             *
             * @param {flatbuffers.Offset} offset The offset to add.
             */
            addOffset(offset: flatbuffers.Offset): void;

            /**
             * Start encoding a new object in the buffer.  Users will not usually need to
             * call this directly. The FlatBuffers compiler will generate helper methods
             * that call this method internally.
             *
             * @param {number} numfields
             */
            startObject(numfields: number): void;

            /**
             * Finish off writing the object that is under construction.
             *
             * @returns {flatbuffers.Offset} The offset to the object inside `dataBuffer`
             */
            endObject(): flatbuffers.Offset;

            /**
             * Finalize a buffer, poiting to the given `root_table`.
             *
             * @param {flatbuffers.Offset} root_table
             * @param {string=} file_identifier
             */
            finish(root_table: flatbuffers.Offset, file_identifier?: string): void;

            /**
             * This checks a required field has been set in a given table that has
             * just been constructed.
             *
             * @param {flatbuffers.Offset} table
             * @param {number} field
             */
            requiredField(table: flatbuffers.Offset, field: number): void;

            /**
             * Start a new array/vector of objects.  Users usually will not call
             * this directly. The FlatBuffers compiler will create a start/end
             * method for vector types in generated code.
             *
             * @param {number} elem_size The size of each element in the array
             * @param {number} num_elems The number of elements in the array
             * @param {number} alignment The alignment of the array
             */
            startVector(elem_size: number, num_elems: number, alignment: number): void;

            /**
             * Finish off the creation of an array and all its elements. The array must be
             * created with `startVector`.
             *
             * @returns {flatbuffers.Offset} The offset at which the newly created array
             * starts.
             */
            endVector(): flatbuffers.Offset;

            /**
             * Encode the string `s` in the buffer using UTF-8. If a Uint8Array is passed
             * instead of a string, it is assumed to contain valid UTF-8 encoded data.
             *
             * @param {string|Uint8Array} s The string to encode
             * @return {flatbuffers.Offset} The offset in the buffer where the encoded string starts
             */
            createString(s: (string|Uint8Array)): flatbuffers.Offset;

            /**
             * A helper function to avoid generated code depending on this file directly.
             *
             * @param {number} low
             * @param {number} high
             * @returns {flatbuffers.Long}
             */
            createLong(low: number, high: number): flatbuffers.Long;

        }

        /**
         * Create a new ByteBuffer with a given array of bytes (`Uint8Array`).
         *
         * @constructor
         * @param {Uint8Array} bytes
         */
        export class ByteBuffer {
            /**
             * Create a new ByteBuffer with a given array of bytes (`Uint8Array`).
             *
             * @constructor
             * @param {Uint8Array} bytes
             */
            constructor(bytes: Uint8Array);

            /**
             * @type {Uint8Array}
             * @private
             */
            private bytes_: Uint8Array;

            /**
             * @type {number}
             * @private
             */
            private position_: number;

            /**
             * Create and allocate a new ByteBuffer with a given size.
             *
             * @param {number} byte_size
             * @returns {flatbuffers.ByteBuffer}
             */
            static allocate(byte_size: number): flatbuffers.ByteBuffer;

            /**
             * Get the underlying `Uint8Array`.
             *
             * @returns {Uint8Array}
             */
            bytes(): Uint8Array;

            /**
             * Get the buffer's position.
             *
             * @returns {number}
             */
            position(): number;

            /**
             * Set the buffer's position.
             *
             * @param {number} position
             */
            setPosition(position: number): void;

            /**
             * Get the buffer's capacity.
             *
             * @returns {number}
             */
            capacity(): number;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readInt8(offset: number): number;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readUint8(offset: number): number;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readInt16(offset: number): number;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readUint16(offset: number): number;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readInt32(offset: number): number;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readUint32(offset: number): number;

            /**
             * @param {number} offset
             * @returns {flatbuffers.Long}
             */
            readInt64(offset: number): flatbuffers.Long;

            /**
             * @param {number} offset
             * @returns {flatbuffers.Long}
             */
            readUint64(offset: number): flatbuffers.Long;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readFloat32(offset: number): number;

            /**
             * @param {number} offset
             * @returns {number}
             */
            readFloat64(offset: number): number;

            /**
             * @param {number} offset
             * @param {number} value
             */
            writeInt8(offset: number, value: number): void;

            /**
             * @param {number} offset
             * @param {number} value
             */
            writeInt16(offset: number, value: number): void;

            /**
             * @param {number} offset
             * @param {number} value
             */
            writeInt32(offset: number, value: number): void;

            /**
             * @param {number} offset
             * @param {flatbuffers.Long} value
             */
            writeInt64(offset: number, value: flatbuffers.Long): void;

            /**
             * @param {number} offset
             * @param {number} value
             */
            writeFloat32(offset: number, value: number): void;

            /**
             * @param {number} offset
             * @param {number} value
             */
            writeFloat64(offset: number, value: number): void;

            /**
             * Look up a field in the vtable, return an offset into the object, or 0 if the
             * field is not present.
             *
             * @param {number} bb_pos
             * @param {number} vtable_offset
             * @returns {number}
             */
            __offset(bb_pos: number, vtable_offset: number): number;

            /**
             * Initialize any Table-derived type to point to the union at the given offset.
             *
             * @param {flatbuffers.Table} t
             * @param {number} offset
             * @returns {flatbuffers.Table}
             */
            __union(t: flatbuffers.Table, offset: number): flatbuffers.Table;

            /**
             * Create a JavaScript string from UTF-8 data stored inside the FlatBuffer.
             * This allocates a new string and converts to wide chars upon each access.
             *
             * To avoid the conversion to UTF-16, pass flatbuffers.Encoding.UTF8_BYTES as
             * the "optionalEncoding" argument. This is useful for avoiding conversion to
             * and from UTF-16 when the data will just be packaged back up in another
             * FlatBuffer later on.
             *
             * @param {number} offset
             * @param {flatbuffers.Encoding=} optionalEncoding Defaults to UTF16_STRING
             * @returns {string|Uint8Array}
             */
            __string(offset: number, optionalEncoding?: flatbuffers.Encoding): (string|Uint8Array);

            /**
             * Retrieve the relative offset stored at "offset"
             * @param {number} offset
             * @returns {number}
             */
            __indirect(offset: number): number;

            /**
             * Get the start of data of a vector whose offset is stored at "offset" in this object.
             *
             * @param {number} offset
             * @returns {number}
             */
            __vector(offset: number): number;

            /**
             * Get the length of a vector whose offset is stored at "offset" in this object.
             *
             * @param {number} offset
             * @returns {number}
             */
            __vector_len(offset: number): number;

            /**
             * @param {string} ident
             * @returns {boolean}
             */
            __has_identifier(ident: string): boolean;

            /**
             * A helper function to avoid generated code depending on this file directly.
             *
             * @param {number} low
             * @param {number} high
             * @returns {flatbuffers.Long}
             */
            createLong(low: number, high: number): flatbuffers.Long;
        }
    }

    /**
     * @namespace
     */
    export namespace schema {
        /**
         * The possible types of things that can exist.
         * Note that neutral trees are not treated as bodies.
         *
         * @enum
         */
        export enum BodyType {
            /**
             * Archons are the mobile equivalent of a HQ whose sole purpose is to hire
             * gardeners to maintain the land.
             */
            ARCHON,
            /**
             * Gardeners are caretakers of the land, planting and watering Bullet Trees
             * while also cultivating all other player robots.
             */
            GARDENER,
            /**
             * Lumberjacks are melee units equipped for felling trees.
             */
            LUMBERJACK,
            /**
             * Recruits are all-around units with a tricky shot.
             */
            RECRUIT,
            /**
             * Soldiers are all-around units with a tricky shot. But different.
             */
            SOLDIER,
            /**
             * Tanks are large, slow units with powerful bullets.
             */
            TANK,
            /**
             * Scouts are fast units that move around without obstruction.
             */
            SCOUT,
            /**
             * A bullet that moves in a perfectly straight line.
             * Note: bullet location updates are not sent; a bullet is defined to be
             * in position loc + dt * vel after dt seconds.
             * This allows us some significant space savings, since there are lots
             * of bullets, and we don't need to send position updates.
             * The event stream will say if a bullet has been destroyed.
             */
            BULLET,
            /**
             * A tree that belongs to a team and produces bullets.
             */
            TREE_BULLET,
            /**
             * A neutral tree.
             * This is included for convenience; note this value SHALL NOT appear in
             * a SpawnedBodyTable.
             */
            TREE_NEUTRAL
        }

        /**
         * Actions that can be performed.
         * Purely aesthetic; have no actual effect on simulation.
         * (Although the simulation may want to track the 'parents' of
         * particular robots.)
         * Actions may have 'targets', which are the units on which
         * the actions were performed.
         *
         * @enum
         */
        export enum Action {
            /**
             * Fire a bullet.
             * Target: spawned bullet.
             */
            FIRE,
            /**
             * Fire three bullets.
             * Target: spawned bullets.
             */
            FIRE_TRIAD,
            /**
             * Fire five bullets.
             * Target: spawned bullets.
             */
            FIRE_PENTAD,
            /**
             * Perform a lumberjack-chop.
             * Target: none
             */
            CHOP,
            /**
             * Shake a tree.
             * Target: tree
             */
            SHAKE_TREE,
            /**
             * Plant a tree.
             * Target: tree
             */
            PLANT_TREE,
            /**
             * Water a tree.
             * Target: tree
             */
            WATER_TREE,
            /**
             * Build a unit.
             * Target: spawned unit
             */
            SPAWN_UNIT,
            /**
             * Die due to an uncaught exception
             * Target: none
             */
            DIE_EXCEPTION,
            /**
             * Die due to suicide.
             * Target: none
             */
            DIE_SUICIDE
        }

        /**
         * An Event is a single step that needs to be processed.
         * A saved game simply consists of a long list of Events.
         * Events can be divided by either being sent separately (e.g. as separate
         * websocket messages), or by being wrapped with a GameWrapper.
         * A game consists of a series of matches; a match consists of a series of
         * rounds, and is played on a single map. Each round is a single simulation
         * step.
         *
         * @enum
         */
        export enum Event {
            NONE,
            /**
             * There should only be one GameHeader, at the start of the stream.
             */
            GameHeader,
            /**
             * There should be one MatchHeader at the start of each match.
             */
            MatchHeader,
            /**
             * A single simulation step. A round may be skipped if
             * nothing happens during its time.
             */
            Round,
            /**
             * There should be one MatchFooter at the end of each simulation step.
             */
            MatchFooter,
            /**
             * There should only be one GameFooter, at the end of the stream.
             */
            GameFooter
        }

        /**
         * A vector in two-dimensional space. Continuous space, of course.
         * Defaults to the 0 vector.
         *
         * @constructor
         */
        export class Vec {
            /**
             * A vector in two-dimensional space. Continuous space, of course.
             * Defaults to the 0 vector.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.Vec}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.Vec;

            /**
             * @returns {number}
             */
            x(): number;

            /**
             * @returns {number}
             */
            y(): number;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} x
             * @param {number} y
             * @returns {flatbuffers.Offset}
             */
            static createVec(builder: flatbuffers.Builder, x: number, y: number): flatbuffers.Offset;

        }

        /**
         * A table of vectors.
         *
         * @constructor
         */
        export class VecTable {
            /**
             * A table of vectors.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.VecTable}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.VecTable;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.VecTable=} obj
             * @returns {schema.VecTable}
             */
            static getRootAsVecTable(bb: flatbuffers.ByteBuffer, obj?: schema.VecTable): schema.VecTable;

            /**
             * @param {number} index
             * @returns {number}
             */
            xs(index: number): number;

            /**
             * @returns {number}
             */
            xsLength(): number;

            /**
             * @returns {Float32Array}
             */
            xsArray(): Float32Array;

            /**
             * @param {number} index
             * @returns {number}
             */
            ys(index: number): number;

            /**
             * @returns {number}
             */
            ysLength(): number;

            /**
             * @returns {Float32Array}
             */
            ysArray(): Float32Array;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startVecTable(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} xsOffset
             */
            static addXs(builder: flatbuffers.Builder, xsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createXsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startXsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} ysOffset
             */
            static addYs(builder: flatbuffers.Builder, ysOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createYsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startYsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endVecTable(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * A table of RGB values.
         *
         * @constructor
         */
        export class RGBTable {
            /**
             * A table of RGB values.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.RGBTable}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.RGBTable;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.RGBTable=} obj
             * @returns {schema.RGBTable}
             */
            static getRootAsRGBTable(bb: flatbuffers.ByteBuffer, obj?: schema.RGBTable): schema.RGBTable;

            /**
             * @param {number} index
             * @returns {number}
             */
            red(index: number): number;

            /**
             * @returns {number}
             */
            redLength(): number;

            /**
             * @returns {Int32Array}
             */
            redArray(): Int32Array;

            /**
             * @param {number} index
             * @returns {number}
             */
            green(index: number): number;

            /**
             * @returns {number}
             */
            greenLength(): number;

            /**
             * @returns {Int32Array}
             */
            greenArray(): Int32Array;

            /**
             * @param {number} index
             * @returns {number}
             */
            blue(index: number): number;

            /**
             * @returns {number}
             */
            blueLength(): number;

            /**
             * @returns {Int32Array}
             */
            blueArray(): Int32Array;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startRGBTable(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} redOffset
             */
            static addRed(builder: flatbuffers.Builder, redOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createRedVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startRedVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} greenOffset
             */
            static addGreen(builder: flatbuffers.Builder, greenOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createGreenVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startGreenVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} blueOffset
             */
            static addBlue(builder: flatbuffers.Builder, blueOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createBlueVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startBlueVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endRGBTable(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * A list of new bodies to be placed on the map.
         *
         * @constructor
         */
        export class SpawnedBodyTable {
            /**
             * A list of new bodies to be placed on the map.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.SpawnedBodyTable}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.SpawnedBodyTable;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.SpawnedBodyTable=} obj
             * @returns {schema.SpawnedBodyTable}
             */
            static getRootAsSpawnedBodyTable(bb: flatbuffers.ByteBuffer, obj?: schema.SpawnedBodyTable): schema.SpawnedBodyTable;

            /**
             * The numeric ID of the new bodies.
             * Will never be negative.
             * There will only be one body/bullet with a particular ID at a time.
             * So, there will never be two robots with the same ID, or a robot and
             * a bullet with the same ID.
             *
             * @param {number} index
             * @returns {number}
             */
            robotIDs(index: number): number;

            /**
             * @returns {number}
             */
            robotIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            robotIDsArray(): Int32Array;

            /**
             * The teams of the new bodies.
             *
             * @param {number} index
             * @returns {number}
             */
            teamIDs(index: number): number;

            /**
             * @returns {number}
             */
            teamIDsLength(): number;

            /**
             * @returns {Int8Array}
             */
            teamIDsArray(): Int8Array;

            /**
             * The types of the new bodies.
             *
             * @param {number} index
             * @returns {schema.BodyType}
             */
            types(index: number): schema.BodyType;

            /**
             * @returns {number}
             */
            typesLength(): number;

            /**
             * @returns {Int8Array}
             */
            typesArray(): Int8Array;

            /**
             * The locations of the bodies.
             *
             * @param {schema.VecTable=} obj
             * @returns {schema.VecTable}
             */
            locs(obj?: schema.VecTable): schema.VecTable;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startSpawnedBodyTable(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} robotIDsOffset
             */
            static addRobotIDs(builder: flatbuffers.Builder, robotIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createRobotIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startRobotIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} teamIDsOffset
             */
            static addTeamIDs(builder: flatbuffers.Builder, teamIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createTeamIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startTeamIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} typesOffset
             */
            static addTypes(builder: flatbuffers.Builder, typesOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<schema.BodyType>} data
             * @returns {flatbuffers.Offset}
             */
            static createTypesVector(builder: flatbuffers.Builder, data: schema.BodyType[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startTypesVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} locsOffset
             */
            static addLocs(builder: flatbuffers.Builder, locsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endSpawnedBodyTable(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * A list of neutral trees to be placed on the map.
         *
         * @constructor
         */
        export class NeutralTreeTable {
            /**
             * A list of neutral trees to be placed on the map.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.NeutralTreeTable}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.NeutralTreeTable;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.NeutralTreeTable=} obj
             * @returns {schema.NeutralTreeTable}
             */
            static getRootAsNeutralTreeTable(bb: flatbuffers.ByteBuffer, obj?: schema.NeutralTreeTable): schema.NeutralTreeTable;

            /**
             * The IDs of the trees.
             *
             * @param {number} index
             * @returns {number}
             */
            robotIDs(index: number): number;

            /**
             * @returns {number}
             */
            robotIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            robotIDsArray(): Int32Array;

            /**
             * The locations of the trees.
             *
             * @param {schema.VecTable=} obj
             * @returns {schema.VecTable}
             */
            locs(obj?: schema.VecTable): schema.VecTable;

            /**
             * The radii of the trees.
             *
             * @param {number} index
             * @returns {number}
             */
            radii(index: number): number;

            /**
             * @returns {number}
             */
            radiiLength(): number;

            /**
             * @returns {Float32Array}
             */
            radiiArray(): Float32Array;

            /**
             * The healths of the trees.
             *
             * @param {number} index
             * @returns {number}
             */
            healths(index: number): number;

            /**
             * @returns {number}
             */
            health(): number;

            /**
             * @returns {Float32Array}
             */
            healthsArray(): Float32Array;

            /**
             * The bullets contained within the trees.
             *
             * @param {number} index
             * @returns {number}
             */
            containedBullets(index: number): number;

            /**
             * @returns {number}
             */
            containedBulletsLength(): number;

            /**
             * @returns {Int32Array}
             */
            containedBulletsArray(): Int32Array;

            /**
             * The bodies contained within the trees (one table for each tree;
             * teamIDs and robotIDs will be unset.)
             *
             * @param {number} index
             * @param {schema.SpawnedBodyTable=} obj
             * @returns {schema.SpawnedBodyTable}
             */
            containedBodies(index: number): schema.BodyType;

            /**
             * @returns {number}
             */
            containedBodiesLength(): number;

            /**
             * @returns {Int8Array}
             */
            containedBodiesArray(): Int8Array;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startNeutralTreeTable(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} robotIDsOffset
             */
            static addRobotIDs(builder: flatbuffers.Builder, robotIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createRobotIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startRobotIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} locsOffset
             */
            static addLocs(builder: flatbuffers.Builder, locsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} radiiOffset
             */
            static addRadii(builder: flatbuffers.Builder, radiiOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createRadiiVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startRadiiVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} containedBulletsOffset
             */
            static addContainedBullets(builder: flatbuffers.Builder, containedBulletsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createContainedBulletsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startContainedBulletsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} containedBodiesOffset
             */
            static addContainedBodies(builder: flatbuffers.Builder, containedBodiesOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<flatbuffers.Offset>} data
             * @returns {flatbuffers.Offset}
             */
            static createContainedBodiesVector(builder: flatbuffers.Builder, data: flatbuffers.Offset[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startContainedBodiesVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} containedTreesOffset
             */
            static addContainedTrees(builder: flatbuffers.Builder, containedTreesOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<flatbuffers.Offset>} data
             * @returns {flatbuffers.Offset}
             */
            static createContainedTreesVector(builder: flatbuffers.Builder, data: flatbuffers.Offset[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startContainedTreesVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endNeutralTreeTable(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * A list of new bullets to be placed on the map.
         *
         * @constructor
         */
        export class SpawnedBulletTable {
            /**
             * A list of new bullets to be placed on the map.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.SpawnedBulletTable}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.SpawnedBulletTable;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.SpawnedBulletTable=} obj
             * @returns {schema.SpawnedBulletTable}
             */
            static getRootAsSpawnedBulletTable(bb: flatbuffers.ByteBuffer, obj?: schema.SpawnedBulletTable): schema.SpawnedBulletTable;

            /**
             * The numeric ID of the new bullets.
             * Will never be negative.
             * There will only be one body/bullet with a particular ID at a time.
             * So, there will never be two robots with the same ID, or a robot and
             * a bullet with the same ID.
             *
             * @param {number} index
             * @returns {number}
             */
            robotIDs(index: number): number;

            /**
             * @returns {number}
             */
            robotIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            robotIDsArray(): Int32Array;

            /**
             * The locations of the bodies.
             *
             * @param {schema.VecTable=} obj
             * @returns {schema.VecTable}
             */
            locs(obj?: schema.VecTable): schema.VecTable;

            /**
             * The velocities of the bodies.
             *
             * @param {schema.VecTable=} obj
             * @returns {schema.VecTable}
             */
            vels(obj?: schema.VecTable): schema.VecTable;

            /**
             * The damage levels of the bodies.
             *
             * @param {number} index
             * @returns {number}
             */
            damages(index: number): number;

            /**
             * @returns {number}
             */
            damagesLength(): number;

            /**
             * @returns {Float32Array}
             */
            damagesArray(): Float32Array;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startSpawnedBulletTable(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} robotIDsOffset
             */
            static addRobotIDs(builder: flatbuffers.Builder, robotIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createRobotIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startRobotIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} locsOffset
             */
            static addLocs(builder: flatbuffers.Builder, locsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} velsOffset
             */
            static addVels(builder: flatbuffers.Builder, velsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} damagesOffset
             */
            static addDamages(builder: flatbuffers.Builder, damagesOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createDamagesVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startDamagesVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endSpawnedBulletTable(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * The map a round is played on.
         *
         * @constructor
         */
        export class GameMap {
            /**
             * The map a round is played on.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.GameMap}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.GameMap;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.GameMap=} obj
             * @returns {schema.GameMap}
             */
            static getRootAsGameMap(bb: flatbuffers.ByteBuffer, obj?: schema.GameMap): schema.GameMap;

            /**
             * The name of a map.
             *
             * @param {flatbuffers.Encoding=} optionalEncoding
             * @returns {string|Uint8Array}
             */
            name(optionalEncoding?: flatbuffers.Encoding): (string|Uint8Array);

            /**
             * The bottom corner of the map.
             *
             * @param {schema.Vec=} obj
             * @returns {schema.Vec}
             */
            minCorner(obj?: schema.Vec): schema.Vec;

            /**
             * The top corner of the map.
             *
             * @param {schema.Vec=} obj
             * @returns {schema.Vec}
             */
            maxCorner(obj?: schema.Vec): schema.Vec;

            /**
             * The bodies on the map.
             *
             * @param {schema.SpawnedBodyTable=} obj
             * @returns {schema.SpawnedBodyTable}
             */
            bodies(obj?: schema.SpawnedBodyTable): schema.SpawnedBodyTable;

            /**
             * The neutral trees on the map.
             *
             * @param {schema.NeutralTreeTable=} obj
             * @returns {schema.NeutralTreeTable}
             */
            trees(obj?: schema.NeutralTreeTable): schema.NeutralTreeTable;

            /**
             * The random seed of the map.
             *
             * @returns {number}
             */
            randomSeed(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startGameMap(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} nameOffset
             */
            static addName(builder: flatbuffers.Builder, nameOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} minCornerOffset
             */
            static addMinCorner(builder: flatbuffers.Builder, minCornerOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} maxCornerOffset
             */
            static addMaxCorner(builder: flatbuffers.Builder, maxCornerOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} bodiesOffset
             */
            static addBodies(builder: flatbuffers.Builder, bodiesOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} treesOffset
             */
            static addTrees(builder: flatbuffers.Builder, treesOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} randomSeed
             */
            static addRandomSeed(builder: flatbuffers.Builder, randomSeed: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endGameMap(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * Metadata about all bodies of a particular type.
         *
         * @constructor
         */
        export class BodyTypeMetadata {
            /**
             * Metadata about all bodies of a particular type.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.BodyTypeMetadata}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.BodyTypeMetadata;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.BodyTypeMetadata=} obj
             * @returns {schema.BodyTypeMetadata}
             */
            static getRootAsBodyTypeMetadata(bb: flatbuffers.ByteBuffer, obj?: schema.BodyTypeMetadata): schema.BodyTypeMetadata;

            /**
             * The relevant type.
             *
             * @returns {schema.BodyType}
             */
            type(): schema.BodyType;

            /**
             * The radius of the type, in distance units.
             *
             * @returns {number}
             */
            radius(): number;

            /**
             * The cost of the type, in bullets.
             *
             * @returns {number}
             */
            cost(): number;

            /**
             * The maxiumum health of the type, in health units.
             *
             * @returns {number}
             */
            maxHealth(): number;

            /**
             * If unset, the same as maxHealth.
             * Otherwise, the health a body of this type starts with.
             *
             * @returns {number}
             */
            startHealth(): number;

            /**
             * The maximum distance this type can move each turn
             *
             * @returns {number}
             */
            strideRadius(): number;

            /**
             * The speed that bullets from this unit move.
             * Note: you don't need to keep track of this, SpawnedBody.vel will always be set.
             *
             * @returns {number}
             */
            bulletSpeed(): number;

            /**
             * The damage that bullets from this unit inflict.
             * Note: you don't need to keep track of this.
             *
             * @returns {number}
             */
            bulletAttack(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startBodyTypeMetadata(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {schema.BodyType} type
             */
            static addType(builder: flatbuffers.Builder, type: schema.BodyType): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} radius
             */
            static addRadius(builder: flatbuffers.Builder, radius: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} cost
             */
            static addCost(builder: flatbuffers.Builder, cost: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} maxHealth
             */
            static addMaxHealth(builder: flatbuffers.Builder, maxHealth: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} startHealth
             */
            static addStartHealth(builder: flatbuffers.Builder, startHealth: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} strideRadius
             */
            static addStrideRadius(builder: flatbuffers.Builder, strideRadius: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} bulletSpeed
             */
            static addBulletSpeed(builder: flatbuffers.Builder, bulletSpeed: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} bulletAttack
             */
            static addBulletAttack(builder: flatbuffers.Builder, bulletAttack: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endBodyTypeMetadata(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * Data relevant to a particular team.
         *
         * @constructor
         */
        export class TeamData {
            /**
             * Data relevant to a particular team.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.TeamData}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.TeamData;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.TeamData=} obj
             * @returns {schema.TeamData}
             */
            static getRootAsTeamData(bb: flatbuffers.ByteBuffer, obj?: schema.TeamData): schema.TeamData;

            /**
             * The name of the team.
             *
             * @param {flatbuffers.Encoding=} optionalEncoding
             * @returns {string|Uint8Array}
             */
            name(optionalEncoding?: flatbuffers.Encoding): (string|Uint8Array);

            /**
             * The java package the team uses.
             *
             * @param {flatbuffers.Encoding=} optionalEncoding
             * @returns {string|Uint8Array}
             */
            packageName(optionalEncoding?: flatbuffers.Encoding): (string|Uint8Array);

            /**
             * The ID of the team this data pertains to.
             *
             * @returns {number}
             */
            teamID(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startTeamData(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} nameOffset
             */
            static addName(builder: flatbuffers.Builder, nameOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} packageNameOffset
             */
            static addPackageName(builder: flatbuffers.Builder, packageNameOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} teamID
             */
            static addTeamID(builder: flatbuffers.Builder, teamID: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endTeamData(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * The first event sent in the game. Contains all metadata about the game.
         *
         * @constructor
         */
        export class GameHeader {
            /**
             * The first event sent in the game. Contains all metadata about the game.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.GameHeader}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.GameHeader;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.GameHeader=} obj
             * @returns {schema.GameHeader}
             */
            static getRootAsGameHeader(bb: flatbuffers.ByteBuffer, obj?: schema.GameHeader): schema.GameHeader;

            /**
             * The version of the spec this game complies with.
             *
             * @param {flatbuffers.Encoding=} optionalEncoding
             * @returns {string|Uint8Array}
             */
            specVersion(optionalEncoding?: flatbuffers.Encoding): (string|Uint8Array);

            /**
             * The teams participating in the game.
             *
             * @param {number} index
             * @param {schema.TeamData=} obj
             * @returns {schema.TeamData}
             */
            teams(index: number, obj?: schema.TeamData): schema.TeamData;

            /**
             * @returns {number}
             */
            teamsLength(): number;

            /**
             * Information about all body types in the game.
             *
             * @param {number} index
             * @param {schema.BodyTypeMetadata=} obj
             * @returns {schema.BodyTypeMetadata}
             */
            bodyTypeMetadata(index: number, obj?: schema.BodyTypeMetadata): schema.BodyTypeMetadata;

            /**
             * @returns {number}
             */
            bodyTypeMetadataLength(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startGameHeader(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} specVersionOffset
             */
            static addSpecVersion(builder: flatbuffers.Builder, specVersionOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} teamsOffset
             */
            static addTeams(builder: flatbuffers.Builder, teamsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<flatbuffers.Offset>} data
             * @returns {flatbuffers.Offset}
             */
            static createTeamsVector(builder: flatbuffers.Builder, data: flatbuffers.Offset[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startTeamsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} bodyTypeMetadataOffset
             */
            static addBodyTypeMetadata(builder: flatbuffers.Builder, bodyTypeMetadataOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<flatbuffers.Offset>} data
             * @returns {flatbuffers.Offset}
             */
            static createBodyTypeMetadataVector(builder: flatbuffers.Builder, data: flatbuffers.Offset[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startBodyTypeMetadataVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endGameHeader(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * The final event sent in the game.
         *
         * @constructor
         */
        export class GameFooter {
            /**
             * The final event sent in the game.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.GameFooter}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.GameFooter;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.GameFooter=} obj
             * @returns {schema.GameFooter}
             */
            static getRootAsGameFooter(bb: flatbuffers.ByteBuffer, obj?: schema.GameFooter): schema.GameFooter;

            /**
             * The ID of the winning team of the game.
             *
             * @returns {number}
             */
            winner(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startGameFooter(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} winner
             */
            static addWinner(builder: flatbuffers.Builder, winner: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endGameFooter(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * Sent to start a match.
         *
         * @constructor
         */
        export class MatchHeader {
            /**
             * Sent to start a match.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.MatchHeader}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.MatchHeader;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.MatchHeader=} obj
             * @returns {schema.MatchHeader}
             */
            static getRootAsMatchHeader(bb: flatbuffers.ByteBuffer, obj?: schema.MatchHeader): schema.MatchHeader;

            /**
             * The map the match was played on.
             *
             * @param {schema.GameMap=} obj
             * @returns {schema.GameMap}
             */
            map(obj?: schema.GameMap): schema.GameMap;

            /**
             * The maximum number of rounds in this match.
             *
             * @returns {number}
             */
            maxRounds(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startMatchHeader(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} mapOffset
             */
            static addMap(builder: flatbuffers.Builder, mapOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} maxRounds
             */
            static addMaxRounds(builder: flatbuffers.Builder, maxRounds: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endMatchHeader(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * Sent to end a match.
         *
         * @constructor
         */
        export class MatchFooter {
            /**
             * Sent to end a match.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.MatchFooter}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.MatchFooter;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.MatchFooter=} obj
             * @returns {schema.MatchFooter}
             */
            static getRootAsMatchFooter(bb: flatbuffers.ByteBuffer, obj?: schema.MatchFooter): schema.MatchFooter;

            /**
             * The ID of the winning team.
             *
             * @returns {number}
             */
            winner(): number;

            /**
             * The number of rounds played.
             *
             * @returns {number}
             */
            totalRounds(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startMatchFooter(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} winner
             */
            static addWinner(builder: flatbuffers.Builder, winner: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} totalRounds
             */
            static addTotalRounds(builder: flatbuffers.Builder, totalRounds: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endMatchFooter(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * A single time-step in a Game.
         * The bulk of the data in the file is stored in tables like this.
         * Note that a struct-of-arrays format is more space efficient than an array-
         * of-structs.
         *
         * @constructor
         */
        export class Round {
            /**
             * A single time-step in a Game.
             * The bulk of the data in the file is stored in tables like this.
             * Note that a struct-of-arrays format is more space efficient than an array-
             * of-structs.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.Round}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.Round;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.Round=} obj
             * @returns {schema.Round}
             */
            static getRootAsRound(bb: flatbuffers.ByteBuffer, obj?: schema.Round): schema.Round;



            /**
             * The IDs of teams in the Game.
             *
             * @param {number} index
             * @returns {number}
             */
            teamIDs(index: number): number;

            /**
             * @returns {number}
             */
            teamIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            teamIDsArray(): Int32Array;

            /**
             * The bullet counts of the teams.
             *
             * @param {number} index
             * @returns {number}
             */
            teamBullets(index: number): number;

            /**
             * @returns {number}
             */
            teamBulletsLength(): number;

            /**
             * @returns {Float32Array}
             */
            teamBulletsArray(): Float32Array;

            /**
             * The victory points of the teams.
             *
             * @param {number} index
             * @returns {number}
             */
            teamVictoryPoints(index: number): number;

            /**
             * @returns {number}
             */
            teamVictoryPointsLength(): number;

            /**
             * @returns {Int32Array}
             */
            teamVictoryPointsArray(): Int32Array;

            /**
             * The IDs of bodies that moved.
             *
             * @param {number} index
             * @returns {number}
             */
            movedIDs(index: number): number;

            /**
             * @returns {number}
             */
            movedIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            movedIDsArray(): Int32Array;

            /**
             * The new locations of bodies that have moved. They are defined to be in
             * their new locations at exactly the time round.number*dt.
             *
             * @param {schema.VecTable=} obj
             * @returns {schema.VecTable}
             */
            movedLocs(obj?: schema.VecTable): schema.VecTable;

            /**
             * New bodies.
             *
             * @param {schema.SpawnedBodyTable=} obj
             * @returns {schema.SpawnedBodyTable}
             */
            spawnedBodies(obj?: schema.SpawnedBodyTable): schema.SpawnedBodyTable;

            /**
             * New bullets.
             *
             * @param {schema.SpawnedBulletTable=} obj
             * @returns {schema.SpawnedBulletTable}
             */
            spawnedBullets(obj?: schema.SpawnedBulletTable): schema.SpawnedBulletTable;

            /**
             * The IDs of bodies with changed health.
             *
             * @param {number} index
             * @returns {number}
             */
            healthChangedIDs(index: number): number;

            /**
             * @returns {number}
             */
            healthChangedIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            healthChangedIDsArray(): Int32Array;

            /**
             * The new health levels of bodies with changed health.
             *
             * @param {number} index
             * @returns {number}
             */
            healthChangeLevels(index: number): number;

            /**
             * @returns {number}
             */
            healthChangeLevelsLength(): number;

            /**
             * @returns {Float32Array}
             */
            healthChangeLevelsArray(): Float32Array;

            /**
             * The IDs of bodies that died.
             *
             * @param {number} index
             * @returns {number}
             */
            diedIDs(index: number): number;

            /**
             * @returns {number}
             */
            diedIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            diedIDsArray(): Int32Array;

            /**
             * The IDs of bullets that died.
             *
             * @param {number} index
             * @returns {number}
             */
            diedBulletIDs(index: number): number;

            /**
             * @returns {number}
             */
            diedBulletIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            diedBulletIDsArray(): Int32Array;

            /**
             * The IDs of robots that performed actions.
             * IDs may repeat.
             *
             * @param {number} index
             * @returns {number}
             */
            actionIDs(index: number): number;

            /**
             * @returns {number}
             */
            actionIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            actionIDsArray(): Int32Array;

            /**
             * The actions performed.
             *
             * @param {number} index
             * @returns {schema.Action}
             */
            actions(index: number): schema.Action;

            /**
             * @returns {number}
             */
            actionsLength(): number;

            /**
             * @returns {Int8Array}
             */
            actionsArray(): Int8Array;

            /**
             * The 'targets' of the performed actions. Actions without targets may have
             * any target (typically 0).
             *
             * @param {number} index
             * @returns {number}
             */
            actionTargets(index: number): number;

            /**
             * @returns {number}
             */
            actionTargetsLength(): number;

            /**
             * @returns {Int32Array}
             */
            actionTargetsArray(): Int32Array;

            /**
             * The IDs of bodies that set indicator strings.
             *
             * @param {number} index
             * @returns {number}
             */
            indicatorStringIDs(index: number): number;

            /**
             * @returns {number}
             */
            indicatorStringIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            indicatorStringIDsArray(): Int32Array;

            /**
             * The indices of the indicator strings that were set.
             *
             * @param {number} index
             * @returns {number}
             */
            indicatorStringIndices(index: number): number;

            /**
             * @returns {number}
             */
            indicatorStringIndicesLength(): number;

            /**
             * @returns {Int32Array}
             */
            indicatorStringIndicesArray(): Int32Array;

            /**
             * The values of the indicator strings that were set.
             *
             * @param {number} index
             * @param {flatbuffers.Encoding=} optionalEncoding
             * @returns {string|Uint8Array}
             */
            indicatorStringValues(index: number, optionalEncoding: flatbuffers.Encoding): string | Uint8Array;

            /**
             * @returns {number}
             */
            indicatorStringValuesLength(): number;

            /**
             * The IDs of bodies that set indicator dots.
             *
             * @param {number} index
             * @returns {number}
             */
            indicatorDotIDs(index: number): number;

            /**
             * @returns {number}
             */
            indicatorDotIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            indicatorDotIDsArray(): Int32Array;

            /**
             * The location of the indicator dots
             *
             * @param {battlecode.schema.VecTable=} obj
             * @returns {battlecode.schema.VecTable}
             */
            indicatorDotLocs(obj?: schema.VecTable): schema.VecTable;

            /**
             * The RGB values of the indicator dots
             *
             * @param {battlecode.schema.RGBTable=} obj
             * @returns {battlecode.schema.RGBTable}
             */
            indicatorDotRGBs(obj?: schema.RGBTable): schema.RGBTable;

            /**
             * The IDs of bodies that set indicator lines.
             *
             * @param {number} index
             * @returns {number}
             */
            indicatorLineIDs(index: number): number;

            /**
             * @returns {number}
             */
            indicatorLineIDsLength(): number;

            /**
             * @returns {Int32Array}
             */
            indicatorLineIDsArray(): Int32Array;

            /**
             * The start location of the indicator lines
             *
             * @param {battlecode.schema.VecTable=} obj
             * @returns {battlecode.schema.VecTable}
             */
            indicatorLineStartLocs(obj?: schema.VecTable): schema.VecTable;

            /**
             * The end location of the indicator lines
             *
             * @param {battlecode.schema.VecTable=} obj
             * @returns {battlecode.schema.VecTable}
             */
            indicatorLineEndLocs(obj?: schema.VecTable): schema.VecTable;

            /**
             * The RGB values of the indicator lines
             *
             * @param {battlecode.schema.RGBTable=} obj
             * @returns {battlecode.schema.RGBTable}
             */
            indicatorLineRGBs(obj?: schema.RGBTable): schema.RGBTable;

            /**
             * The first sent Round in a match should have index 1. (The starting state,
             * created by the MatchHeader, can be thought to have index 0.)
             * It should increase by one for each following round.
             *
             * @returns {number}
             */
            roundID(): number;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startRound(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} movedIDsOffset
             */
            static addMovedIDs(builder: flatbuffers.Builder, movedIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createMovedIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startMovedIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} movedLocsOffset
             */
            static addMovedLocs(builder: flatbuffers.Builder, movedLocsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} spawnedBodiesOffset
             */
            static addSpawnedBodies(builder: flatbuffers.Builder, spawnedBodiesOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} spawnedBulletsOffset
             */
            static addSpawnedBullets(builder: flatbuffers.Builder, spawnedBulletsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} healthChangedIDsOffset
             */
            static addHealthChangedIDs(builder: flatbuffers.Builder, healthChangedIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createHealthChangedIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startHealthChangedIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} healthChangeLevelsOffset
             */
            static addHealthChangeLevels(builder: flatbuffers.Builder, healthChangeLevelsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createHealthChangeLevelsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startHealthChangeLevelsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} diedIDsOffset
             */
            static addDiedIDs(builder: flatbuffers.Builder, diedIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createDiedIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startDiedIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} diedBulletIDsOffset
             */
            static addDiedBulletIDs(builder: flatbuffers.Builder, diedBulletIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createDiedBulletIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startDiedBulletIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} actionIDsOffset
             */
            static addActionIDs(builder: flatbuffers.Builder, actionIDsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createActionIDsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startActionIDsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} actionsOffset
             */
            static addActions(builder: flatbuffers.Builder, actionsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<schema.Action>} data
             * @returns {flatbuffers.Offset}
             */
            static createActionsVector(builder: flatbuffers.Builder, data: schema.Action[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startActionsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} actionTargetsOffset
             */
            static addActionTargets(builder: flatbuffers.Builder, actionTargetsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createActionTargetsVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startActionTargetsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} roundID
             */
            static addRoundID(builder: flatbuffers.Builder, roundID: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endRound(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * Necessary due to flatbuffers requiring unions to be wrapped in tables.
         *
         * @constructor
         */
        export class EventWrapper {
            /**
             * Necessary due to flatbuffers requiring unions to be wrapped in tables.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.EventWrapper}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.EventWrapper;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.EventWrapper=} obj
             * @returns {schema.EventWrapper}
             */
            static getRootAsEventWrapper(bb: flatbuffers.ByteBuffer, obj?: schema.EventWrapper): schema.EventWrapper;

            /**
             * @returns {schema.Event}
             */
            eType(): schema.Event;

            /**
             * @param obj
             * @returns {GameHeader|MatchHeader|Round|MatchFooter|GameFooter}
             */
            e(obj: GameHeader | MatchHeader | Round | MatchFooter | GameFooter): GameHeader | MatchHeader | Round | MatchFooter | GameFooter;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startEventWrapper(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {schema.Event} eType
             */
            static addEType(builder: flatbuffers.Builder, eType: schema.Event): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} eOffset
             */
            static addE(builder: flatbuffers.Builder, eOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endEventWrapper(builder: flatbuffers.Builder): flatbuffers.Offset;

        }

        /**
         * If events are not otherwise delimited, this wrapper structure
         * allows a game to be stored in a single buffer.
         * The first event will be a GameHeader; the last event will be a GameFooter.
         * matchHeaders[0] is the index of the 0th match header in the event stream,
         * corresponding to matchFooters[0]. These indices allow quick traversal of
         * the file.
         *
         * @constructor
         */
        export class GameWrapper {
            /**
             * If events are not otherwise delimited, this wrapper structure
             * allows a game to be stored in a single buffer.
             * The first event will be a GameHeader; the last event will be a GameFooter.
             * matchHeaders[0] is the index of the 0th match header in the event stream,
             * corresponding to matchFooters[0]. These indices allow quick traversal of
             * the file.
             *
             * @constructor
             */
            constructor();

            /**
             * @type {flatbuffers.ByteBuffer}
             */
            bb: flatbuffers.ByteBuffer;

            /**
             * @type {number}
             */
            bb_pos: number;

            /**
             * @param {number} i
             * @param {flatbuffers.ByteBuffer} bb
             * @returns {schema.GameWrapper}
             */
            __init(i: number, bb: flatbuffers.ByteBuffer): schema.GameWrapper;

            /**
             * @param {flatbuffers.ByteBuffer} bb
             * @param {schema.GameWrapper=} obj
             * @returns {schema.GameWrapper}
             */
            static getRootAsGameWrapper(bb: flatbuffers.ByteBuffer, obj?: schema.GameWrapper): schema.GameWrapper;

            /**
             * The series of events comprising the game.
             *
             * @param {number} index
             * @param {schema.EventWrapper=} obj
             * @returns {schema.EventWrapper}
             */
            events(index: number, obj?: schema.EventWrapper): schema.EventWrapper;

            /**
             * @returns {number}
             */
            eventsLength(): number;

            /**
             * The indices of the headers of the matches, in order.
             *
             * @param {number} index
             * @returns {number}
             */
            matchHeaders(index: number): number;

            /**
             * @returns {number}
             */
            matchHeadersLength(): number;

            /**
             * @returns {Int32Array}
             */
            matchHeadersArray(): Int32Array;

            /**
             * The indices of the footers of the matches, in order.
             *
             * @param {number} index
             * @returns {number}
             */
            matchFooters(index: number): number;

            /**
             * @returns {number}
             */
            matchFootersLength(): number;

            /**
             * @returns {Int32Array}
             */
            matchFootersArray(): Int32Array;

            /**
             * @param {flatbuffers.Builder} builder
             */
            static startGameWrapper(builder: flatbuffers.Builder): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} eventsOffset
             */
            static addEvents(builder: flatbuffers.Builder, eventsOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<flatbuffers.Offset>} data
             * @returns {flatbuffers.Offset}
             */
            static createEventsVector(builder: flatbuffers.Builder, data: flatbuffers.Offset[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startEventsVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} matchHeadersOffset
             */
            static addMatchHeaders(builder: flatbuffers.Builder, matchHeadersOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createMatchHeadersVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startMatchHeadersVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {flatbuffers.Offset} matchFootersOffset
             */
            static addMatchFooters(builder: flatbuffers.Builder, matchFootersOffset: flatbuffers.Offset): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {Array.<number>} data
             * @returns {flatbuffers.Offset}
             */
            static createMatchFootersVector(builder: flatbuffers.Builder, data: number[]): flatbuffers.Offset;

            /**
             * @param {flatbuffers.Builder} builder
             * @param {number} numElems
             */
            static startMatchFootersVector(builder: flatbuffers.Builder, numElems: number): void;

            /**
             * @param {flatbuffers.Builder} builder
             * @returns {flatbuffers.Offset}
             */
            static endGameWrapper(builder: flatbuffers.Builder): flatbuffers.Offset;
        }
    }
}

export = index;
