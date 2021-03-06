package org.scalaide.typertimings.weaving.scalac;

import scala.reflect.internal.Types.LazyType;
import scala.reflect.internal.Types.Type;
import scala.reflect.internal.Symbols.Symbol;

import java.util.Map;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ArrayList;

import scala.tools.nsc.Global;
import scala.tools.nsc.reporters.Reporter;
import scala.reflect.api.Position;
import scala.reflect.internal.Trees.Tree;
import scala.reflect.internal.Trees.DefDef;
import scala.tools.nsc.typechecker.Typers.Typer;
import scala.reflect.internal.SymbolTable;
import scala.reflect.internal.Types;
import scala.reflect.internal.util.NoPosition;

/** 
 * This is a quick and dirty attempt to time a few things in Scala compiler.
 * At the moment the list includes:
 *
 *  - LazyType.complete(..)
 *  - Typer.typedDefDef(..)
 *  - Types.lub(..)
 *  - Types.glb(..)
 *
 * All times are reported in microseconds.
 *
 * NOTE: This aspect has strictly more functionality tha TypeCompletionTiming. I keep
 * both because this one is more powerful and the other one has cleaner implementation.
 * Eventually, this should be cleaned up.
 */
public aspect TyperTimings {
  public final long globalStartTime = System.nanoTime();
  public List<Trace> traces = new java.util.ArrayList<Trace>();
  
  private TracingListeners registry = new TracingListeners();
  
  private void addTrace(Trace t) {
    if (t.pos != null && t.pos.isDefined()) {
      traces.add(t);
    }
  }

  /** IdentityHashMap (that is used as IdentityHashSet) that allows us to detect recursive
    * calls to LazyType.complete(..) and avoid double bookkeeping. */
  private Map<LazyType, Object> currentlyCompleted = new IdentityHashMap<LazyType, Object>();

  void around(LazyType lazyTpe, Symbol sym): call(void Type.complete(..)) && 
    target(lazyTpe) && args(sym) {
    if (!currentlyCompleted.containsKey(lazyTpe)) {
      String traceName = lazyTpe.getClass().getSimpleName();
      currentlyCompleted.put(lazyTpe, null);
      long start = System.nanoTime();
      try {
        proceed(lazyTpe, sym);
      } finally {
        long duration = System.nanoTime()-start;
        Trace trace = new Trace(sym.pos(), sym.fullNameString(), start, duration, traceName);
        addTrace(trace);
        currentlyCompleted.remove(lazyTpe);
      }
    } else {
      proceed(lazyTpe, sym);
    }
  }

  /** IdentityHashMap (that is used as IdentityHashSet) that allows us to detect recursive
    * calls to typed* methods and avoid double bookkeeping. */
  private Map<Tree, Object> currentlyTyped = new IdentityHashMap<Tree, Object>();

  DefDef around(Typer typer, DefDef tree): call(DefDef Typer.typedDefDef(..)) && 
    target(typer) && args(tree) {
    DefDef result = null;
    if (!currentlyTyped.containsKey(tree)) {
      String traceName = "typedDefDef";
      currentlyTyped.put(tree, null);
      long start = System.nanoTime();
      try {
        result = proceed(typer, tree);
      } finally {
        long duration = System.nanoTime()-start;
        Trace trace = new Trace(tree.symbol().pos(), tree.symbol().fullNameString(), start, duration, traceName);
        addTrace(trace);
        currentlyTyped.remove(tree);
      }
    } else {
      result = proceed(typer, tree);
    }
    return result;
  }

  Type around(SymbolTable symbolTable, scala.collection.immutable.List<Type> ts): call(Type Types.lub(..)) && 
    target(symbolTable) && args(ts) {
    Type result = null;
    String traceName = "lub";
    long start = System.nanoTime();
    try {
      result = proceed(symbolTable, ts);
    } finally {
      long duration = System.nanoTime()-start;
      Trace trace = new Trace(null, symbolTable.NoSymbol().fullNameString(), start, duration, traceName);
      addTrace(trace);
    }
    return result;
  }  

  Type around(SymbolTable symbolTable, scala.collection.immutable.List<Type> ts): call(Type Types.glb(..)) && 
    target(symbolTable) && args(ts) {
    Type result = null;
    String traceName = "glb";
    long start = System.nanoTime();
    try {
      result = proceed(symbolTable, ts);
    } finally {
      long duration = System.nanoTime()-start;
      Trace trace = new Trace(null, symbolTable.NoSymbol().fullNameString(), start, duration, traceName);
      addTrace(trace);
    }
    return result;
  }

  after(Global.Run run): execution(private void scala.tools.nsc.Global.Run.compileUnitsInternal(..)) && this(run) {
    Global global = run.$outer;
    Reporter reporter = global.reporter();
    StringBuilder buf = new StringBuilder();
    buf.append(String.format("Per-symbol lazy type completion timings (time-stamps in micro seconds normalized against globalStartTime), globalStartTime = %d", globalStartTime));
    buf.append("\n");
    for (Trace trace : traces) {
      long durationMicro = trace.duration / 1000;
      long startTimeMicro = (trace.startTime-globalStartTime) / 1000;
      final Position pos = trace.pos;
      final String traceName = trace.traceName;
      // startTime has 10 characters reserved, which gives us max compilation running time 10^-6*10^10 = 10^4 seconds ~ 166 minutes
      // duration has 8 characters reserved, which gives us max duration of type completion to be 10^-6*10^8 = 10^2 seconds
      buf.append(String.format("%10d\t%8d\t%-16s\t%-80s\t%s\t", startTimeMicro, durationMicro, traceName,
        trace.symFullName, pos.toString()));
      buf.append("\n");
      publishTrace(trace);
    }
    endTrace();
    System.out.println(buf.toString());
    traces.clear();
  }

  private void publishTrace(Trace t) {
    for (ITraceListener l: registry.getProviders()) {
      l.trace(t);
    }
  }

  private void endTrace() {
    for (ITraceListener l: registry.getProviders()) {
      l.endTracing();
    }
  }
}
