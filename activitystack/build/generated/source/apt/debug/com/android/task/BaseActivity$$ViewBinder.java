// Generated code from Butter Knife. Do not modify!
package com.android.task;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class BaseActivity$$ViewBinder<T extends BaseActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131427415, "method 'onClick'");
    unbinder.view2131427415 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427416, "method 'onClick'");
    unbinder.view2131427416 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427417, "method 'onClick'");
    unbinder.view2131427417 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427418, "method 'onClick'");
    unbinder.view2131427418 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427421, "method 'onClick'");
    unbinder.view2131427421 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427422, "method 'onClick'");
    unbinder.view2131427422 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427424, "method 'onClick'");
    unbinder.view2131427424 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427423, "method 'onClick'");
    unbinder.view2131427423 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131427425, "method 'onClick'");
    unbinder.view2131427425 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends BaseActivity> implements Unbinder {
    private T target;

    View view2131427415;

    View view2131427416;

    View view2131427417;

    View view2131427418;

    View view2131427421;

    View view2131427422;

    View view2131427424;

    View view2131427423;

    View view2131427425;

    protected InnerUnbinder(T target) {
      this.target = target;
    }

    @Override
    public final void unbind() {
      if (target == null) throw new IllegalStateException("Bindings already cleared.");
      unbind(target);
      target = null;
    }

    protected void unbind(T target) {
      view2131427415.setOnClickListener(null);
      view2131427416.setOnClickListener(null);
      view2131427417.setOnClickListener(null);
      view2131427418.setOnClickListener(null);
      view2131427421.setOnClickListener(null);
      view2131427422.setOnClickListener(null);
      view2131427424.setOnClickListener(null);
      view2131427423.setOnClickListener(null);
      view2131427425.setOnClickListener(null);
    }
  }
}
