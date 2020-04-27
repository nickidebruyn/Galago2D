package com.galago.sprite.physics;

/**
 * @author nickidebruyn
 */
public enum ThreadingType {
  /**
   * Default mode; user update, physics update and rendering happen sequentially (single threaded)
   */
  SEQUENTIAL,
  /**
   * Parallel threaded mode; physics update and rendering are executed in parallel, update order is kept.<br/>
   * Multiple Dyn4jAppStates will execute in parallel in this mode.
   */
  PARALLEL,

}
