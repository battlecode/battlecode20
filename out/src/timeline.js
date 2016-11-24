"use strict";
var gameworld_1 = require('./gameworld');
// Return a timestamp representing the current time in ms, not necessarily from
// any particular epoch.
var timeMS = window && window.performance && window.performance.now ?
    window.performance.now.bind(window.performance) : Date.now.bind(Date);
/**
 * A timeline of a match. Allows you to see what the state of the match was,
 * at any particular time.
 *
 * Call timeline.seek() to request the state of the world at a particular
 * time; then call timeline.compute() to allow the timeline to perform computations.
 *
 * Note that this API is a state machine: you call methods on it,
 * then call compute() to let it do its thing, and then inspect it
 * to see what its current state is.
 *
 * This is awkward, but less so than callbacks.
 */
var Timeline = (function () {
    function Timeline(header, meta) {
        this.current = new gameworld_1.default(meta);
        this.current.loadFromMatchHeader(header);
        this._farthest = this.current.copy();
        this.snapshots = new Array();
        this.snapshots.push(this.current.copy());
        // leave [0] undefined
        this.deltas = new Array(1);
        this.seekTo = 0;
    }
    /**
     * Move this.current to the targeted round.
     * Return whether or not it is possible to seek to that round;
     * if we don't have deltas to it, we can't.
     */
    Timeline.prototype.seek = function (round) {
        // the last delta we have is this.deltas.length-1, which takes us to turn
        // this.deltas.length-1; if we're higher than that, we can't seek
        if (round > this.deltas.length - 1) {
            this.seekTo = round;
            return true;
        }
        return false;
    };
    /**
     * Perform computations for at least timeLimit ms.
     */
    Timeline.prototype.compute = function (timeLimit) {
        if (timeLimit === void 0) { timeLimit = 5; }
        var start = timeMS();
        // Once we hit our soft limit, stop computing.
        while (timeMS() < start + timeLimit) {
            // First, perform any seeking we need to do.
            // We're guaranteed to have enough rounds to seek to our goal.
            if (this.current.turn !== this.seekTo) {
                if (this.current.turn === this.seekTo - 1) {
                    // shortcut; just step once.
                    this.current.processRound(this.deltas[this.current.turn + 1]);
                }
                else {
                }
            }
            else {
            }
        }
    };
    return Timeline;
}());
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = Timeline;
