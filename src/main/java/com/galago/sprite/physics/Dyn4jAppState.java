package com.galago.sprite.physics;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Capacity;
import org.dyn4j.dynamics.Settings;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author nickidebruyn
 */
public class Dyn4jAppState extends AbstractAppState {

  private static final long TIME_STEP_IN_MICROSECONDS = (long) (Settings.DEFAULT_STEP_FREQUENCY * 1000);
  /**
   * See {@link Application} for details.
   */
  protected Application app = null;
  protected AppStateManager stateManager = null;
  protected Capacity initialCapacity = null;
  protected Bounds bounds = null;
  protected PhysicsSpace physicsSpace = null;
  protected float tpf = 0;
  protected float tpfSum = 0;
  // MultiTreading Fields
  protected ThreadingType threadingType = null;
  protected ScheduledThreadPoolExecutor executor;
  private final Runnable parallelPhysicsUpdate = new Runnable() {
    @Override
    public void run() {
      if (!isEnabled()) {
        return;
      }

      Dyn4jAppState.this.physicsSpace.updateFixed(Dyn4jAppState.this.tpfSum);
      Dyn4jAppState.this.tpfSum = 0;
    }
  };

  public Dyn4jAppState() {
    this(null, null, ThreadingType.PARALLEL);
  }

  public Dyn4jAppState(final Bounds bounds) {
    this(null, bounds, ThreadingType.PARALLEL);
  }

  public Dyn4jAppState(final Capacity initialCapacity) {
    this(initialCapacity, null, ThreadingType.PARALLEL);
  }

  public Dyn4jAppState(final Capacity initialCapacity, final Bounds bounds) {
    this(initialCapacity, bounds, ThreadingType.PARALLEL);
  }

  public Dyn4jAppState(final ThreadingType threadingType) {
    this(null, null, threadingType);
  }

  public Dyn4jAppState(final Bounds bounds, final ThreadingType threadingType) {
    this(null, bounds, threadingType);
  }

  public Dyn4jAppState(final Capacity initialCapacity, final ThreadingType threadingType) {
    this(initialCapacity, null, threadingType);
  }

  public Dyn4jAppState(final Capacity initialCapacity, final Bounds bounds, final ThreadingType threadingType) {
    this.threadingType = threadingType;
    this.initialCapacity = initialCapacity;
    this.bounds = bounds;
  }

  @Override
  public void initialize(final AppStateManager stateManager, final Application app) {
    this.app = app;
    this.stateManager = stateManager;

    // Start physic related objects.
    startPhysics();

    super.initialize(stateManager, app);
  }

  private void startPhysics() {
    if (this.initialized) {
      return;
    }

    if (this.threadingType == ThreadingType.PARALLEL) {
      startPhysicsOnExecutor();
    } else {
      this.physicsSpace = new PhysicsSpace(this.initialCapacity, this.bounds);
    }

    this.initialized = true;
  }

  private void startPhysicsOnExecutor() {
    if (this.executor != null) {
      this.executor.shutdown();
    }
    this.executor = new ScheduledThreadPoolExecutor(1);

    final Callable<Boolean> call = new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        Dyn4jAppState.this.physicsSpace = new PhysicsSpace(Dyn4jAppState.this.initialCapacity,
                Dyn4jAppState.this.bounds);
        return true;
      }
    };

    try {
      this.executor.submit(call).get();
    } catch (final Exception ex) {
      Logger.getLogger(Dyn4jAppState.class.getName()).log(Level.SEVERE, null, ex);
    }

    schedulePhysicsCalculationTask();
  }

  private void schedulePhysicsCalculationTask() {
    if (this.executor != null) {
      this.executor.scheduleAtFixedRate(this.parallelPhysicsUpdate, 0l, TIME_STEP_IN_MICROSECONDS,
              TimeUnit.MICROSECONDS);
    }
  }

  // FIXME Remove this method but check how to implement tests
  @Override
  public void stateAttached(final AppStateManager stateManager) {
    // Start physic related objects if appState is not initialized.
    if (!this.initialized) {
      startPhysics();
    }

    super.stateAttached(stateManager);
  }

  /**
   * See {@link AppStateManager#update(float)}. Note: update method is not
   * called if enabled = false.
   */
  @Override
  public void update(final float tpf) {
    if (!isEnabled()) {
      return;
    }

    this.tpf = tpf;
    this.tpfSum += tpf;
  }

  /**
   * See {@link AppStateManager#render(RenderManager)}. Note: render method is
   * not called if enabled = false.
   */
  @Override
  public void render(final RenderManager rm) {

    if (threadingType == ThreadingType.PARALLEL) {
      executor.submit(parallelPhysicsUpdate);

    } else if (threadingType == ThreadingType.SEQUENTIAL) {
      final float timeStep = isEnabled() ? this.tpf * this.physicsSpace.getSpeed() : 0;
      this.physicsSpace.updateFixed(timeStep);

    } else {

    }
  }

  @Override
  public void setEnabled(final boolean enabled) {
    if (enabled) {
      schedulePhysicsCalculationTask();

    } else if (this.executor != null) {
      this.executor.remove(this.parallelPhysicsUpdate);
    }
    super.setEnabled(enabled);
  }

  @Override
  public void cleanup() {
    if (this.executor != null) {
      this.executor.shutdown();
      this.executor = null;
    }

    this.physicsSpace.clear();

    super.cleanup();
  }

  public PhysicsSpace getPhysicsSpace() {
    return this.physicsSpace;
  }
}
