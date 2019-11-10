// automatically generated by the FlatBuffers compiler, do not modify

package battlecode.schema;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
/**
 * The map a round is played on.
 */
public final class GameMap extends Table {
  public static GameMap getRootAsGameMap(ByteBuffer _bb) { return getRootAsGameMap(_bb, new GameMap()); }
  public static GameMap getRootAsGameMap(ByteBuffer _bb, GameMap obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public GameMap __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  /**
   * The name of a map.
   */
  public String name() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer nameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer nameInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  /**
   * The bottom corner of the map.
   */
  public Vec minCorner() { return minCorner(new Vec()); }
  public Vec minCorner(Vec obj) { int o = __offset(6); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }
  /**
   * The top corner of the map.
   */
  public Vec maxCorner() { return maxCorner(new Vec()); }
  public Vec maxCorner(Vec obj) { int o = __offset(8); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }
  /**
   * The bodies on the map.
   */
  public SpawnedBodyTable bodies() { return bodies(new SpawnedBodyTable()); }
  public SpawnedBodyTable bodies(SpawnedBodyTable obj) { int o = __offset(10); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  /**
   * The random seed of the map.
   */
  public int randomSeed() { int o = __offset(12); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  /**
   * The dirt levels.
   */
  public int dirt(int j) { int o = __offset(14); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int dirtLength() { int o = __offset(14); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer dirtAsByteBuffer() { return __vector_as_bytebuffer(14, 4); }
  public ByteBuffer dirtInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 14, 4); }
  /**
   * The water levels (above dirt).
   */
  public int water(int j) { int o = __offset(16); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int waterLength() { int o = __offset(16); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer waterAsByteBuffer() { return __vector_as_bytebuffer(16, 4); }
  public ByteBuffer waterInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 16, 4); }
  /**
   * The pollution levels.
   */
  public int pollution(int j) { int o = __offset(18); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int pollutionLength() { int o = __offset(18); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer pollutionAsByteBuffer() { return __vector_as_bytebuffer(18, 4); }
  public ByteBuffer pollutionInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 18, 4); }
  /**
   * The amount of crude soup to be mined.
   */
  public int soup(int j) { int o = __offset(20); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int soupLength() { int o = __offset(20); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer soupAsByteBuffer() { return __vector_as_bytebuffer(20, 4); }
  public ByteBuffer soupInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 20, 4); }

  public static void startGameMap(FlatBufferBuilder builder) { builder.startObject(9); }
  public static void addName(FlatBufferBuilder builder, int nameOffset) { builder.addOffset(0, nameOffset, 0); }
  public static void addMinCorner(FlatBufferBuilder builder, int minCornerOffset) { builder.addStruct(1, minCornerOffset, 0); }
  public static void addMaxCorner(FlatBufferBuilder builder, int maxCornerOffset) { builder.addStruct(2, maxCornerOffset, 0); }
  public static void addBodies(FlatBufferBuilder builder, int bodiesOffset) { builder.addOffset(3, bodiesOffset, 0); }
  public static void addRandomSeed(FlatBufferBuilder builder, int randomSeed) { builder.addInt(4, randomSeed, 0); }
  public static void addDirt(FlatBufferBuilder builder, int dirtOffset) { builder.addOffset(5, dirtOffset, 0); }
  public static int createDirtVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startDirtVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addWater(FlatBufferBuilder builder, int waterOffset) { builder.addOffset(6, waterOffset, 0); }
  public static int createWaterVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startWaterVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addPollution(FlatBufferBuilder builder, int pollutionOffset) { builder.addOffset(7, pollutionOffset, 0); }
  public static int createPollutionVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startPollutionVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addSoup(FlatBufferBuilder builder, int soupOffset) { builder.addOffset(8, soupOffset, 0); }
  public static int createSoupVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startSoupVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endGameMap(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
