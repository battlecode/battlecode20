// automatically generated by the FlatBuffers compiler, do not modify

package battlecode.schema;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
/**
 * A list of neutral trees to be placed on the map.
 */
public final class NeutralTreeTable extends Table {
  public static NeutralTreeTable getRootAsNeutralTreeTable(ByteBuffer _bb) { return getRootAsNeutralTreeTable(_bb, new NeutralTreeTable()); }
  public static NeutralTreeTable getRootAsNeutralTreeTable(ByteBuffer _bb, NeutralTreeTable obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public NeutralTreeTable __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  /**
   * The IDs of the trees.
   */
  public int robotIDs(int j) { int o = __offset(4); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int robotIDsLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer robotIDsAsByteBuffer() { return __vector_as_bytebuffer(4, 4); }
  /**
   * The locations of the trees.
   */
  public VecTable locs() { return locs(new VecTable()); }
  public VecTable locs(VecTable obj) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }
  /**
   * The radii of the trees.
   */
  public float radii(int j) { int o = __offset(8); return o != 0 ? bb.getFloat(__vector(o) + j * 4) : 0; }
  public int radiiLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer radiiAsByteBuffer() { return __vector_as_bytebuffer(8, 4); }
  /**
   * The healths of the trees.
   */
  public float healths(int j) { int o = __offset(10); return o != 0 ? bb.getFloat(__vector(o) + j * 4) : 0; }
  public int healthsLength() { int o = __offset(10); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer healthsAsByteBuffer() { return __vector_as_bytebuffer(10, 4); }
  /**
   * The bullets contained within the trees.
   */
  public int containedBullets(int j) { int o = __offset(12); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int containedBulletsLength() { int o = __offset(12); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer containedBulletsAsByteBuffer() { return __vector_as_bytebuffer(12, 4); }
  /**
   * The bodies contained within the trees.
   */
  public byte containedBodies(int j) { int o = __offset(14); return o != 0 ? bb.get(__vector(o) + j * 1) : 0; }
  public int containedBodiesLength() { int o = __offset(14); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer containedBodiesAsByteBuffer() { return __vector_as_bytebuffer(14, 1); }

  public static int createNeutralTreeTable(FlatBufferBuilder builder,
      int robotIDsOffset,
      int locsOffset,
      int radiiOffset,
      int healthsOffset,
      int containedBulletsOffset,
      int containedBodiesOffset) {
    builder.startObject(6);
    NeutralTreeTable.addContainedBodies(builder, containedBodiesOffset);
    NeutralTreeTable.addContainedBullets(builder, containedBulletsOffset);
    NeutralTreeTable.addHealths(builder, healthsOffset);
    NeutralTreeTable.addRadii(builder, radiiOffset);
    NeutralTreeTable.addLocs(builder, locsOffset);
    NeutralTreeTable.addRobotIDs(builder, robotIDsOffset);
    return NeutralTreeTable.endNeutralTreeTable(builder);
  }

  public static void startNeutralTreeTable(FlatBufferBuilder builder) { builder.startObject(6); }
  public static void addRobotIDs(FlatBufferBuilder builder, int robotIDsOffset) { builder.addOffset(0, robotIDsOffset, 0); }
  public static int createRobotIDsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startRobotIDsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addLocs(FlatBufferBuilder builder, int locsOffset) { builder.addOffset(1, locsOffset, 0); }
  public static void addRadii(FlatBufferBuilder builder, int radiiOffset) { builder.addOffset(2, radiiOffset, 0); }
  public static int createRadiiVector(FlatBufferBuilder builder, float[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addFloat(data[i]); return builder.endVector(); }
  public static void startRadiiVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addHealths(FlatBufferBuilder builder, int healthsOffset) { builder.addOffset(3, healthsOffset, 0); }
  public static int createHealthsVector(FlatBufferBuilder builder, float[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addFloat(data[i]); return builder.endVector(); }
  public static void startHealthsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addContainedBullets(FlatBufferBuilder builder, int containedBulletsOffset) { builder.addOffset(4, containedBulletsOffset, 0); }
  public static int createContainedBulletsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startContainedBulletsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addContainedBodies(FlatBufferBuilder builder, int containedBodiesOffset) { builder.addOffset(5, containedBodiesOffset, 0); }
  public static int createContainedBodiesVector(FlatBufferBuilder builder, byte[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addByte(data[i]); return builder.endVector(); }
  public static void startContainedBodiesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static int endNeutralTreeTable(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

