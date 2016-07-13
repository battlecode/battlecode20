import {schema, flatbuffers} from 'battlecode-schema';

/**
 * A custom vector class.
 * Supports operations that operate on values from a flatbuffer; these have the
 * suffix 'Flat'.
 * All methods that return 'this' mutate!
 * Prefer mutating existing vectors to creating new ones, as it isn't free.
 */
export default class Vec {
  public x: number;
  public y: number;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }

  /**
   * Copy the vector.
   */
  copy(): Vec {
    return new Vec(this.x, this.y);
  }

  /**
   * Construct from a flatbuffers Vec.
   */
  static fromFlat(v: schema.Vec): Vec {
    return new Vec(v.x(), v.y());
  }

  /**
   * Export to a builder.
   */
  toFlat(out: flatbuffers.Builder) {
    schema.Vec.createVec(out, this.x, this.y);
  }

  /**
   * Construct from polar coordinates.
   * @param angle angle from +X axis in radians
   * @param mag magnitude
   */
  static fromPolar(angle: number, mag: number) {
    return new Vec(Math.cos(angle) * mag, Math.sin(angle) * mag);
  }

  toString(): string {
    return `Vec(${this.x},${this.y})`;
  }

  // Query methods

  /**
   * Get the length of the vector.
   */
  length(): number {
    return Math.sqrt(this.x * this.x + this.y * this.y);
  }

  /**
   * Length squared, battlecode-style.
   */
  lengthSq(): number {
    return this.x * this.x + this.y * this.y
  }

  /**
   * Get angle from +X axis in radians.
   */
  angle(): number {
    return Math.atan2(this.y, this.x);
  }

  /**
   * Determine whether we are the 0 vector.
   */
  isZero(): boolean {
    return this.x === 0 && this.y == 0;
  }

  // Vector-Vector operations
  // Note that these all have a 'Flat' counterpart.

  /**
   * Add a vector to ourself. MUTATES.
   * @return this
   */
  add(v: Vec): Vec {
    this.x += v.x;
    this.y += v.y;
    return this;
  }

  /**
   * Add a flat vector to ourself. MUTATES.
   * @return this
   */
  addFlat(v: schema.Vec): Vec {
    this.x += v.x();
    this.y += v.y();
    return this;
  }

  /**
   * Subtract a vector from ourself. MUTATES.
   * @return this
   */
  sub(v: Vec): Vec {
    this.x -= v.x;
    this.y -= v.y;
    return this;
  }

  /**
   * Subtract a flat vector from ourself. MUTATES.
   * @return this
   */
  subFlat(v: schema.Vec): Vec {
    this.x -= v.x();
    this.y -= v.y();
    return this;
  }

  /**
   * Perform a linear interpolation between two vectors. MUTATES
   * @param amt between 0 and 1; 0 for this vector, 1 for that vector.
   *            .5 by default.
   * @return this
   */
  mix(v: Vec, amt: number=.5): Vec {
    this.x = (1-amt)*this.x + amt*v.x;
    this.y = (1-amt)*this.y + amt*v.y;
    return this;
  }

  /**
   * Perform a linear interpolation between two vectors. MUTATES
   * @param amt between 0 and 1; 0 for this vector, 1 for that vector.
   *            .5 by default.
   * @return this
   */
  mixFlat(v: schema.Vec, amt: number=.5): Vec {
    this.x = (1-amt)*this.x + amt*v.x();
    this.y = (1-amt)*this.y + amt*v.y();
    return this;
  }

  /**
   * Dot product.
   */
  dot(v: Vec): number {
    return this.x * v.x + this.y * v.y;
  }

  /**
   * Dot product with Flat vector.
   */
  dotFlat(v: schema.Vec): number {
    return this.x * v.x() + this.y * v.y();
  }

  /**
   * Cross product; note that we treat both vectors as 3d and return only the
   * Z component.
   */
  cross(v: Vec): number {
    return (this.x * v.y) - (this.y * v.x);
  }

  /**
   * Cross product with flat vector; note that we treat both vectors as 3d and
   * return only the Z component.
   */
  crossFlat(v: schema.Vec): number {
    return (this.x * v.y()) - (this.y * v.x());
  }

  /**
   * The angle to another vector, in radians, counterclockwise,
   * between -pi and pi.
   * a.rotate(a.angleTo(b)) === b, barring floating point errors.
   */
  angleTo(v: Vec): number {
    return Math.atan2(v.y, v.x) - Math.atan2(this.y, this.x);
  }

  /**
   * The angle to another vector, in radians, counterclockwise,
   * between -pi and pi.
   * a.rotate(a.angleTo(b)) === b, barring floating point errors.
   */
  angleToFlat(v: schema.Vec): number {
    return Math.atan2(v.y(), v.x()) - Math.atan2(this.y, this.x);
  }

  /**
   * Return this distance to another vector.
   */
  dist(v: Vec): number {
    let x = this.x - v.x;
    let y = this.y - v.y;
    return Math.sqrt(x * x + y * y);
  }

  /**
   * Return this distance to a flat vector.
   */
  distFlat(v: schema.Vec): number {
    let x = this.x - v.x();
    let y = this.y - v.y();
    return Math.sqrt(x * x + y * y);
  }

  /**
   * Return this distance squared to another vector.
   */
  distSq(v: Vec): number {
    let x = this.x - v.x;
    let y = this.y - v.y;
    return x * x + y * y;
  }

  /**
   * Return this distance squared to a flat vector.
   */
  distSqFlat(v: schema.Vec): number {
    let x = this.x - v.x();
    let y = this.y - v.y();
    return x * x + y * y;
  }


  // Scalar operations

  /**
   * Multiply ourself by a scalar. MUTATES.
   * @return this
   */
  mult(s: number): Vec {
    this.x *= s;
    this.y *= s;
    return this;
  }

  /**
   * Divide ourself by a scalar. MUTATES.
   * @return this
   */
  div(s: number) {
    this.x /= s;
    this.y /= s;
  }


  /**
   * Rotate ourself, counterclockwise, in radians, by theta. MUTATES.
   * @return this
   */
  rotate(theta: number): Vec {
    let cos = Math.cos(theta);
    let sin = Math.sin(theta);
    this.x = this.x * cos - this.y * sin;
    this.y = this.x * sin + this.y * cos;
    return this;
  }

  /**
   * Bound our length to be between min and max. MUTATES.
   * @return this
   */
  bound(min: number, max: number): Vec {
    let len = this.length();
    if (len < min) {
      this.norm().mult(min);
    } else if (len > max) {
      this.norm().mult(max);
    }
    return this;
  }

  // Operations on ourself.

  /**
   * Normalize ourself to length 1. MUTATES.
   * @return this
   */
  norm(): Vec {
    let len = this.length();

    this.x /= len;
    this.y /= len;

    return this;
  }

  /**
   * Invert this vector. MUTATES.
   * @return this
   */
  invert(): Vec {
    this.x = 1 / this.x;
    this.y = 1 / this.y;

    return this;
  }
}
