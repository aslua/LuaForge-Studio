local bindClass = luajava.bindClass
local CoordinatorLayout = bindClass "androidx.coordinatorlayout.widget.CoordinatorLayout"
local AppBarLayout = bindClass "com.google.android.material.appbar.AppBarLayout"
local MaterialToolbar = bindClass "com.google.android.material.appbar.MaterialToolbar"
local LinearLayoutCompat = bindClass "androidx.appcompat.widget.LinearLayoutCompat"
local Colors = require "Colors"

return {
  CoordinatorLayout,
  layout_width = -1,
  layout_height = -1,
  {
    AppBarLayout,
    fitsSystemWindows = true,
    layout_width = -1,
    {
      MaterialToolbar,
      id = "toolbar",
      layout_width = -1,
      backgroundColor = Colors.colorBackground,
      title = "布局助手",
      layout_scrollFlags = 3,
      layout_height = actionBarSize() + dp2px(8),
    },
  },
  {
    LinearLayoutCompat,
    orientation = "vertical",
    layout_behavior = "appbar_scrolling_view_behavior",
    layout_width = -1,
    layout_height = -1,
    layout_margin = "18dp",
    id = "root",
  },
}